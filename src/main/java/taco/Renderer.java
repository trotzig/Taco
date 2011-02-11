package taco;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Renderer {

	/**
	 * Implementing classes will render a response to the request.
	 * 
	 * @param result
	 *            The object returned from the execute method of your controller
	 * @param controller
	 *            The controller that handled the request
	 * @param request
	 *            The original request
	 * @param response
	 *            The response object, write your response here!
	 * @throws IOException 
	 * @throws ServletException 
	 */
	void render(Object result, Controller<?> controller,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
