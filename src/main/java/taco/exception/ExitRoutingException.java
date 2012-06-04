package taco.exception;

/**
 * Jumping out from current Taco routing filter.
 * 
 */
@SuppressWarnings("serial")
public class ExitRoutingException extends RuntimeException {

	public ExitRoutingException(String msg) {
		super(msg);
	}
	
}
