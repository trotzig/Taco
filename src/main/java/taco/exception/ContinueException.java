package taco.exception;


@SuppressWarnings("serial")
public class ContinueException extends RuntimeException {

	/**
	 * Continue with next routing.
	 * Controller dosen't no what to do with the request.
	 */
	public ContinueException(String msg) {
		super(msg);
	}
	
}
