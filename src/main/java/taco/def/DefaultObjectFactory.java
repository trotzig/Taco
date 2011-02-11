package taco.def;

import taco.ObjectFactory;

public class DefaultObjectFactory implements ObjectFactory {

	@Override
	public Object create(Class<?> clazz) throws Exception {
		return clazz.newInstance();
	}


}
