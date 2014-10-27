package io.sporkpgm.rotation.exceptions;

public class RotationLoadException extends Exception {

	private static final long serialVersionUID = 6288917033131370868L;
	private String message;

	public RotationLoadException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
