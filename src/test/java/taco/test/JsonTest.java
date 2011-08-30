package taco.test;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import taco.RoutingFlow;
import taco.test.controller.ComplexMapping;
import taco.test.controller.MockControllerRequest;
import taco.test.controller.NoMapping;

import com.google.gson.Gson;


public class JsonTest {

	
	@Test
	public void testSimpleJson() {
		RoutingFlow flow = new RoutingFlow().route("/").through(NoMapping.class).renderAsJson();
		Gson gson = new Gson();
		String json = gson.toJson(flow.execute(new MockControllerRequest("/")).getController().execute());
		assertEquals("\"no-mapping\"", json);
	}
	
	
	@Test
	public void testComplexType() {
		RoutingFlow flow = new RoutingFlow().route("/").through(ComplexMapping.class).renderAsJson();
		Gson gson = new Gson();
		String json = gson.toJson(flow.execute(new MockControllerRequest("/")).getController().execute());
		assertEquals("[{\"val\":\"foo\"},{\"val\":\"bar\"}]", json);
	}
	
}
