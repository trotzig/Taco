package taco;

import javax.servlet.http.HttpServletRequest;

public interface Protector {

	/**
	 * Checks a request to see if it is ok or not. Return true to allow the
	 * request to be made.
	 * 
	 * @param request
	 * @return
	 */
	boolean allow(HttpServletRequest request);

}
