/************************************************
    Copyright 2004 Markus Gebhard, Jeff Chapman

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
// $Id: MacOs3_1BrowserLaunching.java,v 1.2 2005/10/07 20:01:08 jchapman0 Exp $
package edu.stanford.ejalbert.launching.macos;

import java.lang.reflect.Method;

import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.launching.IBrowserLaunching;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Markus Gebhard
 */
public class MacOs3_1BrowserLaunching
        implements IBrowserLaunching {
    private Method openURL;

    public void initialize()
            throws BrowserLaunchingInitializingException {
        try {
            Class mrjFileUtilsClass = Class.forName(
                    "com.apple.mrj.MRJFileUtils");
            openURL = mrjFileUtilsClass.getDeclaredMethod("openURL",
                    new Class[] {String.class});
        }
        catch (Exception e) {
            throw new BrowserLaunchingInitializingException(e);
        }
    }

    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        try {
            openURL.invoke(null, new Object[] {urlString});
        }
        catch (Exception e) {
            throw new BrowserLaunchingExecutionException(e);
        }
    }

    public void openUrl(String browser, String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        //logger.debug("falling through to non-targetted openUrl");
        openUrl(urlString);
    }

    /**
     * Returns a list of browsers to be used for browser targetting.
     * This list will always contain at least one item--the BROWSER_DEFAULT.
     *
     * @return List
     */
    public List getBrowserList() {
        List browserList = new ArrayList(1);
        browserList.add(IBrowserLaunching.BROWSER_DEFAULT);
        return browserList;
    }
}
