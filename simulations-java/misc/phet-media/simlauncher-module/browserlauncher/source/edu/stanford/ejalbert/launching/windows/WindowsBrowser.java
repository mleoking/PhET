/************************************************
    Copyright 2005 Jeff Chapman

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
// $Id: WindowsBrowser.java,v 1.2 2005/12/28 19:19:57 jchapman0 Exp $
package edu.stanford.ejalbert.launching.windows;

/**
 * Encapsulates information on a Windows browser.
 *
 * @author Jeff Chapman
 * @version 1.0
 */
public class WindowsBrowser {
    private final String displayName; // in ctor
    private final String exe; // in ctor

    // common windows browsers
    static final WindowsBrowser IEXPLORER = new WindowsBrowser(
            "IE",
            "iexplore");
    static final WindowsBrowser FIREFOX = new WindowsBrowser(
            "Firefox",
            "firefox");
    static final WindowsBrowser MOZILLA = new WindowsBrowser(
            "Mozilla",
            "mozilla");

    WindowsBrowser(String displayName, String exe) {
        this.displayName = displayName;
        this.exe = exe;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getExe() {
        return exe;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(displayName);
        buf.append(' ');
        buf.append(exe);
        return buf.toString();
    }
}
