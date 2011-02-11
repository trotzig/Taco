package taco.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;


import org.junit.Test;

import taco.Controller;
import taco.RoutingFlow;
import taco.test.controller.AllParamTypes;
import taco.test.controller.NoMapping;

public class RouterTest {
	
	public static Map<String, String[]> emptyRequestParams = new HashMap<String, String[]>();
	
	@Test
	public void testNoParams() {
		RoutingFlow flow = new RoutingFlow().route("/").through(NoMapping.class);
		assertNull(flow.execute("/55", emptyRequestParams));
		Controller<?> c = flow.execute("/", emptyRequestParams).getController(); 
		assertNotNull(c);
		assertEquals(NoMapping.class, c.getClass());
	}
	
	
	@Test
	public void testAllParamTypes() {
		RoutingFlow flow = new RoutingFlow().route("/{bool:boolean}/static/{number:int}/{longer:long}/{doubler:double}/{string:string}/{stringDefault}").through(AllParamTypes.class);
		assertNull(flow.execute("/55", emptyRequestParams));
		assertNull(flow.execute("/", emptyRequestParams));
		AllParamTypes c = (AllParamTypes) flow.execute("/true/static/1/2/2.5/string/3", emptyRequestParams).getController(); 
		assertNotNull(c);
		assertEquals(Integer.valueOf(1), c.getNumber());
		assertEquals(Long.valueOf(2), c.getLonger());
		assertEquals(Double.valueOf(2.5d), c.getDoubler());
		assertEquals("string", c.getString());
		assertEquals("3", c.getStringDefault());
	}
	
	
	@Test
	public void wrongTypeDoesNotMatch() {
		RoutingFlow flow = new RoutingFlow().route("/{number:int}").through(AllParamTypes.class);
		assertNull(flow.execute("/hh", emptyRequestParams));
		AllParamTypes c = (AllParamTypes) flow.execute("/123", emptyRequestParams).getController(); 
		assertNotNull(c);
		assertEquals(Integer.valueOf(123), c.getNumber());
	}


	
}
