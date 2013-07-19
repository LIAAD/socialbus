package pt.sapo.labs.utils;

import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtils {
	public static String jsonStringFromObject(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, object);
		} catch (Exception e) {
			System.err.println("Error serializing object");
			e.printStackTrace();
			return null;
		}
		return writer.toString();
	}
}
