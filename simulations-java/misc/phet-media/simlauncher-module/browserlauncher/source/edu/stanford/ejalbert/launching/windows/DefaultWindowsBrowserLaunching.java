/************************************************
    Copyright 2004,2005 Markus Gebhard, Jeff Chapman, Chris Dance

    This file is part of BrowserLauncher2.

    BrowserLauncher2 is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BrowserLauncher2 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with BrowserLauncher2; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 ************************************************/
// $Id: DefaultWindowsBrowserLaunching.java,v 1.8 2005/12/28 19:19:57 jchapman0 Exp $
package edu.stanford.ejalbert.launching.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import edu.stanford.ejalbert.launching.IBrowserLaunching;
import net.sf.wraplog.AbstractLogger;

/**
 * @author Markus Gebhard, Jeff Chapman, Chris Dance
 */
abstract class DefaultWindowsBrowserLaunching
        extends WindowsBrowserLaunching {
    /**
     * Maps display name and exe name to {@link WindowsBrowser WindowsBrowser}
     * objects. Using name and exe as keys for backward compatiblity.
     */
    private Map browserNameAndExeMap = null;

    protected DefaultWindowsBrowserLaunching(AbstractLogger logger) {
        super(logger);
    }

    /**
     * The first parameter that needs to be passed into Runtime.exec() to open the default web
     * browser on Windows.
     */
    protected static final String FIRST_WINDOWS_PARAMETER = "/c";

    /** The second parameter for Runtime.exec() on Windows. */
    protected static final String SECOND_WINDOWS_PARAMETER = "start";

    /**
     * The third parameter for Runtime.exec() on Windows.  This is a "title"
     * parameter that the command line expects.  Setting this parameter allows
     * URLs containing spaces to work.
     */
    protected static final String THIRD_WINDOWS_PARAMETER = "\"\"";

    /**
     * Returns the windows arguments for launching a default browser.
     *
     * @param protocol String
     * @param urlString String
     * @return String[]
     */
    protected abstract String[] getCommandArgs(String protocol,
                                               String urlString);

    /**
     * Returns the windows arguments for launching a specified browser.
     *
     * @param protocol String
     * @param browserName String
     * @param urlString String
     * @return String[]
     */
    protected abstract String[] getCommandArgs(String protocol,
                                               String browserName,
                                               String urlString);

    /**
     * Accesses the Windows registry to look for browser exes. The
     * browsers search for are in the browsersToCheck list. The returned
     * map will use display names and exe names as keys to the
     * {@link WindowsBrowser WindowsBrowser} objects.
     *
     * @param browsersToCheck List
     * @return Map
     */
    protected Map getAvailableBrowsers(List browsersToCheck) {
        logger.debug("entering getAvailableBrowsers");
        logger.debug("browsers to check: " + browsersToCheck);
        Map browsersAvailable = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        /*
         * We determine the list of available browsers by looking for the browser
         * executables defined on the path.  "cmd start" determines the location of the
         * executable by looking at paths defined in the registry key:
         *   HKEY_LOCAL_MACHINE\Software\Microsoft\Windows\CurrentVersion\App Paths\
         *
         * To avoid native code we expect this section of the registry to a file using:
         *   regedit.exe /E
         * Not the most efficient method but works on all versions of Windows.
         */
        try {

            File tmpFile = File.createTempFile("bl2-app-paths", ".reg");
            String[] cmdArgs = new String[] {
                               "regedit.exe",
                               "/E",
                               "\"" + tmpFile.getAbsolutePath() + "\"",
                               "\"HKEY_LOCAL_MACHINE\\Software\\Microsoft\\Windows\\CurrentVersion\\App Paths\""};
            Process process = Runtime.getRuntime().exec(cmdArgs);
            int exitCode = -1;
            try {
                exitCode = process.waitFor();
            }
            catch (InterruptedException e) {
                logger.error("InterruptedException exec'ing regedit.exe: " +
                             e.getMessage());
            }

            if (exitCode != 0) {
                logger.error(
                        "Unable to exec regedit.exe to extract available browsers.");
                tmpFile.delete();
                return browsersAvailable;
            }

            /*
             * Open the newly created registry file.  First we read the first two bytes
             * of the file to check format/encoding (i.e. look for UTF-16 magic number).
             * Walk through the registry key App Paths looking for the browser executables
             * defined on the path.
             */
            FileInputStream fis = new FileInputStream(tmpFile);
            byte magic[] = new byte[2];
            fis.read(magic);
            fis.close();
            InputStreamReader in = null;
            if (magic[0] == -1 && magic[1] == -2) { // magic number 0xff 0xfe
                in = new InputStreamReader(new FileInputStream(tmpFile),
                                           "UTF-16");
            }
            else {
                in = new InputStreamReader(new FileInputStream(tmpFile));
            }
            BufferedReader reader = new BufferedReader(in);
            String line;
            while ((line = reader.readLine()) != null) {
                Iterator iter = browsersToCheck.iterator();
                boolean foundBrowser = false;
                while (iter.hasNext() && !foundBrowser) {
                    WindowsBrowser winBrowser = (WindowsBrowser)iter.next();
                    String exeName = winBrowser.getExe().toLowerCase() + ".exe";
                    if (line.toLowerCase().lastIndexOf(exeName) >= 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(line);
                            logger.debug("Adding browser " +
                                         winBrowser.getDisplayName() +
                                         " to available list.");
                        }
                        // adding display and exe for backward compatibility and
                        // ease of use if someone passes in the name of an exe
                        browsersAvailable.put(winBrowser.getDisplayName(), winBrowser);
                        browsersAvailable.put(winBrowser.getExe(), winBrowser);
                        iter.remove(); // already found so remove from check list.
                        foundBrowser = true;
                    }
                }
            }
            in.close();
            tmpFile.delete();
        }
        catch (IOException e) {
            logger.error("Error listing available browsers: " + e.getMessage());
        }
        return browsersAvailable;
    }

    /**
     * Handles lazy instantiation of available browser map.
     */
    private void initBrowserMap() {
        synchronized(DefaultWindowsBrowserLaunching.class) {
            if (browserNameAndExeMap == null) {
                // create list of standard browsers to check
                List browsersToCheck = new ArrayList();
                browsersToCheck.add(WindowsBrowser.FIREFOX);
                browsersToCheck.add(WindowsBrowser.IEXPLORER);
                browsersToCheck.add(WindowsBrowser.MOZILLA);
                // pull additional browsers from system property

                browserNameAndExeMap = getAvailableBrowsers(browsersToCheck);
            }
        }
    }

    /**
     * Returns map of browser names and exe names to
     * {@link WindowsBrowser WindowsBrowser} objects.
     * <p>
     * This is the preferred method for accessing the browser name and exe map.
     * @return Map
     */
    protected final Map getBrowserMap() {
        initBrowserMap();
        return browserNameAndExeMap;
    }

    /* ----------------- from IBrowserLaunching -------------------- */

    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        try {
            logger.info(urlString);
            String protocol = getProtocol(urlString);
            logger.info(protocol);
            String[] args = getCommandArgs(protocol,
                                           urlString);
            if (logger.isDebugEnabled()) {
                logger.debug(getArrayAsString(args));
            }
            Process process = Runtime.getRuntime().exec(args);
            // This avoids a memory leak on some versions of Java on Windows.
            // That's hinted at in <http://developer.java.sun.com/developer/qow/archive/68/>.
            process.waitFor();
            process.exitValue();
        }
        catch (Exception e) {
            logger.error("fatal exception", e);
            throw new BrowserLaunchingExecutionException(e);
        }
    }

    public void openUrl(String browser, String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        if (IBrowserLaunching.BROWSER_DEFAULT.equals(browser) ||
            browser == null) {
            logger.info("default or null browser target");
            openUrl(urlString);
            return; // exit the method
        }
        Map browserMap = getBrowserMap();
        WindowsBrowser winBrowser = (WindowsBrowser)browserMap.get(browser);
        if(winBrowser == null) {
            logger.info("the available browsers list does not contain: " + browser);
            logger.info("falling through to non-targetted openUrl");
            openUrl(urlString);
        }
        else {
            boolean successfullLaunch = false;
            try {
                logger.info(winBrowser.getDisplayName());
                logger.info(urlString);
                String protocol = getProtocol(urlString);
                logger.info(protocol);
                String[] args = getCommandArgs(protocol,
                                               winBrowser.getExe(),
                                               urlString);
                if (logger.isDebugEnabled()) {
                    logger.debug(getArrayAsString(args));
                }
                Process process = Runtime.getRuntime().exec(args);
                // This avoids a memory leak on some versions of Java on Windows.
                // That's hinted at in <http://developer.java.sun.com/developer/qow/archive/68/>.
                process.waitFor();
                successfullLaunch = process.exitValue() == 0;
            }
            catch (Exception e) {
                logger.error("fatal exception", e);
                successfullLaunch = false;
            }
            if (!successfullLaunch) {
                logger.debug("falling through to non-targetted openUrl");
                openUrl(urlString);
            }
        }
    }

    /**
     * Returns a list of browsers to be used for browser targetting.
     * This list will always contain at least one item--the BROWSER_DEFAULT.
     *
     * @return List
     */
    public List getBrowserList() {
        Map browserMap = getBrowserMap();
        List browsers = new ArrayList();
        browsers.add(IBrowserLaunching.BROWSER_DEFAULT);
        // exes are present in the map as well as display names
        Iterator iter = browserMap.keySet().iterator();
        while(iter.hasNext()) {
            String key = (String)iter.next();
            WindowsBrowser winBrowser = (WindowsBrowser)browserMap.get(key);
            if(key.equals(winBrowser.getDisplayName())) {
                browsers.add(winBrowser.getDisplayName());
            }
        }
        return browsers;
    }
}
