package taco;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHandler {

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public CookieHandler(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public void setCookie(String name, String value, Integer validTime) {		
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(validTime);
		
		this.response.addCookie(cookie);
	}
	
	public Cookie getCookie(String name) {
		Cookie[] cookies = this.request.getCookies();
		
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

}
