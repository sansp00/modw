<#
.SYNOPSIS
    ModW installer.
.DESCRIPTION
    The installer of ModW. For details please check the website and wiki.
.PARAMETER ModWDir
    Specifies ModW root path.
    If not specified, ModW will be installed to '$env:USERPROFILE\modw'.
.PARAMETER NoProxy
    Bypass system proxy during the installation.
.PARAMETER Proxy
    Specifies proxy to use during the installation.
.PARAMETER ProxyCredential
    Specifies credential for the given proxy.
.PARAMETER ProxyUseDefaultCredentials
    Use the credentials of the current user for the proxy server that is specified by the -Proxy parameter.
.PARAMETER RunAsAdmin
    Force to run the installer as administrator.
#>
param(
    [String] $ModWDir,
    [Switch] $NoProxy,
    [Uri] $Proxy,
    [System.Management.Automation.PSCredential] $ProxyCredential,
    [Switch] $ProxyUseDefaultCredentials,
    [Switch] $RunAsAdmin
)

# Disable StrictMode in this script
Set-StrictMode -Off

function Write-InstallInfo {
    param(
        [Parameter(Mandatory = $True, Position = 0)]
        [String] $String,
        [Parameter(Mandatory = $False, Position = 1)]
        [System.ConsoleColor] $ForegroundColor = $host.UI.RawUI.ForegroundColor
    )

    $backup = $host.UI.RawUI.ForegroundColor

    if ($ForegroundColor -ne $host.UI.RawUI.ForegroundColor) {
        $host.UI.RawUI.ForegroundColor = $ForegroundColor
    }

    Write-Output "$String"

    $host.UI.RawUI.ForegroundColor = $backup
}

function Deny-Install {
    param(
        [String] $message,
        [Int] $errorCode = 1
    )

    Write-InstallInfo -String $message -ForegroundColor DarkRed
    Write-InstallInfo 'Abort.'

    # Don't abort if invoked with iex that would close the PS session
    if ($IS_EXECUTED_FROM_IEX) {
        break
    } else {
        exit $errorCode
    }
}

function Test-LanguageMode {
    if ($ExecutionContext.SessionState.LanguageMode -ne 'FullLanguage') {
        Write-Output 'ModW requires PowerShell FullLanguage mode to run, current PowerShell environment is restricted.'
        Write-Output 'Abort.'

        if ($IS_EXECUTED_FROM_IEX) {
            break
        } else {
            exit $errorCode
        }
    }
}

function Test-ValidateParameter {
    if ($null -eq $Proxy -and ($null -ne $ProxyCredential -or $ProxyUseDefaultCredentials)) {
        Deny-Install 'Provide a valid proxy URI for the -Proxy parameter when using the -ProxyCredential or -ProxyUseDefaultCredentials.'
    }

    if ($ProxyUseDefaultCredentials -and $null -ne $ProxyCredential) {
        Deny-Install "ProxyUseDefaultCredentials is conflict with ProxyCredential. Don't use the -ProxyCredential and -ProxyUseDefaultCredentials together."
    }
}

