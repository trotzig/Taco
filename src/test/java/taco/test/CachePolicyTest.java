package taco.test;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

import taco.PreparedFlow;
import taco.Router;
import taco.RouterFilter;
import taco.RoutingContinuation;
import taco.RoutingFlow;
import taco.test.controller.MockControllerRequest;

public class CachePolicyTest {

//	@Test
	public void ifNoPolicyDontCache() throws IOException, ServletException {
		
		PreparedFlow flow = new PreparedFlow();
		flow.setContinuation(new RoutingContinuation());
		flow.setFlow(new RoutingFlow());
		
		
		LinkedList mock = Mockito.mock(LinkedList.class);
		Mockito.when(mock.get(1)).thenReturn("Hej");
		System.out.println(mock.get(1));
		
		
		MockControllerRequest request = new MockControllerRequest("/");
		
		Router router = Mockito.mock(Router.class);
		Mockito.when(router.execute(request)).thenReturn(flow);
		
		
		RouterFilter filter = new RouterFilter();
		filter.setRouter(router);
		
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		
		filter.doFilter(request, response, null);

		Mockito.verify(response).addDateHeader("Expires", Mockito.anyLong());
	}
	
	
}
