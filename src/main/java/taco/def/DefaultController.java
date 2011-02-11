package taco.def;

import taco.Controller;
import taco.StatusCodeException;

public class DefaultController implements Controller<String> {

	private String message;
	private Integer number;
	
	@Override
	public String execute() {
		if (message == null) {
			throw new StatusCodeException(404, "No message found");
		} 
		if (number == null) {
			throw new StatusCodeException(503, "server error");
		} 
		
		return message + " " + number;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Integer getNumber() {
		return number;
	}

}
