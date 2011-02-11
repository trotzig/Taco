package taco.test.controller;

import taco.Controller;

public class NoMapping implements Controller<String> {

	@Override
	public String execute() {
		return "no-mapping";
	}
	
}
