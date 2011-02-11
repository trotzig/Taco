package taco;

/**
 * Default controller, used in case no controller is configured.
 * 
 * @author henper
 * 
 */
public class VoidController implements Controller<Void> {

	@Override
	public Void execute() {
		return null;
	}

}
