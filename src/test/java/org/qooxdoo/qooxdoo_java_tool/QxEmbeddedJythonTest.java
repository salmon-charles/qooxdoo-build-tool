package org.qooxdoo.qooxdoo_java_tool;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.InteractiveConsole;

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
    		// Setup librairies path
    		Properties pOverride = new Properties();
    		String jythonPythonPath = this.getClass().getResource("/Lib").getPath();
    	    // Order is important, dont change !
    	    String pythonPath = jythonPythonPath
    	    					+ File.pathSeparator 
    	    					+ new File(qooxdooSdkPath,"/tool/pylib").getPath()
    			    			+ File.pathSeparator 
    	    					+ new File(qooxdooSdkPath,"/tool/bin").getPath();
    	   // System.out.println(pythonPath);
    	    pOverride.put("python.path", pythonPath);
    	    // Initialize python
    	    String [] args = new String[0];
    		PySystemState.initialize(PySystemState.getBaseProperties(),pOverride, args);
    		try {
        		qx.setArgs("generator.py",args);
    		} catch (Exception e) {
    			System.out.println("ERROR:"+e);
    		}
    		InteractiveConsole console = qx.getInteractiveConsole();
    		console.set("QOOXDOO_PATH",qooxdooSdkPath);
    		console.exec("from qooxdoojava import generator");
    		console.exec("generator(\"\")");
    		
//    		console.raw_input(new PyString(">"));
         
    	};
    }
    
  
}
