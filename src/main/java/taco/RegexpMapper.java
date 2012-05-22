package taco;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpMapper {
	
	public static class PreparedMapping {
		private Map<String, Object> map = new HashMap<String, Object>();

		public void put(String paramName, Object paramValue) {
			map.put(paramName, paramValue);
		}
		public Map<String, Object> getMap() {
			return map;
		}
		
	}

	public static interface ValueParser {
		Object parse(String s);
	}
	
	public static enum ParamType {
		BOOLEAN(Boolean.class, "(true|false)", new ValueParser() {
			@Override
			public Object parse(String s) {
				return Boolean.parseBoolean(s);
			}
		}),
		STRING(String.class, "([^\\/]+)"),
		INT(Integer.class, "(-?[0-9]+)", new ValueParser() {
			@Override
			public Object parse(String s) {
				return Integer.parseInt(s);
			}
		}),
		LONG(Long.class, "(-?[0-9]+)", new ValueParser() {
			@Override
			public Object parse(String s) {
				return Long.parseLong(s);
			}
		}),
		DOUBLE(Double.class, "(-?[0-9,.]+)", new ValueParser() {
			@Override
			public Object parse(String s) {
				return Double.parseDouble(s);
			}
		});
		
		private Class<?> clazz;
		private String pattern;
		private ValueParser parser;
		
		private ParamType(Class<?> clazz, String pattern) {
			this.clazz = clazz;
			this.pattern = pattern;
		}
		
		private ParamType(Class<?> clazz, String pattern, ValueParser parser) {
			this(clazz, pattern);
			this.parser = parser;
			
		}
		
		public Class<?> getClazz() {
			return clazz;
		}
		public String getPattern() {
			return pattern;
		}
		public Object parse(String s) {
			if (parser == null) {
				return s;
			}
			return parser.parse(s);
		}
	}
	
	
	private static Pattern simplePatternFinder = Pattern
			.compile("\\{[^\\}]+\\}");
	private Pattern pattern;
	private List<String> paramNames;
	private List<ParamType> paramTypes = new ArrayList<ParamType>();

	public RegexpMapper(String simplePattern) {
		parseSimplePattern(simplePattern);
	}

	private void parseSimplePattern(String simplePattern) {
		String regex = simplePattern;
		Matcher m = simplePatternFinder.matcher(simplePattern);
		paramNames = new ArrayList<String>();
		boolean result = m.find();
		while (result) {
			String sp = m.group();
			String paramName = sp.replace("{", "").replace("}", "");
			ParamType type = ParamType.STRING;
			if (paramName.contains(":")) {
				String[] split = paramName.split(":");
				paramName = split[0];
				type = ParamType.valueOf(split[1].toUpperCase());
			} 
			regex = regex.replace(sp, type.getPattern());
			paramNames.add(paramName);
			paramTypes.add(type);
			result = m.find();
		}
		pattern = Pattern.compile(regex);
	}
	
	public PreparedMapping execute(String uri) {
		Matcher m = pattern.matcher(uri);
		if (!m.matches()) {
			return null;
		}
		PreparedMapping map = new PreparedMapping();
		if (m.groupCount() > 0) {
			for (int i = 0; i < m.groupCount(); i++) {
				String strObj = m.group(i + 1);
				String name = getParamName(i);
				RegexpMapper.ParamType type = getParamType(i);
				Object obj;
				try {
					obj = type.parse(URLDecoder.decode(strObj, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Failed to find UTF-8 encoding", e);
				}
				map.put(name, obj);
			}
		}
		return map;
	}

	public boolean matches(String uri) {
		return pattern.matcher(uri).matches();
	}

	private String getParamName(int i) {
		return paramNames.get(i);
	}

	private ParamType getParamType(int i) {
		return paramTypes.get(i);
	}

}
