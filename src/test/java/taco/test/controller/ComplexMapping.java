package taco.test.controller;

import java.util.Arrays;
import java.util.List;

import taco.Controller;


public class ComplexMapping implements Controller<List<Complex>> {

	@Override
	public List<Complex> execute() {
		return Arrays.asList(new Complex("foo"), new Complex("bar"));
	}

}
