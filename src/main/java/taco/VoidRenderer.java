package taco;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VoidRenderer implements Renderer {

	@Override
	public void render(Object result, Controller<?> controller,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// renderer that produces no output
	}

}
