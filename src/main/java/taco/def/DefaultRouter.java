package taco.def;

import taco.Router;

public class DefaultRouter extends Router {

	/**
	 * Default, non-arg constructor required by MVCaur
	 */
	public DefaultRouter() {
		/*
		 * Shows how to inject a custom object factory. The object factory is
		 * used when creating new instances of Controllers
		 */
		setObjectFactory(new DefaultObjectFactory());
	}

	@Override
	public void init() {
		route("/").through(ComplexController.class).renderedBy("/complex.jsp");
		
		route("/simple").renderedBy("/simple.jsp");
		
		route("/json/{message}/{number:int}").through(DefaultController.class)
				.renderAsJson();
		route("/hello/{message}").through(DefaultController.class).renderedBy(
				"/start.jsp");
		route("/hello/{message}/{number:int}").through(DefaultController.class)
				.renderedBy("/start.jsp");
		
		route("/servlet").throughServlet(TestServlet.class);
	}

}
