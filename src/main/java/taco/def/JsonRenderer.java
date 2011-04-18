package taco.def;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import taco.Controller;
import taco.Renderer;

import com.google.gson.Gson;


public class JsonRenderer implements Renderer {
	
	@Override
	public void render(Object result, Controller<?> controller,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String callbackFunc = request.getParameter("callback");
		if (callbackFunc != null) {
			response.setContentType("text/javascript; charset=utf-8");
			response.getWriter().println(callbackFunc + "(");
		} else {
			response.setContentType("application/json; charset=utf-8");
		}
		Gson gson = new Gson();
		gson.toJson(result, response.getWriter());
		
		if (callbackFunc != null) {
			response.getWriter().println(");");
		}
	}

}
