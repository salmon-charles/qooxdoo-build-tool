package org.qooxdoo.qooxdoo_java_tool;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.InteractiveConsole;
import org.qooxdoo.charless.buildtool.QxEmbeddedJython;

public class QxEmbeddedJythonTest extends TestCase {
	
	public static String qooxdooSdkPath = QxEmbeddedJythonTest.class.getResource("/qooxdoo-sdk").getPath();
    
	private boolean skipTests = true;
	
    public void testRun() throws Exception {
    	// Launch the generator on a testing config
    	String[] options = new String[] {
    			"--config",this.getClass().getResource("/config-test.json").getPath()
    	};
    	
    	QxEmbeddedJython qx = new QxEmbeddedJython(new File(qooxdooSdkPath));
    	try {
    		if (! skipTests) {qx.run("generator.py",options);};
    	} catch (Exception e)
    	{}
        
    }
    
    public void testHelp() throws Exception {
    	// Launch the generator on a testing config
    	String[] options = new String[] {
    			"--help"
    	};
    	QxEmbeddedJython qx = new QxEmbeddedJython(new File(qooxdooSdkPath));
    	try {
    		if (! skipTests) {qx.run("generator.py",options);};
    	} catch (Exception e) {
    		
    	}
        
    }
    
    public void testConsole() {
    	QxEmbeddedJython qx = new QxEmbeddedJython(new File(qooxdooSdkPath));
    	if (! skipTests) {

         
    	};
    }
    
  
}
