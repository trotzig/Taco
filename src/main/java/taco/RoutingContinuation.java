package taco;

import javax.servlet.Servlet;

public class RoutingContinuation {

	private Controller<?> controller;
	private Servlet servlet;
	
	public Controller<?> getController() {
		return controller;
	}
	public void setController(Controller<?> controller) {
		this.controller = controller;
	}
	public Servlet getServlet() {
		return servlet;
	}
	public void setServlet(Servlet servlet) {
		this.servlet = servlet;
	}
	
	
}
