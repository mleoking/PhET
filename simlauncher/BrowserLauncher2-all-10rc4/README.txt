Introduction
------------

BrowserLauncher2, a continuation of the BrowserLauncher project, 
is a library that facilitates openning a browser from a Java 
application and directing the browser to a supplied url. In 
most cases the browser openned will be the user's default browser.

BrowserLauncher2 has been released under the LGPL. 
Read the COPYING.txt file for licensing information.

Project Founder (BrowserLauncher): 
Eric Albert

Project Founder (BrowserLauncher2): 
Jeff Chapman sdvalidator@yahoo.com

Contributors:  
Thomas Aglassinger
Jeff Chapman
Chris Dance
Markus Gebhard
Olivier Hochreutiner
Morgan Schweers

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
For more information on OSI, see http://www.opensource.org

System Requirements
-------------------

BrowserLauncher2 is written entirely in Java. The libraries have been 
compiled using JDK 1.4. Operating System support is ongoing. The library 
supports various flavors of Mac, Windows, and Unix/Linux.

Using the Library
-----------------

The preferred method for using the BrowserLauncher2 api is to create an 
instance of BrowserLauncher (edu.stanford.ejalbert.BrowserLauncher) and 
invoke the method: public void openURLinBrowser(String urlString).

If the application will be invoking urls often, it might be useful to wrap 
the BrowserLauncher instance with a singleton or use some mechanism to cache it.

The call to openURLinBrowser() should be executed in a separate thread from 
the application's main/event thread. Applications can create an instance of 
BrowserLauncherRunner (edu.stanford.ejalbert.BrowserLauncherRunner) and pass 
it to a Thread. The sample code below is taken from the test application 
(edu.stanford.ejalbert.BrowserLauncher.BrowserLauncherTestApp) which can be 
used as a reference application.

BrowserLauncherErrorHandler errorHandler = new TestAppErrorHandler(debugTextArea);
BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher, urlString, errorHandler);
Thread launcherThread = new Thread(runner);
launcherThread.start();

Logging
---------
BrowserLauncher2 uses a subset of WrapLog 1.1 for logging. If you do not specify a logger 
instance, a default logger (NoneLogger) will be used. The default logger does not log 
anything.

For more information on using WrapLog, see http://sourceforge.net/projects/wraplog.

