package taco.exception;


@SuppressWarnings("serial")
public class RedirectException extends StatusCodeException {

	private String redirectUri;

	public enum Codes {
		FOUND_302 (302),
		TEMPORARY_REDIRECT_307 (307);
		
		private final int code2;

		Codes(int code) {
			this.code2 = code;
		}
		
		int getCode() {
			return this.code2;
		}
	}
	
	/**
	 * Standard 302 redirect.
	 * 
	 * @param redirectUri
	 * @param message
	 */
	public RedirectException(String redirectUri, String message) {
		super(Codes.FOUND_302.getCode(), message);
		this.redirectUri = redirectUri;
	}
	
	public RedirectException(Codes code, String redirectUri, String message) {
		super(code.getCode(), message);
		this.redirectUri = redirectUri;
	}
	
	public String getRedirectUri() {
		return redirectUri;
	}
	
}
