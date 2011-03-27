package taco;

import javax.servlet.http.HttpServletRequest;

/**
 * Controllers can implement this interface to get access to the request object
 * 
 * @author henper
 * 
 */
public interface RequestAware {

	/**
	 * Gain access to the underlying request object
	 * 
	 * @param request
	 */
	void setRequest(HttpServletRequest request);

}
