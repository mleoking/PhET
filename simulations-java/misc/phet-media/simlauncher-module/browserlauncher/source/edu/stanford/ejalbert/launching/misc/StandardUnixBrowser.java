/************************************************
    Copyright 2004,2005 Jeff Chapman

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
// $Id: StandardUnixBrowser.java,v 1.5 2005/10/28 18:57:32 jchapman0 Exp $
package edu.stanford.ejalbert.launching.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.sf.wraplog.AbstractLogger;

class StandardUnixBrowser
        implements UnixBrowser {
    /**
     * name of browser for user display
     */
    private String browserName; // in ctor
    /**
     * name of browser used to invoke it
     */
    private String browserArgName; // in ctor
    /**
     * The shell parameters for Netscape that opens a given URL in an already-open copy of Netscape
     * on many command-line systems.
     */
    private static final String REMOTE_PARAMETER = "-remote";
    private static final String OPEN_PARAMETER_START = "openURL(";
    private static final String OPEN_PARAMETER_END = ")";

    static final StandardUnixBrowser NETSCAPE = new StandardUnixBrowser(
            "Netscape",
            "netscape");
    static final StandardUnixBrowser MOZILLA = new StandardUnixBrowser(
            "Mozilla",
            "mozilla");
    static final StandardUnixBrowser FIREFOX = new StandardUnixBrowser(
            "FireFox",
            "firefox");
    // on some systems, firefox is referenced as mozilla-firefox
    static final StandardUnixBrowser MOZILLA_FIREFOX = new StandardUnixBrowser(
            "FireFox",
            "mozilla-firefox");
    static final StandardUnixBrowser KONQUEROR = new StandardUnixBrowser(
            "Konqueror",
            "konqueror");
    static final StandardUnixBrowser OPERA = new StandardUnixBrowser(
            "Opera",
            "opera");
    StandardUnixBrowser(String browserName, String browserArgName) {
        this.browserArgName = browserArgName;
        this.browserName = browserName;
    }

    public String toString() {
        return browserName;
    }

    /* ------------------------- from UnixBrowser ------------------------ */

    public String getBrowserName() {
        return browserName;
    }

    public String[] getArgsForOpenBrowser(String urlString) {
        return new String[] {
                browserArgName,
                REMOTE_PARAMETER,
                OPEN_PARAMETER_START + urlString + OPEN_PARAMETER_END};
    }

    public String[] getArgsForStartingBrowser(String urlString) {
        return new String[] {
                browserArgName, urlString};
    }

    /**
     * Returns true if the browser is available, ie which command finds it.
     *
     * @param logger AbstractLogger
     * @return boolean
     */
    public boolean isBrowserAvailable(AbstractLogger logger) {
        boolean isAvailable = false;
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"which",
                    browserArgName});
            InputStream errStream = process.getErrorStream();
            InputStream inStream = process.getInputStream();
            int exitCode = process.waitFor();
            BufferedReader errIn
                    = new BufferedReader(new InputStreamReader(errStream));
            BufferedReader in
                    = new BufferedReader(new InputStreamReader(inStream));
            String whichOutput = in.readLine();
            String whichErrOutput = errIn.readLine();
            in.close();
            errIn.close();
            if(whichOutput != null) {
                logger.debug(whichOutput);
            }
            if(whichErrOutput != null) {
                logger.debug(whichErrOutput);
            }
            // this doesn't work on SunOS unix systems but does on Linux systems
            //isAvailable = exitCode == 0;
            isAvailable = whichOutput != null &&
                          whichOutput.startsWith("/");
        }
        catch (IOException ex) {
            logger.error("io error executing which command", ex);
        }
        catch (InterruptedException ex) {
            logger.error("interrupted executing which command", ex);
        }
        return isAvailable;
    }
}
