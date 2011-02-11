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
		response.setContentType("application/json");
		Gson gson = new Gson();
		gson.toJson(result, response.getWriter());
	}

}
