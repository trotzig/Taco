package taco;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Mocked servlet config to allow for dynamically added servlets
 * 
 * @author henper
 * 
 */
public class InjectedServletConfig implements ServletConfig {

	private ServletContext context;

	public InjectedServletConfig(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public String getInitParameter(String key) {
		// no init params
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getInitParameterNames() {
		return new Enumeration() {
			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public Object nextElement() {
				return null;
			}
		};
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

	@Override
	public String getServletName() {
		return "generated-by-taco";
	}

}
