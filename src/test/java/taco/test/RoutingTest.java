package taco.test;
import static org.junit.Assert.*;

import org.junit.Test;

import taco.PreparedFlow;
import taco.Router;
import taco.VoidRenderer;
import taco.test.controller.MockControllerRequest;
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
		PreparedFlow flow = router.execute(new MockControllerRequest("/"));
		NoMapping controller = (NoMapping) flow.getContinuation().getController();
		assertEquals("no-mapping", controller.execute());
	}
	
	@Test
	public void testNoMatch() {
		Router router = new Router() {
			
			@Override
			public void init() {
				route("/{name}").through(NoMapping.class).renderedBy("/start.jsp");
			}
		};
		router.init();
		PreparedFlow flow = router.execute(new MockControllerRequest("/hello/foo/bar"));
		assertNull(flow);
	}
	
	@Test
	public void testVoidRenderer() {
		Router router = new Router() {
			
			@Override
			public void init() {
				route("/").through(NoMapping.class);
			}
		};
		router.init();
		PreparedFlow flow = router.execute(new MockControllerRequest("/"));
		assertNotNull(flow);
		assertEquals(flow.getFlow().getRenderer().getClass(), VoidRenderer.class);
	}
	
	
}
