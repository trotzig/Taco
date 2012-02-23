package taco;

public class CachePolicy {

	private int expirationInMinutes;
	
	public CachePolicy(int expirationInMinutes) {
		this.expirationInMinutes = expirationInMinutes;
	}
	
	public int getExpirationInMinutes() {
		return expirationInMinutes;
	}
	
}
