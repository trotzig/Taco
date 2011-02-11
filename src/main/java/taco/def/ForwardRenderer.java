package taco.def;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import taco.Controller;
import taco.Renderer;


/**
 * A renderer that forwards the request to an internal resource, e.g. a JSP
 * file.
 * 
 * @author henper
 * 
 */
public class ForwardRenderer implements Renderer {

	private String forward;

	/**
	 * Create a forward renderer to an internal resource
	 * 
	 * @param forward
	 */
	public ForwardRenderer(String forward) {
		this.forward = forward;
	}

	/**
	 * Gets the resource path to forward to.
	 * 
	 * @return
	 */
	public String getForward() {
		return forward;
	}

	@Override
	public void render(Object result, Controller<?> controller,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, IOException {
		request.getRequestDispatcher(forward).forward(request, response);
	}

}
