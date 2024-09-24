package io.moderne.wrapper.cli;

import java.util.Arrays;

public enum ExitCode {
	OK(0), GENERAL_ERROR(1), MISUSE_OF_SHELL_BUILT_IN(2), CANNOT_EXECUTE(126), COMMAND_NOT_FOUND(127),
	FATAL_ERROR_SIGNAL(128), UNDEF(-1);

	private final int value;

	private ExitCode(final int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public ExitCode fromValue(final int value) {
		return Arrays.stream(ExitCode.values()).filter(exitCode -> exitCode.value() == value).findFirst().orElse(UNDEF);
	}
}
