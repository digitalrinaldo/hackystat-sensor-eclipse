**Table of Contents**


# 1.0 Prerequisites #

This sensor is compatible with Eclipse 3.5 (Galileo).  It is probably not downward compatible.

# 2.0 Downloading #

Go to the [Hackystat Home Page](http://www.hackystat.org)  and download the "Featured Download" with "sensors" in its name. (There is also a "services" featured download; you don't need that one.)

Unzip this file. Assuming you've downloaded the 8.3.1030 release, you will get a directory structure similar to the following:

```
hackystat-8.3.1030-sensors-bin/
                               hackystat-sensor-eclipse/
                                                      org.hackystat.sensor.eclipse_8.3.v20091030.jar

                             :
```

There will also be directories for other sensors as well.

# 3.0 Installation #

## 3.1 Installation for Windows ##

To install the Hackystat Eclipse sensor on Windows, exit any running Eclipse processes, then copy the org.hackystat.sensor.eclipse-(version).jar file illustrated above to the Eclipse plugins/ directory.

Then restart Eclipse.

The sensor is now installed. The next step is to set your sensor preferences as described in Section 4.0.

## 3.2 Installation for Macintosh (Tiger and Leopard) ##

Installation for the Macintosh is greatly complicated by the lack of support for Java 1.6 on 32 bit Macintoshes running Tiger or Leopard.  What this means is that the Eclipse program by default runs under a Java 1.5 virtual machine on Macintosh.  This will hopefully change with the Snow Leopard OS which is expected to support both 32 bit and 64 bit Java 1.6. When that happens, new releases of Eclipse for the Macintosh should run under 1.6, enabling Macintosh installation to be as easy as Windows installation.

For now, however, life is not so wonderful.  Macintosh installation involves the following steps:

### 3.2.1 Download and install a JAXB 2.1 release ###

Go to https://jaxb.dev.java.net/.  Download the latest stable JAXB 2.1 release, which is a file like JAXB2\_20090708.jar.  To install it, you must run 'java -jar JAXB2\_20090708.jar'.  Don't just unzip the file.  You must run the jar file in this way in order to display a license agreement, after that a directory will be created with the actual sources. This directory is called something like jaxb-ri-20090708.

### 3.2.2  Determine the location of the endorsed directory used by Eclipse ###

In Eclipse, open: Eclipse | About Eclipse SDK | Installation Details | Configuration

Now scroll down until you find the property "java.endorsed.dirs".  Make a note of its value.  It should be something like /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/endorsed.

### 3.3.3 Copy JAXB files to the endorsed directory ###

Copy the following files from the JAXB directory to the endorsed directory:
  * activation.jar
  * jaxb-api.jar
  * jax-impl.jar
  * jsr173\_1.0\_api.jar

Note that this copy operation requires administrator status because of the location of the endorsed directory.  One easy way to do the copy is to open a shell window, then type something like the following:

```
cd /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/endorsed
sudo cp ~/jaxb-ri-20090708/lib/activation.jar .
sudo cp ~/jaxb-ri-20090708/lib/jaxb-api.jar .
sudo cp ~/jaxb-ri-20090708/lib/jaxb-impl.jar .
sudo cp ~/jaxb-ri-20090708/lib/jsr173_1.0_api.jar .
```

Note that you should modify these commands based upon the actual location of the endorsed directory and the JAXB installation on your system.

### 3.3.4 Install the sensor ###

Finally, you can install the sensor following the instructions in 3.1.

## 3.4 Installation for Linux ##

For Linux installation, follow the Windows instructions if your Eclipse runs under 1.6, or follow the Macintosh instructions if your Eclipse runs under 1.5.  To figure out what your Eclipse is running under, open the same Configuration window as noted above in Section 3.2.2, then scroll down until you see the value of the java.home property.


# 4.0 Sensor Configuration #

You will need to provide a Hackystat SensorBase host, a SensorBase account (i.e. email address), and a password in the Preference Settings for the Hackystat Eclipse Sensor. This generally involves registering with a SensorBase host.  Contact your Hackystat administrator if you are unclear on how to do this.

Once you have these three items of information, select the Window | Preferences menu, then select "Hackystat Sensor"  to display its Preference page, as illustrated below:

![http://hackystat-sensor-eclipse.googlecode.com/svn/wiki/preferences.gif](http://hackystat-sensor-eclipse.googlecode.com/svn/wiki/preferences.gif)

The Hackystat Eclipse Sensor will check the validity of your input after you click the **Apply** and/or **OK** buttons. The new preferences will take effect the next time you start up Eclipse.

Note that the Eclipse sensor buffers data locally and "autosends" it after
the interval in minutes specified in this preferences page. Any buffered
data is always sent when Eclipse exits.  When testing the sensor, you may
need to exit Eclipse in order to force data transmission.

Use the AutoSend Interval preference to change the frequency with which the Eclipse sensor sends data. In general, the default (5 minutes) is reasonably frequent without incurring noticable overhead on the IDE.

Note that the Eclipse sensor does not read any information from the sensorshell.properties file.

# 5.0 Verify your installation #

To confirm that data is being received by the SensorBase, we recommend that you use the Sensor Data page of the ProjectBrowser application.

After installing the Eclipse sensor, open Eclipse, work briefly, and then exit Eclipse (to guarantee that the buffered data has been sent).

Then login to the ProjectBrowser, select the "Default" project, the current month and year, and press submit.  You should see a screen similar to the following appear:

![http://hackystat-sensor-eclipse.googlecode.com/svn/wiki/sensordata.gif](http://hackystat-sensor-eclipse.googlecode.com/svn/wiki/sensordata.gif)

This screen shows that DevEvent data was sent by Eclipse and this user on November 15, 2008.

Clicking on the "15: Eclipse" link pops up a modal window with details on the 15 sensor data instances:

![http://hackystat-sensor-eclipse.googlecode.com/svn/wiki/sensordata2.gif](http://hackystat-sensor-eclipse.googlecode.com/svn/wiki/sensordata2.gif)

If the ProjectBrowser does not show your data, then recheck your settings in the Eclipse Preferences page.






