package taco.test;
import static org.junit.Assert.*;

import org.junit.Test;

import taco.PreparedFlow;
import taco.Router;
import taco.test.controller.NoMapping;




public class RoutingTest {

	@Test
	public void testSingleRoute() {
		Router router = new Router() {
			
			@Override
			public void init() {
				route("/").through(NoMapping.class).renderedBy("/start.jsp");
			}
		};
		router.init();
		PreparedFlow flow = router.execute("/");
		NoMapping controller = (NoMapping) flow.getContinuation().getController();
		assertEquals("no-mapping", controller.execute());
	}
	
	public void testNoMatch() {
		Router router = new Router() {
			
			@Override
			public void init() {
				route("/{name}").through(NoMapping.class).renderedBy("/start.jsp");
			}
		};
		PreparedFlow flow = router.execute("/hello/foo/bar");
		assertNull(flow);
	}
}
