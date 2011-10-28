package org.qooxdoo.charless.buildtool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jline.ConsoleReader;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.InteractiveConsole;
import org.python.util.PythonInterpreter;


/**
 * The Generator command line interface
 * 
 * @author charless
 * 
 */
public class QooxdooBuildTool  {
	
	public static File qooxdooSdkPath;
	
	public static void main(String[] args) throws ScriptException {
		// Resolve Qooxdoo Sdk path
		qooxdooSdkPath = resolveQooxdooSdkPath();
		if (qooxdooSdkPath == null) {
			System.out.println(
					"[ERROR] Can not find Qooxdoo sdk path; set the \'QOOXDOO_PATH\' environment variable"
			);	
			System.exit(1);
		}
		System.out.println("QOOXDOO_PATH="+qooxdooSdkPath.getAbsolutePath());
		// Parse args
		if (args.length ==0 || args[0].startsWith("-")) {
			// Console must be started from an app root
			File configJson = new File("./config.json");
			if (! configJson.exists()) {
				System.out.println(
						"[ERROR] Could not find the \'config.json\' file; the console must be launched from a qooxdoo application root directory"
				);
				System.out.println("Type \'qbt help\' for getting started");
				System.exit(1);
			}
			System.exit(console(args));
		}
		// Help
		if ("help".equals(args[0].toLowerCase())) {
			help();
			System.exit(0);
		}
		// Action
		String pythonScriptName = args[0];
		if ("create-application".equals(args[0].toLowerCase())) {
			pythonScriptName = "create-application.py";
			args[0] = pythonScriptName;
		} else if(! args[0].endsWith("py")) {
			// jobs to be run
			pythonScriptName = "generator.py";
		}
		// Args
		if (pythonScriptName.equals(args[0])) {
			// Remove scriptname from args
			String [] pythonScriptArgs = new String[args.length-1];
			for (int i=1;i<args.length;i++) {
				pythonScriptArgs[i-1] = args[i];
			}
			args = pythonScriptArgs;
		}
		String pyScriptWithArgs = pythonScriptName;
		for (int i=0; i<args.length;i++) {
			pyScriptWithArgs += " "+args[i];
		}
		// Check script existence
		File pythonScript = QxEmbeddedJython.resolvePythonScriptPath(qooxdooSdkPath,pythonScriptName);
		if (! pythonScript.exists() || ! pythonScript.canRead()) {
			System.out.println(
					"[ERROR] The python script \'"
					+ pythonScript.getAbsolutePath()
					+"\' does not exist or is not readable !"
			);
			System.out.println();
			System.exit(1);
		}
		long starts = System.currentTimeMillis();
		System.out.print("[INFO] Initializing Jython...");
		QxEmbeddedJython qx = new QxEmbeddedJython(qooxdooSdkPath);
        long passedTimeInSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis() - starts, TimeUnit.MILLISECONDS);
        System.out.println(" done (in "+passedTimeInSeconds+" seconds)");
        starts = System.currentTimeMillis();
		System.out.println("[INFO] Running \'"+pyScriptWithArgs+"\'");
		try {
			qx.run(pythonScriptName,args);
		} catch (Exception e) {
			// Ignore exception at the moment
			// TODO: Add a java property to turn stack trace on
		}
        passedTimeInSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis() - starts, TimeUnit.MILLISECONDS);
        System.out.println("[INFO] DONE ! (in "+passedTimeInSeconds+" seconds)");
	}
		
	/**
	 * Resolve the path to the Qooxdoo Sdk
	 * In order:
	 *  - Get the path from the "qooxdoo.path" property
	 *  - Get the path from the QOOXDOO_PATH environment variable
	 *  - Resolve the path to current_dir/qooxdoo_sdk
	 */
	public static File resolveQooxdooSdkPath() {
		File qooxdooSdkPath;
		// Get path from property
		String qooxdooPathProperty = System.getProperty("qooxdoo.path");
		if (qooxdooPathProperty != null ) {
			qooxdooSdkPath = new File(qooxdooPathProperty);
			if (qooxdooSdkPath.exists() && qooxdooSdkPath.isDirectory()) {
				System.out.println("[INFO] Qooxdoo path resolved from property \'qooxdoo.path\'");
				return qooxdooSdkPath;
			} else {
				System.out.println("[WARN] Path \'"+qooxdooSdkPath.getAbsolutePath()+"\' does not exists or is not a directory");
			}
		}
		// Get path from env
		String qooxdooPathEnv = System.getenv().get("QOOXDOO_PATH");
		if (qooxdooPathEnv != null ) {
			qooxdooSdkPath = new File(qooxdooPathEnv);
			if (qooxdooSdkPath.exists() && qooxdooSdkPath.isDirectory()) {
				System.out.println("[INFO] Qooxdoo path resolved from environment variable \'QOOXDOO_PATH\'");
				return qooxdooSdkPath;
			} else {
				System.out.println("[WARN] Path \'"+qooxdooSdkPath.getAbsolutePath()+"\' does not exists or is not a directory");
			}
		}
		// Default
		class QxSdkFilter implements FilenameFilter
		{
		   public boolean accept ( File dir, String name ) {
		      if (	new File( dir, name ).isDirectory() 
		    		  && name.toLowerCase().startsWith("qooxdoo")
		    		  && name.toLowerCase().endsWith("sdk") ) 
		      {
		         return true;
		      }
		      return false;
		   }
		}
		File currentDirectory = new File( "." );
		String[] qxSdkDirs = currentDirectory.list( new QxSdkFilter() );
		for (String dir: qxSdkDirs) {
			qooxdooSdkPath = new File(dir);
			// TDODO: Get QOOXDOO_PATH from config.json if it exists
			if (qooxdooSdkPath.exists() && qooxdooSdkPath.isDirectory()) {
				System.out.println("[INFO] Found a qooxdoo sdk directory from current dir");
				return qooxdooSdkPath;
			} 
		}
		System.out.println("[ERROR] Can't resolve Qooxdoo Sdk path !");
		return null;
	}
	
	public static void help () {
		System.out.println("qOOXDOO bUILD tOOL");
		System.out.println();
		System.out.println("NEW PROJECT");
		System.out.println("    $ qbt create-application -t <type> -n <appName> [other_options]");
		System.out.println();
		System.out.println("JOBS");
		System.out.println(" Go to a qooxdoo application root directory and type:");
		System.out.println("    $ qbt \"job1,job2\" [generator_options]");
		System.out.println(" Or by using the interactive console");
		System.out.println("    $ qbt [generator_options]");
		System.out.println("      >>> job1()");
		System.out.println("      >>> job2()");
		System.out.println("      >>> jobs(\"job1,job2\") # in one shot");
		System.out.println("      >>> jobs(\"x\")         # full list of available jobs");
		System.out.println();
		System.out.println("EXAMPLES");
		System.out.println(" - Create a qooxdoo ria application");
		System.out.println("    $ qbt create-application -t gui -n myRiaApp");
		System.out.println(" - Compile a qooxdoo application (from it's root directory)");
		System.out.println("    $ qbt build");
		System.out.println(" or by using the interactive console:");
		System.out.println("    $ qbt console");
		System.out.println("      >>> build()");
		System.out.println();
		System.out.println("OTHERS");
		System.out.println("    $ qbt help");
		System.out.println("    $ qbt <qxPythonScriptName> [script_options]");
		
		
	}
	
	public static int console(String[] args) {
		System.out.print("[INFO] Entering interactive console, please wait...");
		try {
			QxEmbeddedJython qxjython = new QxEmbeddedJython(qooxdooSdkPath);
			InteractiveConsole c = qxjython.getQxInteractiveConsole(args);
			c.interact();
		} catch(Exception e) {
			System.out.println(e);
			return(1);
		}
		return(0);
		
	}
	
	

}

