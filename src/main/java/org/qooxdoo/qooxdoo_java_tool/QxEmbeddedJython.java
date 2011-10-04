package org.qooxdoo.qooxdoo_java_tool;

import java.io.File;
import java.util.Properties;

import javax.script.ScriptException;

import org.python.core.Py;
import org.python.core.PyFile;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.core.imp;
import org.python.util.InteractiveConsole;
import org.python.util.JLineConsole;
import org.python.util.PythonInterpreter;

/**
 * Run the a qooxdoo python script with an embedded Jython 
 * 
 * @author charless
 *
 */
public class QxEmbeddedJython {

	protected InteractiveConsole c;
	protected File qooxdooSdk;

	public static String qooxdooSdkPath = new File(".","qooxdoo-sdk").getPath();
	
	public static File resolvePythonScriptPath(File qooxdooSdkPath, String pythonScriptName) {
		return new File(qooxdooSdkPath,"tool"+File.separator+"bin"+File.separator+pythonScriptName);
	}
	
	public QxEmbeddedJython(File qooxdooSdk) {
		// Check qooxdoo sdk path
		this.qooxdooSdk = qooxdooSdk;
		if (this.qooxdooSdk == null) {
			this.qooxdooSdk = new File(qooxdooSdkPath);
		}
		// Setup librairies path
		Properties pOverride = new Properties();
	   // String jythonPythonPath = this.getClass().getResource("/Lib").getPath();
	    // Order is important, dont change !
	    String pythonPath = 
	    					 new File(this.qooxdooSdk,"/tool/pylib").getPath()
			    			+ File.pathSeparator 
	    					+ new File(this.qooxdooSdk,"/tool/bin").getPath();
	    pOverride.put("python.path", pythonPath);
	    // Initialize python
	    String [] args = new String[0];
		PySystemState.initialize(PySystemState.getBaseProperties(),pOverride, args);
		c = createInterpreter(checkIsInteractive());
	} 

	public void run(String pythonScriptName, String [] args) throws ScriptException {
		File pythonScript = resolvePythonScriptPath(this.qooxdooSdk,pythonScriptName);
		if (! pythonScript.exists() || ! pythonScript.canRead()) {
			throw new ScriptException(
					"The python script \'"
					+ pythonScript.getAbsolutePath()
					+"\' does not exist or is not readable !"
			);
		}
		try {
			PySystemState systemState = Py.getSystemState();
			systemState.argv.clear ();
			systemState.argv.append (new PyString (pythonScript.getPath()));      
			for (String arg: args) {
				systemState.argv.append (new PyString (arg));
			}
			this.c.execfile(pythonScript.getAbsolutePath());
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	protected boolean checkIsInteractive() {
		PySystemState systemState = Py.getSystemState();
		boolean interactive = ((PyFile) Py.defaultSystemState.stdin).isatty();
		if (!interactive) {
			systemState.ps1 = systemState.ps2 = Py.EmptyString;
		}
		return interactive;
	}

	protected InteractiveConsole createInterpreter(boolean interactive) {
		InteractiveConsole c = newInterpreter(interactive);
		Py.getSystemState().__setattr__("_jy_interpreter", Py.java2py(c));

		imp.load("site");
		return c;
	}

	private InteractiveConsole newInterpreter(boolean interactiveStdin) {
		if (!interactiveStdin) {
			return new InteractiveConsole();
		}

		String interpClass = PySystemState.registry.getProperty(
				"python.console", "");
		if (interpClass.length() > 0) {
			try {
				return (InteractiveConsole) Class.forName(interpClass)
						.newInstance();
			} catch (Throwable t) {
				// fall through
			}
		}
		return new JLineConsole();
	}
	
	public PythonInterpreter getPyInterpreter() {
		return new PythonInterpreter(Py.getSystemState().__dict__, Py.getSystemState());
	}

}

