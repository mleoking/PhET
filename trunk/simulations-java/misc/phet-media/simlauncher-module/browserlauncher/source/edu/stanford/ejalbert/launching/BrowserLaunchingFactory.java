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
// $Id: BrowserLaunchingFactory.java,v 1.3 2005/10/28 18:54:35 jchapman0 Exp $
package edu.stanford.ejalbert.launching;

import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import edu.stanford.ejalbert.launching.macos.MacOs2_0BrowserLaunching;
import edu.stanford.ejalbert.launching.macos.MacOs2_1BrowserLaunching;
import edu.stanford.ejalbert.launching.macos.MacOs3_0BrowserLaunching;
import edu.stanford.ejalbert.launching.macos.MacOs3_1BrowserLaunching;
import edu.stanford.ejalbert.launching.misc.UnixNetscapeBrowserLaunching;
import edu.stanford.ejalbert.launching.misc.SunOSBrowserLaunching;
import edu.stanford.ejalbert.launching.windows.Windows2000BrowserLaunching;
import edu.stanford.ejalbert.launching.windows.Windows9xBrowserLaunching;
import edu.stanford.ejalbert.launching.windows.WindowsNtBrowserLaunching;
import net.sf.wraplog.AbstractLogger;

/**
 * Factory for determining the OS and returning the appropriate version
 * of IBrowserLaunching. The factory uses
 * {@link System#getProperty(String) System.getProperty("os.name")} to
 * determine the OS.
 *
 * @author Markus Gebhard
 */
public class BrowserLaunchingFactory {

    public static IBrowserLaunching createSystemBrowserLaunching(AbstractLogger
            logger)
            throws UnsupportedOperatingSystemException {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            logger.info("Mac OS");
            String mrjVersion = System.getProperty("mrj.version");
            String majorMRJVersion = mrjVersion.substring(0, 3);
            try {
                double version = Double.valueOf(majorMRJVersion).doubleValue();
                logger.info("version=" + Double.toString(version));
                if (version == 2) {
                    return new MacOs2_0BrowserLaunching();
                }
                else if (version >= 2.1 && version < 3) {
                    // Assume that all 2.x versions of MRJ work the same.  MRJ 2.1 actually
                    // works via Runtime.exec() and 2.2 supports that but has an openURL() method
                    // as well that we currently ignore.
                    return new MacOs2_1BrowserLaunching();
                }
                else if (version == 3.0) {
                    return new MacOs3_0BrowserLaunching();
                }
                else if (version >= 3.1) {
                    // Assume that all 3.1 and later versions of MRJ work the same.
                    return new MacOs3_1BrowserLaunching();
                }
                else {
                    throw new UnsupportedOperatingSystemException(
                            "Unsupported MRJ version: " + version);
                }
            }
            catch (NumberFormatException nfe) {
                throw new UnsupportedOperatingSystemException(
                        "Invalid MRJ version: " + mrjVersion);
            }
        }
        else if (osName.startsWith("Windows")) {
            logger.info("Windows OS");
            if (osName.indexOf("9") != -1) {
                return new Windows9xBrowserLaunching(logger);
            }
            else if (osName.indexOf("2000") != -1) {
                return new Windows2000BrowserLaunching(logger);
            }
            else {
                return new WindowsNtBrowserLaunching(logger);
            }
        }
        else if (osName.startsWith("SunOS")) {
            logger.info("SunOS");
            return new SunOSBrowserLaunching(logger);
        }
        else {
            logger.info("Unix-type OS");
            return new UnixNetscapeBrowserLaunching(logger);
        }
    }
}
