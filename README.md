[Setup]: http://qxmaven.charless.org/doku.php?id=artifacts:qooxdoo-build-tool:setup
[qooxdoo download page]: http://qooxdoo.org/download
[qbt home page]: http://qxmaven.charless.org/doku.php?id=artifacts:qooxdoo-build-tool:start

# qbt - Qooxdoo Build Tool

Qbt is a build tool for Qooxdoo applications, that requires Java 1.6 (or later) only (no need of an external python interpreter).

It is a wrapper to the qooxdoo python tool chain, and so provides the same functionalities.

It is shipped with an interactive qooxdoo-jython console (REPL), to build and manage your application easily. 

# Getting started

1. Install a binary release of qbt (see [Setup])

2. Download a qooxdoo sdk (see [qooxdoo download page])

3. Create an application, run build jobs

			$ export QOOXDOO_PATH=/path/to/qooxdoo/sdk
			$ qbt create-application -t gui -n myRiaApp
			$ cd myRiaApp
			$ qbt "build, test, api"
			
Visit the [qbt home page] project page for full documentation.