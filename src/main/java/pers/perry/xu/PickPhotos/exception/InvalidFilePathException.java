package pers.perry.xu.pickphotos.exception;

public class InvalidFilePathException extends Throwable {

	private static final long serialVersionUID = 1L;

	private String msgString;
	
	public InvalidFilePathException(String msg) {
		super();
		this.msgString = msg;
	}

	@Override
	public String getMessage() {
		return msgString;
	}
}
