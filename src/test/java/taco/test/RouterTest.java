package taco.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import taco.CachePolicy;
import taco.Controller;
import taco.RoutingFlow;
import taco.test.controller.AllParamTypes;
import taco.test.controller.MockControllerRequest;
import taco.test.controller.NoMapping;

public class RouterTest {

	@Test
	public void testNoParams() {
		RoutingFlow flow = new RoutingFlow().route("/").through(NoMapping.class);
		assertNull(flow.execute(new MockControllerRequest("/55")));
		Controller<?> c = flow.execute(new MockControllerRequest("/")).getController(); 
		assertNotNull(c);
		assertEquals(NoMapping.class, c.getClass());
	}
	
	
	@Test
	public void testAllParamTypes() {
		RoutingFlow flow = new RoutingFlow().route("/{bool:boolean}/static/{number:int}/{longer:long}/{doubler:double}/{string:string}/{stringDefault}").through(AllParamTypes.class);
		assertNull(flow.execute(new MockControllerRequest("/55")));
		assertNull(flow.execute(new MockControllerRequest("/")));
		AllParamTypes c = (AllParamTypes) flow.execute(new MockControllerRequest("/true/static/1/2/2.5/string/3")).getController(); 
		assertNotNull(c);
		assertEquals(Integer.valueOf(1), c.getNumber());
		assertEquals(Long.valueOf(2), c.getLonger());
		assertEquals(Double.valueOf(2.5d), c.getDoubler());
		assertEquals("string", c.getString());
		assertEquals("3", c.getStringDefault());
	}
	
	@Test
	public void testNegativeValues() {
		RoutingFlow flow = new RoutingFlow().route("/{number:int}/{longer:long}/{doubler:double}").through(AllParamTypes.class);
		AllParamTypes c = (AllParamTypes) flow.execute(new MockControllerRequest("/-42/-4711/-47.11")).getController();
		assertNotNull(c);
		assertEquals(Integer.valueOf(-42), c.getNumber());
		assertEquals(Long.valueOf("-4711"), c.getLonger());
		assertEquals(Double.valueOf(-47.11d), c.getDoubler());
	}
	
	
	@Test
	public void wrongTypeDoesNotMatch() {
		RoutingFlow flow = new RoutingFlow().route("/{number:int}").through(AllParamTypes.class);
		assertNull(flow.execute(new MockControllerRequest("/hh")));
		AllParamTypes c = (AllParamTypes) flow.execute(new MockControllerRequest("/123")).getController(); 
		assertNotNull(c);
		assertEquals(Integer.valueOf(123), c.getNumber());
	}
	
	
	
	@Test 
	public void cacheSettingForRouteAffectsHeaders() {
		assertEquals(1, new RoutingFlow().withCachePolicy(new CachePolicy(1)).getCachePolicy().getExpirationInMinutes());
	}


	
}
