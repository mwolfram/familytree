package at.or.wolfram.mate.familytree;

import java.io.IOException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {

	private Mapper() {
	}
	
	public static <T> T parseJson(String jsonString, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		byte[] jsonData = jsonString.getBytes(Charset.defaultCharset());
		ObjectMapper objectMapper = new ObjectMapper();
		T object = objectMapper.readValue(jsonData, clazz);
		return object;
	}
	
	public static <T> String writeJson(T object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}
	
}
