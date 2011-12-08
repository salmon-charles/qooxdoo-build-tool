package org.qooxdoo.charless.build.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.qooxdoo.charless.build.config.QbtConfig;


import junit.framework.TestCase;

public class QbtConfigTest extends TestCase {
	
	public void testRead() throws Exception {
		File config = new File(this.getClass().getResource("/qbt.json").getPath());
		QbtConfig cfg = QbtConfig.read(config);
		assertEquals("/path/to/qx/sdk",cfg.getQooxdooPath());
	}
	
	public void testWrite() throws Exception {
		File out = File.createTempFile("qbtconfig", ".json");
		if (out.exists()) {
			QbtConfig cfg = new QbtConfig();
			cfg.setQooxdooPath("/path/to/qx/sdk");
			cfg.write(out);
			// Read
			QbtConfig cfgRead = QbtConfig.read(out);
			assertEquals(cfg.getQooxdooPath(), cfgRead.getQooxdooPath());
		}
	}
	
}
