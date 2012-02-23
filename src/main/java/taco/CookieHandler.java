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

	public Cookie setCookie(String name, String value, Integer validTime) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(validTime);
		cookie.setPath("/");
		this.response.addCookie(cookie);
		
		return cookie;
	}
	
	public Cookie setCookie(String name, String value, Integer validTime, String domain) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(validTime);
		cookie.setDomain(domain);
		cookie.setPath("/");
		this.response.addCookie(cookie);
		
		return cookie;
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
	
	public void deleteCookie(String name) {
		setCookie(name, null, 0);
	}

}
