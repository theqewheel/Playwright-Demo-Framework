package framework.logging;

public class LogManager {
	
	public static UniversalLogger getLogger(Class<?> clazz) {
		return new UniversalLogger(clazz);
	}

}
