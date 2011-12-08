package org.qooxdoo.charless.build.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Handles the configuration of a Qooxdoo Application
 * @author charless
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Config {
	private String name;
	
	private Map<String,Object> let = new LinkedHashMap<String,Object>();
	
	public final static String APPLICATION_JSON_FILE = "config.json";

	
	/**
	 * Read the qbt json file from current dir
	 * Return null if file does not exists
	 * @return null|QbtConfig
	 * @throws Exception
	 */
	public static Config read() throws Exception {
		File jsonApp = new File("."+File.separator+APPLICATION_JSON_FILE);
		if (jsonApp.exists()) {
			return read(jsonApp);
		}
		return null;
	}
	
	public static Config read(File jsonConfig) throws Exception {
		ObjectMapper mapper = Json.getMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS,true);
		Config cfg = mapper.readValue(jsonConfig, Config.class);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS,false);
		return cfg;
	}
	
	public static boolean isaQxApplicationDirectory(File dir) {
		return (new File(dir,APPLICATION_JSON_FILE).isFile());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Object let(String name) {
		return this.let.get(name);
	}
	
	public String getQooxdooPath() {
		return (String)this.let("QOOXDOO_PATH");
	}
	
	public void setQooxdooPath(String qooxdooPath) {
		this.let.put("QOOXDOO_PATH",qooxdooPath);
	}
	
	public String getApplication() {
		return (String)this.let("APPLICATION");
	}
	
	public String getQxTheme() {
		return (String)this.let("QXTHEME");
	}
	
	public String getCache() {
		return (String)this.let("CACHE");
	}
	
	public String getRoot() {
		return (String)this.let("ROOT");
	}
	
	@SuppressWarnings(value = { "unchecked", "unused" })
	private void setLet(Object o) {
		let = (LinkedHashMap<String,Object>) o;
	}
	
	@SuppressWarnings("unused")
	private Map<String,Object>  getLet() {
		return let;
	}
	
}
