package taco;

public interface ObjectFactory {

	/**
	 * Creates an object
	 * 
	 * @param clazz the class of the object to create
	 * @return
	 * @throws Exception 
	 */
	Object create(Class<?> clazz) throws Exception;
	 
	

}
