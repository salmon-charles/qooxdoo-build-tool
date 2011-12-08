package org.qooxdoo.charless.build.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.qooxdoo.charless.build.config.Config;


import junit.framework.TestCase;

public class ConfigTest extends TestCase {
	
	public void testRead() throws Exception {
		File config = new File(this.getClass().getResource("/config.json").getPath());
		Config cfg = Config.read(config);
		assertEquals("myRiaApp",cfg.getName());
		Map<String,String> expected = new LinkedHashMap<String,String>() {{
			put("APPLICATION","myriaapp");
			put("QOOXDOO_PATH","../../../../appname/target/qooxdoo-sdk");
			put("QXTHEME","myriaapp.theme.Theme");
			put("CACHE","cache");
			put("ROOT","root");
		}};
		for (String key: expected.keySet()) {
			assertEquals(expected.get(key),(String)cfg.let(key));
		}
		// Shortcuts
		assertEquals(expected.get("QOOXDOO_PATH"),cfg.getQooxdooPath());
		assertEquals(expected.get("APPLICATION"),cfg.getApplication());
		assertEquals(expected.get("QXTHEME"),cfg.getQxTheme());
		assertEquals(expected.get("CACHE"),cfg.getCache());
		assertEquals(expected.get("ROOT"),cfg.getRoot());
	}
	
}
