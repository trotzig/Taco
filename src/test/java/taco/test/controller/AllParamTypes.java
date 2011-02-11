package taco.test.controller;

import taco.Controller;

public class AllParamTypes implements Controller<String>{
	private Boolean bool;
	private String string;
	private String stringDefault;
	private Long longer;
	private Integer number;
	private Double doubler;
	
	@Override
	public String execute() {
		return "all";
	}

	public Boolean getBool() {
		return bool;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getStringDefault() {
		return stringDefault;
	}

	public void setStringDefault(String stringDefault) {
		this.stringDefault = stringDefault;
	}

	public Long getLonger() {
		return longer;
	}

	public void setLonger(Long longer) {
		this.longer = longer;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Double getDoubler() {
		return doubler;
	}

	public void setDoubler(Double doubler) {
		this.doubler = doubler;
	}
 
	
	
}
