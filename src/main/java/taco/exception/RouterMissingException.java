package taco.exception;

/**
 * Exception thrown when the RouterFilter has been configured without a router
 * class, or the router class can not be found.
 * 
 * @author henric.persson@gmail.com
 * 
 */
@SuppressWarnings("serial")
public class RouterMissingException extends RuntimeException {
	// empty marker exception

	public RouterMissingException(String msg) {
		super(msg);
	}
}
