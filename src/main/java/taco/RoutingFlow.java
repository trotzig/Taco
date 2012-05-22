package taco;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

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
	private Renderer renderer = new VoidRenderer();
	private String mapping;
	private RegexpMapper mapper;
	private ObjectFactory objectFactory;
	private Protector protector;
	private CachePolicy cachePolicy = new DontCachePolicy(); 

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
	
	public CachePolicy getCachePolicy() {
		return cachePolicy;
	}
	
	public void setCachePolicy(CachePolicy cachePolicy) {
		this.cachePolicy = cachePolicy;
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
	 * Sets a protector to this routing flow
	 * 
	 * @param protector
	 */
	public void setProtector(Protector protector) {
		this.protector = protector;
	}

	public Protector getProtector() {
		return protector;
	}

	/**
	 * Instruct the routing flow to be protected by a {@link Protector}
	 * 
	 * @param protector
	 * @return
	 */
	public RoutingFlow protect(Protector protector) {
		setProtector(protector);
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
	public RoutingContinuation execute(HttpServletRequest request) {
		PreparedMapping mapping = mapper.execute(request.getRequestURI());
		if (mapping != null) {
			return createContinuation(mapping, request);
		}
		return null;
	}

	public boolean matches(String uri) {
		return mapper.matches(uri);
	}

	private RoutingContinuation createContinuation(PreparedMapping m, HttpServletRequest request) {
		RoutingContinuation cont = new RoutingContinuation();
		if (controller != null) {
			cont.setController(createController(objectFactory, m, request));
		} else if (servlet != null) {
			try {
				cont.setServlet(servlet);
			} catch (Exception e) {
				throw new RuntimeException("Failed to create servlet", e);
			}
		}
		return cont;
	}

	private Controller<?> createController(ObjectFactory factory, PreparedMapping m, HttpServletRequest request) {
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

		// secondly, set all params from request parameters and non-multipart post data

		try {
			@SuppressWarnings("unchecked")
			Map<String, String[]> requestParams = request.getParameterMap();
			for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
				handleRequestParam(ctrl, entry.getKey(), entry.getValue()[0]);
			}
		} catch (UnsupportedOperationException e) {
			// expected in cases where the parameter map has been wrapped by
			// something unmodifiable, see e.g. this bug:
			// http://code.google.com/p/googleappengine/issues/detail?id=3081&q=UnsupportedOperationException&colspec=ID%20Type%20Component%20Status%20Stars%20Summary%20Language%20Priority%20Owner%20Log
		}

		// thirdly, set params from multipart post data

		String contentType = request.getContentType();
		if (contentType != null && contentType.startsWith("multipart/form-data")) {
			ServletFileUpload upload = new ServletFileUpload();
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();
					String fieldName = item.getFieldName();
					InputStream inputStream = item.openStream();
					try {
						if (item.isFormField()) {
							handleRequestParam(ctrl, fieldName, Streams.asString(inputStream, "UTF-8"));
						}
						else {
							ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
							Streams.copy(inputStream, baos, true);
							byte[] value = baos.toByteArray();
							if (value.length > 0 || !item.getName().isEmpty()) {
								setParam(ctrl, baos.toByteArray(), fieldName);
							}
						}
					} catch (NoSuchMethodException e) {
						// ignore missing method
					}
				}
			} catch (FileUploadException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return ctrl;
	}

	private void handleRequestParam(Controller<?> ctrl, String name, String value) {
		for (ParamType pt : ParamType.values()) {
			try {
				setParam(ctrl, pt.parse(value), name);
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
	public RoutingFlow throughServlet(Class<? extends Servlet> clazz) {
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
		this.controller = null; // reset controller
		return this;
	}

	/**
	 * Set a custom cache policy for your request
	 * 
	 * @param cachePolicy
	 * @return
	 */
	public RoutingFlow withCachePolicy(CachePolicy cachePolicy) {
		setCachePolicy(cachePolicy);
		return this;
	}

}
