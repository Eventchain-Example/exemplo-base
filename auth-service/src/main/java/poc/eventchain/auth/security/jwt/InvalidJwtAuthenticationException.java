package poc.eventchain.auth.security.jwt;

public class InvalidJwtAuthenticationException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 559510206071953698L;

	public InvalidJwtAuthenticationException() {
		super();
	}

	public InvalidJwtAuthenticationException(String message) {
		super(message);
	}

	public InvalidJwtAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidJwtAuthenticationException(Throwable cause) {
		super(cause);
	}

	protected InvalidJwtAuthenticationException(String message, Throwable cause, boolean enableSuppression,
                                                boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
