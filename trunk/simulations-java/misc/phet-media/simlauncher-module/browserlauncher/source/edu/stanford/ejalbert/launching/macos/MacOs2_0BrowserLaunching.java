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
// $Id: MacOs2_0BrowserLaunching.java,v 1.2 2005/10/07 20:01:08 jchapman0 Exp $
package edu.stanford.ejalbert.launching.macos;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import edu.stanford.ejalbert.launching.IBrowserLaunching;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Markus Gebhard
 */
public class MacOs2_0BrowserLaunching
        extends MacOsBrowserLaunching {

    /** The name for the AppleEvent type corresponding to a GetURL event. */
    private static final String GURL_EVENT = "GURL";

    private Class aeDescClass;
    private Constructor aeTargetConstructor;
    private Constructor appleEventConstructor;
    private Constructor aeDescConstructor;
    private Method makeOSType;
    private Method putParameter;
    private Method sendNoReply;
    private Integer keyDirectObject;
    private Integer kAutoGenerateReturnID;
    private Integer kAnyTransactionID;

    public void initialize()
            throws BrowserLaunchingInitializingException {
        try {
            Class aeTargetClass = Class.forName("com.apple.MacOS.AETarget");
            Class osUtilsClass = Class.forName("com.apple.MacOS.OSUtils");
            Class appleEventClass = Class.forName("com.apple.MacOS.AppleEvent");
            Class aeClass = Class.forName("com.apple.MacOS.ae");
            aeDescClass = Class.forName("com.apple.MacOS.AEDesc");

            aeTargetConstructor = aeTargetClass.getDeclaredConstructor(new
                    Class[] {int.class});
            appleEventConstructor =
                    appleEventClass.getDeclaredConstructor(
                            new Class[] {int.class, int.class, aeTargetClass, int.class, int.class});
            aeDescConstructor = aeDescClass.getDeclaredConstructor(new Class[] {
                    String.class});

            makeOSType = osUtilsClass.getDeclaredMethod("makeOSType",
                    new Class[] {String.class});
            putParameter = appleEventClass.getDeclaredMethod("putParameter",
                    new Class[] {int.class, aeDescClass});
            sendNoReply = appleEventClass.getDeclaredMethod("sendNoReply",
                    new Class[] {
            });

            Field keyDirectObjectField = aeClass.getDeclaredField(
                    "keyDirectObject");
            keyDirectObject = (Integer) keyDirectObjectField.get(null);
            Field autoGenerateReturnIDField = appleEventClass.getDeclaredField(
                    "kAutoGenerateReturnID");
            kAutoGenerateReturnID = (Integer) autoGenerateReturnIDField.get(null);
            Field anyTransactionIDField = appleEventClass.getDeclaredField(
                    "kAnyTransactionID");
            kAnyTransactionID = (Integer) anyTransactionIDField.get(null);
        }
        catch (Exception cnfe) {
            throw new BrowserLaunchingInitializingException(cnfe);
        }
    }

    private Object getBrowser()
            throws BrowserLaunchingInitializingException {
        try {
            Integer finderCreatorCode = (Integer) makeOSType.invoke(null,
                    new Object[] {FINDER_CREATOR});
            Object aeTarget = aeTargetConstructor.newInstance(new Object[] {
                    finderCreatorCode});
            Integer gurlType = (Integer) makeOSType.invoke(null,
                    new Object[] {GURL_EVENT});
            Object appleEvent =
                    appleEventConstructor.newInstance(
                            new Object[] {gurlType, gurlType, aeTarget,
                            kAutoGenerateReturnID, kAnyTransactionID});
            // Don't set browser = appleEvent because then the next time we call
            // locateBrowser(), we'll get the same AppleEvent, to which we'll already have
            // added the relevant parameter. Instead, regenerate the AppleEvent every time.
            // There's probably a way to do this better; if any has any ideas, please let
            // me know.
            return appleEvent;
        }
        catch (Exception e) {
            throw new BrowserLaunchingInitializingException(e);
        }
    }

    public void openUrl(String urlString)
            throws UnsupportedOperatingSystemException,
            BrowserLaunchingExecutionException,
            BrowserLaunchingInitializingException {
        Object browser = getBrowser();
        Object aeDesc = null;
        try {
            aeDesc = aeDescConstructor.newInstance(new Object[] {urlString});
            putParameter.invoke(browser, new Object[] {keyDirectObject, aeDesc});
            sendNoReply.invoke(browser, new Object[] {
            });
        }
        catch (Exception e) {
            throw new BrowserLaunchingExecutionException(e);
        }
        finally {
            //TODO Oct 10, 2003 (Markus Gebhard): Unnecessary, because local variables - isn't it?
            aeDesc = null; // Encourage it to get disposed if it was created
            browser = null; // Ditto
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
