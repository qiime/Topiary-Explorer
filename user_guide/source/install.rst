.. _install:

**************************
Installing TopiaryExplorer
**************************

Requirements
------------
**TopiaryExplorer requires Java 6.** 

To check your version of Java open a command terminal and run the following::

	java -version

If you are running a version less than 1.6.x (i.e., Java 6), you'll need to upgrade to Java 6 before you can use TopiaryExplorer. 
 * If you're running Mac OS 10.5 (Leopard), you can find `update instructions here <http://support.apple.com/kb/TS3489>`_. On Mac OS 10.5 you likely already have Java 6 installed, but are not using it by default. 
 * If you're not using OS X, you can `download Java 6 here <http://www.oracle.com/technetwork/java/javase/downloads/jdk-6u25-download-346242.html>`_. Follow the instructions on the Oracle website to ensure that you have Java installed correctly.

TopiaryExplorer additionally requires that you have at least 2GB of RAM. 

Installing the release version of TopiaryExplorer
-------------------------------------------------

To install the release version of TopiaryExplorer `download the release version here <https://sourceforge.net/projects/topiaryexplorer/files/releases/te1.0.tgz/download>`_. After downloading, change to the directory where you've downloaded the file and from the command line unzip with the command::

	tar -xzf te1.0.tgz
	cd te_current
	
Then to run TopiaryExplorer, run the command::

	javaws topiaryexplorer.jnlp

Installing the development version of TopiaryExplorer
-----------------------------------------------------

Alternatively you can work with the development version of TopiaryExplorer by checking the latest code out of the svn repository. Note that you may experience more bugs in the development version, but you'll also have access to the latest features. Instead of downloading the release version, you'll run the following commands::

	svn co https://topiaryexplorer.svn.sourceforge.net/svnroot/topiaryexplorer/trunk/main TopiaryExplorer
	cd TopiaryExplorer

Then to run TopiaryExplorer, run the command::

	javaws topiaryexplorer.jnlp

Alternate instructions when working with very large trees or on systems with less than 2GB of RAM
-------------------------------------------------------------------------------------------------------------

If you're working with very large trees 2GB of RAM (the default used by TopiaryExplorer) may not be sufficient. In that case you may want to start different versions of the jnlp which specifically use more RAM. We include versions that use 4GB and 8GB of RAM, which you can start with these commands (respectively)::

	javaws topiaryexplorer_4000mb.jnlp
	javaws topiaryexplorer_8000mb.jnlp
	
If you have less that 2GB of RAM in your system, you can work with a version of TopiaryExplorer that requires only 1GB of RAM, but you will likely begin to notice performance issues. To work with that version open TopiaryExplorer with the command::

	javaws topiaryexplorer_1000mb.jnlp

.. _newick: http://en.wikipedia.org/wiki/Newick_format
.. _QIIME: http://qiime.org
