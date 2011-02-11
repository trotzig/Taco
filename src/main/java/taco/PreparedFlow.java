package taco;

/**
 * A {@link PreparedFlow} is an object instructing the router how to continue
 * its route.
 * 
 * @author henper
 * 
 */
public class PreparedFlow {

	private RoutingFlow flow;
	private RoutingContinuation continuation;

	/**
	 * Gets the flow description for this prepared flow.
	 * 
	 * @return
	 */
	public RoutingFlow getFlow() {
		return flow;
	}

	public void setFlow(RoutingFlow flow) {
		this.flow = flow;
	}

	public RoutingContinuation getContinuation() {
		return continuation;
	}

	public void setContinuation(RoutingContinuation continuation) {
		this.continuation = continuation;
	}

	
	


}
