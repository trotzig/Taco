package taco;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class RouterFilter implements Filter {
	public static ServletContext servletContext;

	private Router router;
	
	private UserService userService = UserServiceFactory.getUserService();
	
	private static final String correctWebXml = "<filter>\n"
			+ "\t<filter-name>routingFilter</filter-name>\n"
			+ "\t<filter-class>taco.RouterFilter</filter-class>\n"
			+ "\t<init-param>\n" + "\t\t<param-name>router</param-name>\n"
			+ "\t\t<param-value>taco.TestRouter</param-value>\n"
			+ "\t</init-param>\n" + "</filter>\n\n";

	private static final String zeroArgumentConstructorBody = "() {\n"
			+ "\t//default constructor, needed by taco\n" + "}";


	@Override
	public void destroy() {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		PreparedFlow flow = router.execute(request);
		if (flow == null) {
			// no url mapping for this request, continue as if nothing happened.
			chain.doFilter(req, resp);
		} else {
			try {
				//Set correct cache headers
				if (!response.containsHeader("Expires")) {
					CachePolicy policy = flow.getFlow().getCachePolicy();
					int min = policy.getExpirationInMinutes();
					long expiresMS = 0;
					if (min > 0) {
						expiresMS = System.currentTimeMillis() + (min * 60L * 1000L);
					}
					response.setDateHeader("Expires", expiresMS);
				}
				
				routeThrough(request, response, flow);
				
				
			} catch (RedirectException e) {
				response.sendRedirect(e.getRedirectUri());
			} catch (StatusCodeException e) {
				//respond with the status code
				response.sendError(e.getCode(), e.getMessage());
			}
		}
	}

	private void routeThrough(HttpServletRequest request,
			HttpServletResponse response, PreparedFlow flow)
			throws ServletException, IOException {
		Protector prot = flow.getFlow().getProtector();
		if (prot != null) {
			if (!prot.allow(request)) {
				throw new RedirectException(userService.createLoginURL(request.getRequestURI()), "You are not authorized to perform this request");
			}
		}
		RoutingContinuation cont = flow.getContinuation();
		if (cont.getController() != null) {
			if (cont.getController() instanceof RequestAware) {
				//inject the underlying request object
				((RequestAware)cont.getController()).setRequest(request);
			}
			if (cont.getController() instanceof CookieAware) {
				CookieHandler cookieHandler = new CookieHandler(request, response);
				((CookieAware)cont.getController()).setCookieHandler(cookieHandler);
			}
			
			Object result = cont.getController().execute();
			request.setAttribute("taco", result);
			request.setAttribute("controller", cont.getController());
			flow.getFlow().getRenderer().render(result, cont.getController(), request, response);
		} else if (cont.getServlet() != null) {
			cont.getServlet().service(request, response);
		} else {
			throw new RuntimeException("No continuation found for this request");
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void init(FilterConfig conf) throws ServletException {
		servletContext = conf.getServletContext();
		String routerClass = conf.getInitParameter("router");
		if (routerClass == null) {
			throw new RouterMissingException(
					"No router class configured in web.xml. "
							+ "A correct web.xml configuration should look something like this: "
							+ correctWebXml
							+ "Your web.xml is missing the init-param named router");
		}
		Class<Router> routerClazz;
		try {
			routerClazz = (Class<Router>) Class.forName(routerClass);
			try {
				router = routerClazz.newInstance();
			} catch (Exception e) {
				if (e instanceof ClassCastException) {
					throw new RuntimeException(
							"Failed to create the router. Make sure "
									+ routerClass + " extends "
									+ Router.class.getName(), e);
				}
				throw new RuntimeException(
						"Failed to initialize the router class. Does \""
								+ routerClass
								+ "\" have a zero argument default constructor? "
								+ "If not, add such a constructor:\n"
								+ "public " + routerClazz.getSimpleName()
								+ zeroArgumentConstructorBody, e);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"The router class can not be found. Is \"" + routerClass
							+ "\" a real class and on the runtime classpath?",
					e);
		}

		router.init();

	}
	
	/**
	 * Convenient method, used mainly for testing 
	 * 
	 * @param router
	 */
	public void setRouter(Router router) {
		this.router = router;
	}


}
