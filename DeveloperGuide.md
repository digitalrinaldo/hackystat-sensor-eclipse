This document introduces how to build a release of the Hackystat Eclipse sensor from source using Eclipse's plug-in development environment.

# 1.0 Install required package #

The Hackystat Eclipse Sensor project is implemented as an Eclipse "feature".
You will need Eclipse to build, debug and run the system.

| Package | Version | Remark  |
|:--------|:--------|:--------|
| [Java](http://java.sun.com/)    | 1.5 or 1.6    | Eclipse must run on JRE 5 or 6 |
| [Eclipse](http://www.eclipse.org) | [Version 3.3](http://www.eclipse.org/downloads/)    | Be sure to download Eclipse "classic" in order to get the PDE environment. |
| [Sensorshell](http://code.google.com/p/hackystat-sensor-shell/) | 8.0.`*`  | For sensorshell.jar |

The Hackystat Eclipse Sensor also provides a mechanism to build it automatically headlessly without starting the Eclipse IDE. The automation does not come for cheap. More library files are needed for headless build.
| [JUnit](http://www.junit.org/) | 4.3.1 | JUNIT\_HOME | D:\Tool\junit4.3.1 |
|:-------------------------------|:------|:------------|:-------------------|
| [PMD](http://pmd.sourceforge.net/)| 3.9   | PMD\_HOME   | D:\Tool\pmd-3.9    |
| [Ant](http://ant.apache.org/)  | 1.7   | ANT\_HOME   |  C:\develop\apache-ant-1.7.0 |
| [Checkstyle](http://checkstyle.sourceforge.net/) | 4.3   | CHECKSTYLE\_HOME |  D:\Tool\checkstyle-4.3 |
| [Emma](http://emma.sourceforge.net/)| 2.0.5312| EMMA\_HOME  | D:\Tool\emma-2.0.5312 |
| [FindBugs](http://findbugs.sourceforge.net/) | 1.2.0 	| FINDBUGS\_HOME | D:\Tool\findbugs-1.2.1 |
| [Hackystat Utilities](http://code.google.com/p/hackystat-utilities) | 8.0.1002 | HACKYSTAT\_UTILITIES\_HOME 	| C:\develop\hackystat-utilities-8.0.1002 |
| [Hackystat Sensorshell](http://code.google.com/p/hackystat-sensor-shell) | 8.0.1002 | HACKYSTAT\_SENSORSHELL\_HOME | C:\develop\hackystat-sensor-shell-8.0.1002 |
| [Hackystat SensorBase](http://code.google.com/p/hackystat-sensorbase-uh) | 8.0.1002 | HACKYSTAT\_SENSORBASE\_HOME | D:\svn\hackystat-sensorbase-uh-8.0.606 |


# 2.0 Check out the sources #

Check out the sources from the trunk of the Google Project repository. The URL is:

http://hackystat-sensor-eclipse.googlecode.com/svn/trunk/

The [Hackystat Eclipse Sensor Source Page](http://code.google.com/p/hackystat-sensor-eclipse/source) has more instructions.

# 3.0 Build a release using PDE #

In the checked out sources, you should see four folders named feature, images, plugin, and site.

| feature/ | Provides the sensor-eclipse-feature project that encapsulates the sensor as an Eclipse feature. |
|:---------|:------------------------------------------------------------------------------------------------|
| images/  | Provides screenshots used by [UserGuide](http://code.google.com/p/hackystat-sensor-eclipse/wiki/UserGuide) or [DeveloperGuide](http://code.google.com/p/hackystat-sensor-eclipse/wiki/DeveloperGuide). |
| site/    | Provides the sensor-eclipse-site project that enables users to install the sensor using the Eclipse **Update Manager**. (See [UserGuide](http://code.google.com/p/hackystat-sensor-eclipse/wiki/UserGuide)). |
| plugin/  | Provides the sensor-eclipse-plugin project that implements the actual Eclipse sensor for Hacksytat 8. |


## 3.1 Import projects ##

Import the above three predefined projects into your Eclipse IDE. Note that you must open the feature and plugin projects in order to build a release.

## 3.2 Update the Version Number ##

Open the sensor-eclipse-feature project, and do the following:

  * Open the feature.xml file. Be sure to use the "Feature Manifest" editor.
  * Edit the Version field appropriately. An example Version is "8.0.904".
  * Save the file.

Open the sensor-eclipse-plugin project, and do the following:

  * Open the plugin.xml file. Be sure to use the "Plugin Manifest" editor.
  * Edit the Version field appropriately. An example Version is "8.0.904".
  * Save the file.

Open the sensor-eclipse-site project, and do the following:

  * Open the site.xml file. Be sure to use the "Site Manifest" editor.
  * In the "Site Map" tab, under "Managing the Site", there is a window containing the single feature associated with the Eclipse plugin.  That feature will now have an out of date version number.   To fix this, right click on the old feature and select "Remove" to remove it.  Now click "Add Feature..." and a window will appear that should contain the org.hackystat.sensor.eclipse feature with the new version number. Select that feature and click OK to add it to this window.
  * Save the file.

Open dist.build.xml located on the top directory (with any editor), and do the following:
  * Locate a property that looks like the following line
> > `<property name="version" value="8.0.923"/>`
  * Update the version number.

## 3.3 Update the sensorshell.jar file ##

In general, you will want to update the sensorshell.jar file to the most recent release.  To do this, get the most
recent release of sensorshell.jar and place it in the plugin/lib directory.  For more information, see
the [SensorShell Developer Guide](http://code.google.com/p/hackystat-sensor-shell/wiki/DeveloperGuide).

## 3.4 Build a release ##

Once the version number and sensorshell.jar file are updated, build the plugin by doing the following:

  * Open the site.xml file in the sensor-eclipse-site project. Make sure you open this file using the "Site Manifest" Editor.  You will see this file displayed using the "Update Site Map" page.
  * Click the **Build** button or **BuildAll** button to build a release. Eclipse will generate two jar files that are located in the features/ and plugins/ directories of the sensor-eclipse-site project.
  * To make these files available to the community, you must SVN add the new jar files, then SVN commit the entire Eclipse sensor project.

## 3.5 Build a release headlessly ##
  * Define the environment variable ECLIPSE\_HOME, then open a DOS window.
  * Build the sensor headlessly using the following command `ant -f dist.build.xml`, then the release zip will be generated in <where you are>/build/dist.
  * Add a new download in Google Hosting.
  * Upload the release zip file.

# 4.0 Debug the sensor as an Eclipse application #

You can run/debug the sensor as an Eclipse application if desired.
  * Start sensorbase service (See [UserGuide](http://code.google.com/p/hackystat-sensorbase-uh/wiki/DeveloperGuide) for instructions)
  * Open the sensor-eclipse-plugin project
  * In the main menu, select **Run** | **Run As** || **Eclipse Application** to launch a new Eclipse for debugging.
  * Do some development in the newly opened Eclipse IDE. You can check the Hackystat log, console output, or Eclipse's Error Log for troubleshooting. Or you can launch the plugin   in debug mode.

