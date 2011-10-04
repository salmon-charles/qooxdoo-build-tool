package org.qooxdoo.qooxdoo_java_tool;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptException;


/**
 * The Generator command line interface
 * 
 * @author charless
 * 
 */
public class QooxdooJavaTool  {
	
	public static Map<String,String> actions = new TreeMap<String,String>() {{
		put("new-gui", "create-application.py -t gui -n $ARG1");
		put("compile", "generator.py -c $ARG1/config.json build");
	}};
	
	public static File qooxdooSdkPath;
	
	public static void main(String[] args) throws ScriptException {
		// Resolve Qooxdoo Sdk path
		qooxdooSdkPath = resolveQooxdooSdkPath();
		System.out.println("QOOXDOO_PATH="+qooxdooSdkPath.getAbsolutePath());
		if (qooxdooSdkPath == null) {
			throw new ScriptException(
					"Can not find Qooxdoo sdk path; use the \'qooxdoo.sdk\' property or set the \'QOOXDOO_PATH\' environment variable");
		}
		// Parse args
		if (args.length ==0) {
			help();
			System.exit(1);
		}
		// Handles shortcuts
		if (actions.containsKey(args[0])) {
			if (args.length <= 1) {
				throw new ScriptException(
						"Missing one argument ! ("
						+ args[0] + " <missingArg>)");
			}
			String[] shortcutArgs = actions.get(args[0]).replaceAll("\\$ARG1",args[1]).split(" +");
			args = shortcutArgs;
		}
		String pythonScriptName = args[0];
		String pyScriptWithArgs = pythonScriptName;
		String [] pythonScriptArgs = new String[args.length-1];
		for (int i=1;i<args.length;i++) {
			pythonScriptArgs[i-1] = args[i];
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
			help();
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
			qx.run(pythonScriptName,pythonScriptArgs);
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
			if (qooxdooSdkPath.exists() && qooxdooSdkPath.isDirectory()) {
				System.out.println("[INFO] Found a qooxdoo sdk directory from current dir");
				return qooxdooSdkPath;
			} 
		}
		System.out.println("[ERROR] Can't resolve Qooxdoo Sdk path !");
		return null;
	}
	
	public static void help () {
		System.out.println("********************************************");
		System.out.println("* QooxdooJavaTool                          *");
		System.out.println("********************************************");
		System.out.println("QooxdooJavaTool <pythonScriptName> [options]");
		System.out.println("QooxdooJavaTool <shortcut> <applicationName>");
		
		// List possible actions
		System.out.println();
		System.out.println("Available shortcuts");
		System.out.println("===================");
		for (Map.Entry<String,String> entry : actions.entrySet()) {
			System.out.println(" * "+entry.getKey()+" = "+entry.getValue());
		}
		
		// List available scripts
		System.out.println();
		if (qooxdooSdkPath != null) {
			System.out.println("Available scripts");
			System.out.println("=================");
			File aScript = QxEmbeddedJython.resolvePythonScriptPath(qooxdooSdkPath,"script.py");
			class PyFilter implements FilenameFilter
			{
			   public boolean accept ( File dir, String name ) {
			      if (	new File( dir, name ).isFile() 
			    		  && name.toLowerCase().endsWith(".py") ) 
			      {
			         return true;
			      }
			      return false;
			   }
			}
			String[] availablePythonScripts = aScript.getParentFile().list( new PyFilter() );
			for (String script: availablePythonScripts) {
				System.out.println(" * "+script);
			}
		}
		
		// List possible actions
		System.out.println();
		System.out.println("Examples");
		System.out.println("========");
		
		
	}
}

