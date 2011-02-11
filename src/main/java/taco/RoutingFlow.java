package taco;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import taco.RegexpMapper.ParamType;
import taco.RegexpMapper.PreparedMapping;
import taco.def.DefaultObjectFactory;
import taco.def.ForwardRenderer;
import taco.def.JsonRenderer;


/**
 * A {@link RoutingFlow} is a representation of a routing flow through MVCaur.
 * For a request, each routing flow is checked to see if it knows how to handle
 * the request. A routing flow knows what request it can handle, which
 * controller to execute and how to render the response.
 * 
 * A response can be rendered by any predefined type , or a custom
 * {@link Renderer}.
 * 
 * @author henper
 * 
 */
public class RoutingFlow {

	private Map<Class<? extends Servlet>, Servlet> loadedServlets = new HashMap<Class<? extends Servlet>, Servlet>();

	private Class<? extends Controller<?>> controller = VoidController.class;
	private Servlet servlet;
	private Renderer renderer;
	private String mapping;
	private RegexpMapper mapper;
	private ObjectFactory objectFactory;

	public RoutingFlow(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	
	public RoutingFlow() {
		this.objectFactory = new DefaultObjectFactory();
	}
	
	public String getMapping() {
		return mapping;
	}

	protected void setMapping(String mapping) {
		this.mapping = mapping;
		this.mapper = new RegexpMapper(mapping);
	}

	public Class<? extends Controller<?>> getController() {
		return controller;
	}

	protected void setController(Class<? extends Controller<?>> controller) {
		this.controller = controller;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public Servlet getServlet() {
		return servlet;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Tell the routing flow which controller class to use.
	 * 
	 * @param controllerClass
	 * @return
	 */
	public RoutingFlow through(Class<? extends Controller<?>> controllerClass) {
		setController(controllerClass);
		return this;
	}

	/**
	 * Instruct the routing flow to be forwarded to an internal resource, e.g. a
	 * JSP file.
	 * 
	 * @param forward
	 * @return
	 */
	public RoutingFlow renderedBy(String forward) {
		this.renderer = new ForwardRenderer(forward);
		return this;
	}

	/**
	 * Instruct the routing flow to render through your own custom renderer
	 * 
	 * @param renderer
	 * @return
	 */
	public RoutingFlow renderedBy(Renderer renderer) {
		this.renderer = renderer;
		return this;
	}

	/**
	 * Instruct the routing flow to be rendered as JSON
	 * 
	 * @return
	 */
	public RoutingFlow renderAsJson() {
		this.renderer = new JsonRenderer();
		return this;
	}

	/**
	 * Returns a controller/servlet if the routing flow knows how to handle a
	 * request
	 * 
	 * @param request
	 *            .getRequestURI()
	 * @return
	 */
	public RoutingContinuation execute(String requestURI,
			Map<String, String[]> requestParams) {
		PreparedMapping mapping = mapper.execute(requestURI);
		if (mapping != null) {
			return createContinuation(mapping, requestParams);
		}
		return null;
	}

	private RoutingContinuation createContinuation(PreparedMapping m,
			Map<String, String[]> requestParams) {
		RoutingContinuation cont = new RoutingContinuation();
		if (controller != null) {
			cont.setController(createController(objectFactory, m, requestParams));
		} else if (servlet != null) {
			try {
				cont.setServlet(servlet);
			} catch (Exception e) {
				throw new RuntimeException("Failed to create servlet", e);
			}
		}
		return cont;
	}

	private Controller<?> createController(ObjectFactory factory,
			PreparedMapping m, Map<String, String[]> requestParams) {
		Controller<?> ctrl;
		try {
			ctrl = (Controller<?>) factory.create(controller);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create controller", e);
		}
		// first, set params within the url
		Map<String, Object> urlParams = m.getMap();
		for (Map.Entry<String, Object> entry : urlParams.entrySet()) {
			try {
				setParam(ctrl, entry.getValue(), entry.getKey());
			} catch (NoSuchMethodException e) {
				// ignore missing method
			}
		}
		// secondly, set all params from request parameters
		if (requestParams != null) {
			for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
				handleRequestParam(ctrl, entry);
			}
		}
		return ctrl;
	}

	private void handleRequestParam(Controller<?> ctrl,
			Map.Entry<String, String[]> entry) {
		for (ParamType pt : ParamType.values()) {
			try {
				setParam(ctrl, pt.parse(entry.getValue()[0]), entry.getKey());
				// success, break loop
				return;
			} catch (NoSuchMethodException e) {
				// try next param type
			} catch (NumberFormatException e) {
				// try next param type
			}
		}
	}

	private void setParam(Controller<?> ctrl, Object obj, String name)
			throws NoSuchMethodException {
		String nameUpper = name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		try {
			Method setMethod = controller.getMethod("set" + nameUpper,
					obj.getClass());
			try {
				setMethod.invoke(ctrl, obj);
			} catch (Exception e) {
				// not much to do, ignore
			}
		} catch (SecurityException e) {
			// no proper method found, ignore
		}
	}

	public RoutingFlow route(String route) {
		setMapping(route);
		return this;
	}

	/**
	 * Instruct the flow to route through a servlet
	 * 
	 * @param clazz
	 */
	public void throughServlet(Class<? extends Servlet> clazz) {
		Servlet s = loadedServlets.get(clazz);
		if (s == null) {
			try {
				s = (Servlet) objectFactory.create(clazz);
				s.init(new InjectedServletConfig(RouterFilter.servletContext));
			} catch (Exception e) {
				throw new RuntimeException("Failed to create servlet", e);
			}
			loadedServlets.put(clazz, s);
		}
		this.servlet = s;
		this.controller = null; //reset controller
	}

}
