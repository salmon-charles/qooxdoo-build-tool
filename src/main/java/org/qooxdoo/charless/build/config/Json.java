package org.qooxdoo.charless.build.config;

import org.codehaus.jackson.map.ObjectMapper;

public class Json {
	private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

	public static ObjectMapper getMapper() {
		return mapper;
	}

}
