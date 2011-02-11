package taco.def;

public class Complex {

	public static class Inner {
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
	}
	
	private String name;
	private Inner inner;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Inner getInner() {
		return inner;
	}
	public void setInner(Inner inner) {
		this.inner = inner;
	}
	
	
}
