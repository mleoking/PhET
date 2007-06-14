/************************************************
    Copyright 2005 Olivier Hochreutiner, Jeff Chapman

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
// $Id: SunOSBrowserLaunching.java,v 1.1 2005/10/28 18:52:01 jchapman0 Exp $
package edu.stanford.ejalbert.launching.misc;

import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import net.sf.wraplog.AbstractLogger;

/**
 * Launches a default browser on SunOS Unix systems using the sdtwebclient
 * command.
 *
 * @author Olivier Hochreutiner
 */
public class SunOSBrowserLaunching
        extends UnixNetscapeBrowserLaunching {

    static final StandardUnixBrowser SDT_WEB_CLIENT = new StandardUnixBrowser(
            "SdtWebClient",
            "sdtwebclient");

    public SunOSBrowserLaunching(AbstractLogger logger) {
        super(logger);
    }

    /**
     * This implementation will cause the calling thread to block until the
     * browser exits. Calling methods MUST wrap the call in a separate thread.
     *
     * @param urlString String
     * @throws BrowserLaunchingExecutionException
     */
    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        try {
            logger.info(urlString);
            logger.info(SDT_WEB_CLIENT.getBrowserName());
            Process process = Runtime.getRuntime().exec(
                    SDT_WEB_CLIENT.getArgsForStartingBrowser(urlString));
            process.waitFor();
        }
        catch (Exception e) {
            throw new BrowserLaunchingExecutionException(e);
        }
    }
}
