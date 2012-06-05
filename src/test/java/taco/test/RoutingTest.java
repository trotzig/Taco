package taco.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import taco.PreparedFlow;
import taco.Router;
import taco.RoutingFlow;
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

	@Test
	public void testRouteTesting() {
		Router router = new Router() {
			@Override
			public void init() {
				route("/article/{id}").through(NoMapping.class).renderedBy("/start.jsp");
			}
		};

		router.init();

		assertTrue(router.hasMatchingRoute("/article/123"));
		assertFalse(router.hasMatchingRoute("/section/456"));
		
		RoutingFlow flow = router.getMatchingRoute("/article/123");
		assertEquals(NoMapping.class, flow.getController());
		
		assertNull(router.getMatchingRoute("/section/456"));
		
	}
}
