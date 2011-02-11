package taco;

/**
 * Throw this exception within your execute() method of your {@link Controller}
 * to signal a specific status code to a client.
 * 
 * @author henper
 * 
 */
@SuppressWarnings("serial")
public class StatusCodeException extends RuntimeException {

	private int code;

	/**
	 * Creates a {@link StatusCodeException} with the specified status code
	 * 
	 * @param code
	 * @param message
	 */
	public StatusCodeException(int code, String message) {
		this(code, message, null);
	}

	public StatusCodeException(int code, String message, Throwable t) {
		super(message, t);
		this.code = code;
	}

	/**
	 * Gets the status code
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

}
