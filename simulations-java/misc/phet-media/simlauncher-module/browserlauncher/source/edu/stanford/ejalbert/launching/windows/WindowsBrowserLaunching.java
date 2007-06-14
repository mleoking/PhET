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
// $Id: WindowsBrowserLaunching.java,v 1.3 2005/12/22 18:07:13 jchapman0 Exp $
package edu.stanford.ejalbert.launching.windows;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.launching.IBrowserLaunching;
import net.sf.wraplog.AbstractLogger;

/**
 * @author Markus Gebhard
 */
public abstract class WindowsBrowserLaunching
        implements IBrowserLaunching {
    protected final AbstractLogger logger; // in ctor

    protected WindowsBrowserLaunching(AbstractLogger logger) {
        this.logger = logger;
    }

    protected String getArrayAsString(String[] array) {
        return Arrays.asList(array).toString();
    }

    protected String getProtocol(String urlString)
            throws MalformedURLException {
        URL url = new URL(urlString);
        return url.getProtocol();
    }

    /* ----------------- from IBrowserLaunching -------------------- */

    public void initialize()
            throws BrowserLaunchingInitializingException {
    }
}