function Test-IsAdministrator {
    return ([Security.Principal.WindowsPrincipal]`
            [Security.Principal.WindowsIdentity]::GetCurrent()`
    ).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Test-Prerequisite {
    # ModW requires PowerShell 5 at least
    if (($PSVersionTable.PSVersion.Major) -lt 5) {
        Deny-Install 'PowerShell 5 or later is required to run ModW. Go to https://microsoft.com/powershell to get the latest version of PowerShell.'
    }

    # ModW requires TLS 1.2 SecurityProtocol, which exists in .NET Framework 4.5+
    if ([System.Enum]::GetNames([System.Net.SecurityProtocolType]) -notcontains 'Tls12') {
        Deny-Install 'ModW requires .NET Framework 4.5+ to work. Go to https://microsoft.com/net/download to get the latest version of .NET Framework.'
    }

    # Detect if RunAsAdministrator, there is no need to run as administrator when installing ModW
    if (!$RunAsAdmin -and (Test-IsAdministrator)) {
        # Exception: Windows Sandbox, GitHub Actions CI
        $exception = ($env:USERNAME -eq 'WDAGUtilityAccount') -or ($env:GITHUB_ACTIONS -eq 'true' -and $env:CI -eq 'true')
        if (!$exception) {
            Deny-Install 'Running the installer as administrator is disabled by default.'
        }
    }

    # Show notification to change execution policy
    $allowedExecutionPolicy = @('Unrestricted', 'RemoteSigned', 'ByPass')
    if ((Get-ExecutionPolicy).ToString() -notin $allowedExecutionPolicy) {
        Deny-Install "PowerShell requires an execution policy in [$($allowedExecutionPolicy -join ', ')] to run ModW. For example, to set the execution policy to 'RemoteSigned' please run 'Set-ExecutionPolicy RemoteSigned -Scope CurrentUser'."
    }

    # Test if ModW is installed, by checking if ModW command exists.
    if (Test-CommandAvailable('modw')) {
        Deny-Install "ModW is already installed. Run 'modw -update' to get the latest version."
    }
}


function Test-JavaPrerequisite {
    if (!(Get-Env 'JAVA_HOME')) {
        Deny-Install 'JAVA_HOME environment variable is required to run ModW.'
    }
    $javaHomeEnvPath = Get-Env 'JAVA_HOME'

    $javaExePath = "$javaHomeEnvPath\bin\java.exe"

    if (Test-CommandAvailable("$javaExePath")) {
        Deny-Install "Java is not properly installed."
    }
}

function Test-CommandAvailable {
    param (
        [Parameter(Mandatory = $True, Position = 0)]
        [String] $Command
    )
    return [Boolean](Get-Command $Command -ErrorAction SilentlyContinue)
}

function Test-isFileLocked {
    param(
        [String] $path
    )

    $file = New-Object System.IO.FileInfo $path

    if (!(Test-Path $path)) {
        return $false
    }

    try {
        $stream = $file.Open(
            [System.IO.FileMode]::Open,
            [System.IO.FileAccess]::ReadWrite,
            [System.IO.FileShare]::None
        )
        if ($stream) {
            $stream.Close()
        }
        return $false
    } catch {
        # The file is locked by a process.
        return $true
    }
}


function Get-Env {
    param(
        [String] $name,
        [Switch] $global
    )

    $RegisterKey = if ($global) {
        Get-Item -Path 'HKLM:\SYSTEM\CurrentControlSet\Control\Session Manager'
    } else {
        Get-Item -Path 'HKCU:'
    }

    $EnvRegisterKey = $RegisterKey.OpenSubKey('Environment')
    $RegistryValueOption = [Microsoft.Win32.RegistryValueOptions]::DoNotExpandEnvironmentNames
    $EnvRegisterKey.GetValue($name, $null, $RegistryValueOption)
}

function Publish-Env {
    if (-not ('Win32.NativeMethods' -as [Type])) {
        Add-Type -Namespace Win32 -Name NativeMethods -MemberDefinition @'
[DllImport("user32.dll", SetLastError = true, CharSet = CharSet.Auto)]
public static extern IntPtr SendMessageTimeout(
    IntPtr hWnd, uint Msg, UIntPtr wParam, string lParam,
    uint fuFlags, uint uTimeout, out UIntPtr lpdwResult);
'@
    }

    $HWND_BROADCAST = [IntPtr] 0xffff
    $WM_SETTINGCHANGE = 0x1a
    $result = [UIntPtr]::Zero

    [Win32.Nativemethods]::SendMessageTimeout($HWND_BROADCAST,
        $WM_SETTINGCHANGE,
        [UIntPtr]::Zero,
        'Environment',
        2,
        5000,
        [ref] $result
    ) | Out-Null
}

function Install-ModW {
    Write-InstallInfo 'Initializing...'
    # Validate install parameters
    Test-ValidateParameter
    # Check prerequisites
    Test-Prerequisite
    # Enable TLS 1.2
    Optimize-SecurityProtocol

    # Download ModW from GitHub
    Write-InstallInfo 'Downloading...'
    $downloader = Get-Downloader

    $modwZipfile = "$MODW_APP_DIR\modw.zip"
    if (!(Test-Path $MODW_APP_DIR)) {
        New-Item -Type Directory $MODW_APP_DIR | Out-Null
    }
    Write-Verbose "Downloading $MODW_PACKAGE_REPO to $modwZipfile"
    $downloader.downloadFile($MODW_PACKAGE_REPO, $modwZipfile)

    # Extract files from downloaded zip
    Write-InstallInfo 'Extracting...'
    # 1. extract modw
    $modwUnzipTempDir = "$MODW_APP_DIR\_tmp"
    Write-Verbose "Extracting $modwZipfile to $modwUnzipTempDir"
    Expand-ZipArchive $modwZipfile $modwUnzipTempDir
    Copy-Item "$modwUnzipTempDir\modw-*\*" $MODW_APP_DIR -Recurse -Force

    # Cleanup
    Remove-Item $modwUnzipTempDir -Recurse -Force
    Remove-Item $modwZipfile

    if (!(Test-Path $MODW_HOME_DIR)) {
        New-Item -Type Directory $MODW_HOME_DIR | Out-Null
    }

    $modwRepoDir = "$MODW_HOME_DIR\repo"
    if (!(Test-Path $modwRepoDir)) {
        New-Item -Type Directory $modwRepoDir | Out-Null
    }

    $modwCacheDir = "$MODW_HOME_DIR\repo\modw"
    if (!(Test-Path $modwCacheDir)) {
        New-Item -Type Directory $modwCacheDir | Out-Null
    }

    # Finally ensure ModW is in the PATH
    Add-ModWDirToPath

    Write-InstallInfo 'ModW was installed successfully!' -ForegroundColor DarkGreen
    Write-InstallInfo "Type 'modw --help' for instructions."
}



function Write-DebugInfo {
    param($BoundArgs)

    Write-Verbose '-------- PSBoundParameters --------'
    $BoundArgs.GetEnumerator() | ForEach-Object { Write-Verbose $_ }
    Write-Verbose '-------- Environment Variables --------'
    Write-Verbose "`$env:USERPROFILE: $env:USERPROFILE"
    Write-Verbose "`$env:ProgramData: $env:ProgramData"
    Write-Verbose "`$env:MODW: $env:MODW"
}

# Prepare variables
$IS_EXECUTED_FROM_IEX = ($null -eq $MyInvocation.MyCommand.Path)

# Abort when the language mode is restricted
Test-LanguageMode

# ModW app directory - where the scripts are deployed
$MODW_DIR = $ModWDir, $env:MODW, "$env:USERPROFILE\modw" | Where-Object { -not [String]::IsNullOrEmpty($_) } | Select-Object -First 1
# ModW home directory - where the configuration and repo is located
$MODW_HOME_DIR = "$env:USERPROFILE\.modw"
# ModW repo directory - where the mod jars are deployed
$MODW_REPO_DIR = "$MODW_HOME_DIR\repo"



# Quit if anything goes wrong
$oldErrorActionPreference = $ErrorActionPreference
$ErrorActionPreference = 'Stop'

# Logging debug info
Write-DebugInfo $PSBoundParameters
# Bootstrap function
Install-ModW

# Reset $ErrorActionPreference to original value
$ErrorActionPreference = $oldErrorActionPreference
