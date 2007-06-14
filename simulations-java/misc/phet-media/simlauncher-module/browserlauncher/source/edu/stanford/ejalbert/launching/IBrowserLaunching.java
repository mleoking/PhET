/************************************************
    Copyright 2004,2005 Markus Gebhard, Jeff Chapman

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
// $Id: IBrowserLaunching.java,v 1.4 2005/10/13 19:27:59 jchapman0 Exp $
package edu.stanford.ejalbert.launching;

import java.util.List;

import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

/**
 * @author Markus Gebhard
 */
public interface IBrowserLaunching {
    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_FILE = "file";
    public static final String PROTOCOL_MAILTO = "mailto";
    /**
     * Identifier for the system's default browser.
     */
    public static final String BROWSER_DEFAULT = "Default";

    /**
     * Performs any initialization needed for the particular O/S.
     *
     * @throws BrowserLaunchingInitializingException
     */
    public void initialize()
            throws BrowserLaunchingInitializingException;

    /**
     * Opens the passed url in the system's default browser.
     *
     * @param urlString String
     * @throws UnsupportedOperatingSystemException
     * @throws BrowserLaunchingExecutionException
     * @throws BrowserLaunchingInitializingException
     */
    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException;

    /**
     * Allows user to target a specific browser. The names of
     * potential browsers can be accessed via the
     * {@link #getBrowserList() getBrowserList} method.
     * <p>
     * If the call to the requested browser fails, the code will
     * fail over to the default browser.
     *
     * @param browser String
     * @param urlString String
     * @throws UnsupportedOperatingSystemException
     * @throws BrowserLaunchingExecutionException
     * @throws BrowserLaunchingInitializingException
     */
    public void openUrl(String browser, String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException;

    /**
     * Returns a list of browsers to be used for browser targetting.
     * This list will always contain at least one item:
     * {@link #BROWSER_DEFAULT BROWSER_DEFAULT}.
     *
     * @return List
     */
    public List getBrowserList();
}
