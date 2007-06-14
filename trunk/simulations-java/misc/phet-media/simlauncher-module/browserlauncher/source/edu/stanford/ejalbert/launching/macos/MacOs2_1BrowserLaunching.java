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
// $Id: MacOs2_1BrowserLaunching.java,v 1.2 2005/10/07 20:01:08 jchapman0 Exp $
package edu.stanford.ejalbert.launching.macos;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
public class MacOs2_1BrowserLaunching
        extends MacOsBrowserLaunching implements IBrowserLaunching {

    /**
     * The file type of the Finder on a Macintosh.  Hardcoding "Finder" would keep non-U.S. English
     * systems from working properly.
     */
    private static final String FINDER_TYPE = "FNDR";

    private Object kSystemFolderType;
    private Method findFolder;
    private Method getFileCreator;
    private Method getFileType;

    private String browser;

    public void initialize()
            throws BrowserLaunchingInitializingException {
        try {
            Class mrjFileUtilsClass = Class.forName(
                    "com.apple.mrj.MRJFileUtils");
            Class mrjOSTypeClass = Class.forName("com.apple.mrj.MRJOSType");
            Field systemFolderField = mrjFileUtilsClass.getDeclaredField(
                    "kSystemFolderType");
            kSystemFolderType = systemFolderField.get(null);
            findFolder = mrjFileUtilsClass.getDeclaredMethod("findFolder",
                    new Class[] {mrjOSTypeClass});
            getFileCreator = mrjFileUtilsClass.getDeclaredMethod(
                    "getFileCreator", new Class[] {File.class});
            getFileType = mrjFileUtilsClass.getDeclaredMethod("getFileType",
                    new Class[] {File.class});
        }
        catch (Exception e) {
            throw new BrowserLaunchingInitializingException(e);
        }
    }

    private String getBrowser()
            throws BrowserLaunchingInitializingException {
        if (browser != null) {
            return browser;
        }

        File systemFolder;
        try {
            systemFolder = (File) findFolder.invoke(null,
                    new Object[] {kSystemFolderType});
        }
        catch (Exception e) {
            throw new BrowserLaunchingInitializingException(e);
        }
        String[] systemFolderFiles = systemFolder.list();
        // Avoid a FilenameFilter because that can't be stopped mid-list
        for (int i = 0; i < systemFolderFiles.length; i++) {
            try {
                File file = new File(systemFolder, systemFolderFiles[i]);
                if (!file.isFile()) {
                    continue;
                }
                // We're looking for a file with a creator code of 'MACS' and
                // a type of 'FNDR'.  Only requiring the type results in non-Finder
                // applications being picked up on certain Mac OS 9 systems,
                // especially German ones, and sending a GURL event to those
                // applications results in a logout under Multiple Users.
                Object fileType = getFileType.invoke(null, new Object[] {file});
                if (FINDER_TYPE.equals(fileType.toString())) {
                    Object fileCreator = getFileCreator.invoke(null,
                            new Object[] {file});
                    if (FINDER_CREATOR.equals(fileCreator.toString())) {
                        browser = file.toString(); // Actually the Finder, but that's OK
                        return browser;
                    }
                }
            }
            catch (Exception e) {
                throw new BrowserLaunchingInitializingException(e);
            }
        }
        throw new BrowserLaunchingInitializingException("Unable to find finder");
    }

    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        String browser = getBrowser();
        try {
            Runtime.getRuntime().exec(new String[] {browser, urlString});
        }
        catch (IOException e) {
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
