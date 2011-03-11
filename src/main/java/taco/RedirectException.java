package taco;

@SuppressWarnings("serial")
public class RedirectException extends StatusCodeException {

	private String redirectUri;

	public RedirectException(String redirectUri, String message) {
		super(302, message);
		this.redirectUri = redirectUri;
	}
	
	public String getRedirectUri() {
		return redirectUri;
	}
	
}
