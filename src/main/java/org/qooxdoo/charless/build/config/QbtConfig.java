package org.qooxdoo.charless.build.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Handles the configuration of qbt
 * When qbt is launched from a directory that is not a qooxdoo application directory,
 * it creates a file named 'qbt.json' which is used to store qbt configuration
 * @author charless
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class QbtConfig {
	private String version;
	private String qooxdooPath;
	
	public final static String QBT_JSON_FILE = "qbt.json";
    private final static Logger logger = Logger.getLogger(QbtConfig.class);
	
	/**
	 * Return a qbt configuration.
	 * Qooxdoo Path resolution order:
	 *  - From config.json
	 *  - From qbt.json
	 *  - From env variable
	 * @return A  Qbt Config
	 */
	public static QbtConfig init() {
		// Read application json file
		Config appcfg = null;
		try { 
			appcfg = Config.read();
		} 
		catch(Exception e) {
			logger.warn("Error while reading "+Config.APPLICATION_JSON_FILE+": "+e);
		}
		// Read qbt json file
		QbtConfig qbtcfg = null;
		try { 
			qbtcfg = read();
		} 
		catch(Exception e) {
			logger.warn("Error while reading "+QBT_JSON_FILE+": "+e);
		}
		if (qbtcfg == null) {qbtcfg = new QbtConfig();}
		// Resolve qooxdoo path
		if (appcfg == null) {
			if (qbtcfg.getQooxdooPath() != null) {
				logger.info("QOOXDOO_PATH resolved from \'"+QBT_JSON_FILE+"\'");
			} else {
				// Get path from env
				String qooxdooPathEnv = System.getenv().get("QOOXDOO_PATH");
				if (qooxdooPathEnv != null ) {
					File qooxdooSdkPath = new File(qooxdooPathEnv);
					if (qooxdooSdkPath.exists() && qooxdooSdkPath.isDirectory()) {
						qbtcfg.setQooxdooPath(qooxdooSdkPath.getAbsolutePath());
						logger.info("QOOXDOO_PATH resolved from \'QOOXDOO_PATH env variable\'");
					} 
				}
			}
		} else {
			qbtcfg.setQooxdooPath(appcfg.getQooxdooPath());
			logger.info("QOOXDOO_PATH resolved from \'"+Config.APPLICATION_JSON_FILE+"\'");
		}
		// Finish qbtcfg init
		// TODO: handle version
		return qbtcfg;
	}
	
	/**
	 * Read the qbt json file from current dir
	 * Return null if file does not exists
	 * @return null|QbtConfig
	 * @throws Exception
	 */
	public static QbtConfig read() throws Exception {
		File jsonQbt = new File("."+File.separator+QBT_JSON_FILE);
		if (jsonQbt.exists()) {
			return read(jsonQbt);
		}
		return null;
	}
	
	public static QbtConfig read(File jsonConfig) throws Exception {
		ObjectMapper mapper = Json.getMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS,true);
		QbtConfig cfg = mapper.readValue(jsonConfig, QbtConfig.class);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS,false);
		return cfg;
	}
	
	/**
	 * Write the qbt json file in current directory
	 * @throws Exception
	 */
	public void write() throws Exception {
		this.write(new File("."+File.separator+QBT_JSON_FILE));
	}
	
	public void write(File jsonConfig) throws Exception {
		ObjectMapper mapper = Json.getMapper();
		mapper.writeValue(jsonConfig, this);
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getQooxdooPath() {
		return qooxdooPath;
	}
	public void setQooxdooPath(String qooxdooPath) {
		this.qooxdooPath = qooxdooPath;
	}


	
	
	
}
