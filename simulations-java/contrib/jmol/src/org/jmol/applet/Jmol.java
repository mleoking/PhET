/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-18 12:45:37 -0700 (Fri, 18 Mar 2011) $
 * $Revision: 15323 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.applet;

import org.jmol.api.*;
import org.jmol.appletwrapper.*;
import org.jmol.export.JmolFileDropper;
import org.jmol.i18n.GT;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;

import java.applet.Applet;
import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.UIManager;

import netscape.javascript.JSObject;

/*
 * these are *required*:
 * 
 * [param name="progressbar" value="true" /] [param name="progresscolor"
 * value="blue" /] [param name="boxmessage" value="your-favorite-message" /]
 * [param name="boxbgcolor" value="#112233" /] [param name="boxfgcolor"
 * value="#778899" /]
 * 
 * these are *optional*:
 * 
 * [param name="syncId" value="nnnnn" /]
 * 
 * determines the subset of applets *across pages* that are to be synchronized
 * (usually just a random number assigned in Jmol.js)
 * if this is fiddled with, it still should be a random number, not
 * one that is assigned statically for a given web page.
 * 
 * [param name="menuFile" value="myMenu.mnu" /]
 * 
 * optional file to load containing menu data in the format of Jmol.mnu (Jmol 11.3.15)
 * 
 * [param name="loadInline" value=" | do | it | this | way " /]
 * 
 * [param name="script" value="your-script" /]
 *  // this one flips the orientation and uses RasMol/Chime colors [param
 * name="emulate" value="chime" /]
 *  // this is *required* if you want the applet to be able to // call your
 * callbacks
 * 
 * mayscript="true" is required as an applet/object for any callback, eval, or text/textarea setting)
 *
 * To disable ALL access to JavaScript (as, for example, in a Wiki) 
 * remove the MAYSCRIPT tag or set MAYSCRIPT="false"
 * 
 * To set a maximum size for the applet if resizable:
 *
 * [param name="maximumSize" value="nnnn" /]
 * 
 * 
 * You can specify that the signed or unsign applet or application should
 * use an independent command thread (EXCEPT for scripts containing the "javascript" command)  
 * 
 * [param name="useCommandThread" value="true"]
 * 
 * You can specify a language (French in this case) using  
 * 
 * [param name="language" value="fr"]
 * 
 * You can check that it is set correctly using 
 * 
 * [param name="debug" value="true"]
 *  
 *  or
 *  
 * [param name="logLevel" value="5"]
 * 
 * and then checking the console for a message about MAYSCRIPT
 * 
 * In addition, you can turn off JUST EVAL, by setting on the web page
 * 
 * _jmol.noEval = true
 * 
 * This allows callbacks but does not allow the script constructs: 
 * 
 *  script javascript:...
 *  javascript ...
 *  x = javascript(...) 
 *  
 * However, this can be overridden by adding an evalCallback function 
 * This MUST be defined along with applet loading using a <param> tag
 * Easiest way to do this is to define
 * 
 * jmolSetCallback("evalCallback", "whateverFunction")
 * 
 * prior to the jmolApplet() command
 * 
 * This is because the signed applet was having trouble finding _jmol in 
 * Protein Explorer
 * 
 * see JmolConstants for callback types.
 * 
 * The use of jmolButtons is fully deprecated and NOT recommended.
 * 
 * 
 * 
 * new for Jmol 11.9.11:
 * 
 * [param name="multiTouchSparshUI" value="true"]
 * [param name="multiTouchSparshUI-simulated" value="true"]
 * 
 * (signed applet only) loads the SparshUI client adapter
 *  requires JmolMultiTouchDriver.exe (HP TouchSmart computer only)
 *  Uses 127.0.0.1 port 5946 (client) and 5947 (device). 
 *  (see http://code.google.com/p/sparsh-ui/)
 * 
 */

public class Jmol implements WrappedApplet {

  boolean mayScript;
  boolean haveDocumentAccess;
  boolean loading;

  String[] callbacks = new String[JmolConstants.CALLBACK_COUNT];

  String language;
  String htmlName;
  String fullName;
  String syncId;
  String languagePath;

  AppletWrapper appletWrapper;
  protected JmolViewer viewer;

  private final static boolean REQUIRE_PROGRESSBAR = true;
  private boolean hasProgressBar;

  protected boolean doTranslate = true;

  private String statusForm;
  private String statusText;
  private String statusTextarea;

  private int paintCounter;

  /*
   * miguel 2004 11 29
   * 
   * WARNING! DANGER!
   * 
   * I have discovered that if you call JSObject.getWindow().toString() on
   * Safari v125.1 / Java 1.4.2_03 then it breaks or kills Safari I filed Apple
   * bug report #3897879
   * 
   * Therefore, do *not* call System.out.println("" + jsoWindow);
   */

  /*
   * see below public String getAppletInfo() { return appletInfo; }
   * 
   * static String appletInfo = GT._("Jmol Applet. Part of the OpenScience
   * project. " + "See http://www.jmol.org for more information");
   */
  public void setAppletWrapper(AppletWrapper appletWrapper) {
    this.appletWrapper = appletWrapper;
  }

  //protected void finalize() throws Throwable {
  //  System.out.println("Jmol finalize " + this);
  //  super.finalize();
  //}

  public void init() {
    htmlName = getParameter("name");
    syncId = getParameter("syncId");
    fullName = htmlName + "__" + syncId + "__";
    System.out.println("Jmol applet " + fullName + " initializing");
    setLogging();

    String ms = getParameter("mayscript");
    mayScript = (ms != null) && (!ms.equalsIgnoreCase("false"));
    JmolAppletRegistry.checkIn(fullName, appletWrapper);
    initWindows();
    initApplication();
  }

  public void destroy() {
    gRight = null;
    JmolAppletRegistry.checkOut(fullName);
    viewer.setModeMouse(JmolConstants.MOUSE_NONE);
    viewer = null;
    if (dropper != null) {
      dropper.dispose();
      dropper = null;
    }
    System.out.println("Jmol applet " + fullName + " destroyed");
  }

  String getParameter(String paramName) {
    return appletWrapper.getParameter(paramName);
  }

  public Graphics setStereoGraphics(boolean isStereo) {
    isStereoSlave = isStereo;
    return (isStereo ? appletWrapper.getGraphics() : null);
  }

  boolean isSigned;

  public void initWindows() {

    String options = "-applet";
    isSigned = getBooleanValue("signed", false) || appletWrapper.isSigned();
    if (isSigned)
      options += "-signed";
    if (getBooleanValue("useCommandThread", isSigned))
      options += "-threaded";
    if (isSigned && getBooleanValue("multiTouchSparshUI-simulated", false))
      options += "-multitouch-sparshui-simulated";
    else if (isSigned && getBooleanValue("multiTouchSparshUI", false)) // true for testing JmolAppletSignedMT.jar
      options += "-multitouch-sparshui";
    String s = getValue("MaximumSize", null);
    if (s != null)
      options += "-maximumSize " + s;
    // note, -appletProxy must be the LAST item added
    s = getValue("JmolAppletProxy", null);
    if (s != null)
      options += "-appletProxy " + s;
    viewer = JmolViewer.allocateViewer(appletWrapper, null, fullName,
        appletWrapper.getDocumentBase(), appletWrapper.getCodeBase(), options,
        new MyStatusListener());
    String menuFile = getParameter("menuFile");
    if (menuFile != null)
      viewer.getProperty("DATA_API", "setMenu", viewer
          .getFileAsString(menuFile));
    try {
      UIManager.setLookAndFeel(UIManager
          .getCrossPlatformLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }
    if (Logger.debugging) {
      Logger.debug("checking for jsoWindow mayScript=" + mayScript);
    }
    if (mayScript) {
      mayScript = haveDocumentAccess = false;
      JSObject jsoWindow = null;
      JSObject jsoDocument = null;
      try {
        jsoWindow = JSObject.getWindow(appletWrapper);
        if (Logger.debugging) {
          Logger.debug("jsoWindow=" + jsoWindow);
        }
        if (jsoWindow == null) {
          Logger
              .error("jsoWindow returned null ... no JavaScript callbacks :-(");
        } else {
          mayScript = true;
        }
        jsoDocument = (JSObject) jsoWindow.getMember("document");
        if (jsoDocument == null) {
          Logger
              .error("jsoDocument returned null ... no DOM manipulations :-(");
        } else {
          haveDocumentAccess = true;
        }
      } catch (Exception e) {
        Logger
            .error("Microsoft MSIE bug -- http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5012558 "
                + e);
      }
      if (Logger.debugging) {
        Logger.debug("jsoWindow:" + jsoWindow + " jsoDocument:" + jsoDocument
            + " mayScript:" + mayScript + " haveDocumentAccess:"
            + haveDocumentAccess);
      }
    }
  }

  private void setLogging() {
    int iLevel = (getValue("logLevel", (getBooleanValue("debug", false) ? "5"
        : "4"))).charAt(0) - '0';
    if (iLevel != 4)
      System.out.println("setting logLevel=" + iLevel
          + " -- To change, use script \"set logLevel [0-5]\"");
    Logger.setLogLevel(iLevel);
  }

  private boolean getBooleanValue(String propertyName, boolean defaultValue) {
    String value = getValue(propertyName, defaultValue ? "true" : "");
    return (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value
        .equalsIgnoreCase("yes"));
  }

  private String getValue(String propertyName, String defaultValue) {
    String stringValue = getParameter(propertyName);
    if (stringValue != null)
      return stringValue;
    return defaultValue;
  }

  private String getValueLowerCase(String paramName, String defaultValue) {
    String value = getValue(paramName, defaultValue);
    if (value != null) {
      value = value.trim().toLowerCase();
      if (value.length() == 0)
        value = null;
    }
    return value;
  }

  JmolFileDropper dropper;
  
  public void initApplication() {
    viewer.pushHoldRepaint();
    {
      // REQUIRE that the progressbar be shown
      hasProgressBar = getBooleanValue("progressbar", false);
      String emulate = getValueLowerCase("emulate", "jmol");
      setStringProperty("defaults", emulate.equals("chime") ? "RasMol" : "Jmol");
      setStringProperty("backgroundColor", getValue("bgcolor", getValue(
          "boxbgcolor", "black")));

      viewer.setBooleanProperty("frank", true);
      loading = true;
      for (int i = 0; i < JmolConstants.CALLBACK_COUNT; i++) {
        String name = JmolConstants.getCallbackName(i);
        setValue(name, null);
      }
      loading = false;

      language = getParameter("language");
      if (language != null) {
        System.out.print("requested language=" + language + "; ");
        new GT(language);
      }
      doTranslate = (!"none".equals(language) && getBooleanValue("doTranslate",
          true));
      language = GT.getLanguage();
      System.out.println("language=" + language);

      boolean haveCallback = false;
      // these are set by viewer.setStringProperty() from setValue
      for (int i = 0; i < JmolConstants.CALLBACK_COUNT && !haveCallback; i++)
        haveCallback = (callbacks[i] != null);
      if (haveCallback || statusForm != null || statusText != null) {
        if (!mayScript)
          Logger
              .warn("MAYSCRIPT missing -- all applet JavaScript calls disabled");
      }
      if (callbacks[JmolConstants.CALLBACK_SCRIPT] == null
          && callbacks[JmolConstants.CALLBACK_ERROR] == null)
        if (callbacks[JmolConstants.CALLBACK_MESSAGE] != null
            || statusForm != null || statusText != null) {
          if (doTranslate && (getValue("doTranslate", null) == null)) {
            doTranslate = false;
            Logger
                .warn("Note -- Presence of message callback disables disable translation;"
                    + " to enable message translation use jmolSetTranslation(true) prior to jmolApplet()");
          }
          if (doTranslate)
            Logger
                .warn("Note -- Automatic language translation may affect parsing of message callbacks"
                    + " messages; use scriptCallback or errorCallback to process errors");
        }

      if (!doTranslate) {
        GT.setDoTranslate(false);
        Logger.warn("Note -- language translation disabled");
      }

      statusForm = getValue("StatusForm", null);
      statusText = getValue("StatusText", null); // text
      statusTextarea = getValue("StatusTextarea", null); // textarea

      if (statusForm != null && statusText != null) {
        Logger.info("applet text status will be reported to document."
            + statusForm + "." + statusText);
      }
      if (statusForm != null && statusTextarea != null) {
        Logger.info("applet textarea status will be reported to document."
            + statusForm + "." + statusTextarea);
      }

      // should the popupMenu be loaded ?
      if (!getBooleanValue("popupMenu", true))
        viewer.getProperty("DATA_API", "disablePopupMenu", null);
      loadNodeId(getValue("loadNodeId", null));

      String loadParam;
      String scriptParam = getValue("script", "");
      String test = null;

      // test =
      // "\n  Marvin  10270415522D\n\n  9  9  0  0  0  0            999 V2000\n   -2.2457    0.8188    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -2.2457   -0.0063    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.5313   -0.4188    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.8168   -0.0063    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.8168    0.8188    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.1023    1.2313    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.5313    1.2313    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.5313    2.8813    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.5313    2.0563    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n  7  1  1  0  0  0  0\n  7  5  2  0  0  0  0\n  1  2  2  0  0  0  0\n  2  3  1  0  0  0  0\n  3  4  2  0  0  0  0\n  4  5  1  0  0  0  0\n  5  6  1  0  0  0  0\n  8  9  1  0  0  0  0\n  7  9  1  0  0  0  0\nM  APO  1   9   1\nM  STY  1   1 SUP\nM  SAL   1  2   8   9\nM  SBL   1  1   9\nM  SMT   1 Et\nM  END\n";
      // test =
      // "#JVXL VERSION 1.4\n#created by Jmol Version 11.6.15  2008-11-24 13:39 on Wed Dec 09 20:51:19 CST 2009\nMO range (-4.286028, -4.170638, -6.45537) to (5.6358566, 10.584962, 3.6056142)\ncalculation type: ?\n-32 -4.286028 -4.170638 -6.45537 ANGSTROMS\n80 0.12402356 0.0 0.0\n80 0.0 0.184445 0.0\n80 0.0 0.0 0.1257623\n6 6.0 0.0 0.0 0.0\n6 6.0 1.5335295 0.0 0.0\n8 8.0 2.3333447 1.1734227 0.0\n6 6.0 2.3022442 2.08224 -1.0298198\n6 6.0 1.9440373 3.352165 -0.783915\n6 6.0 2.166842 4.391685 -1.8063345\n8 8.0 1.4540279 5.5320063 -1.5695299\n6 6.0 1.4419276 6.5410266 -2.576349\n6 6.0 0.4292079 7.5804462 -2.13194\n1 1.0 0.699213 8.036855 -1.1707217\n1 1.0 0.36370665 8.384962 -2.8751543\n1 1.0 -0.57431144 7.149238 -2.0169382\n1 1.0 2.4541469 6.9775352 -2.6722505\n1 1.0 1.1674224 6.103718 -3.555268\n8 8.0 2.9358563 4.365084 -2.7551525\n1 1.0 1.4746284 3.6545703 0.16250335\n1 1.0 2.660351 1.708333 -1.9975383\n6 6.0 2.025939 -0.95221835 -1.101621\n1 1.0 1.6514318 -1.9706379 -0.937718\n1 1.0 1.6784323 -0.62881225 -2.0926402\n1 1.0 3.1216602 -1.0091194 -1.1394216\n1 1.0 1.869836 -0.3798073 0.9982192\n6 6.0 -0.86121655 0.62561214 -1.0898211\n6 6.0 -0.8380163 2.0938404 -1.2457241\n6 6.0 -0.81681573 2.6353507 -2.5209484\n7 7.0 -0.79011524 3.084059 -3.6053698\n6 6.0 -1.1576223 2.898656 -0.16150327\n7 7.0 -1.4360279 3.5764692 0.75561434\n1 1.0 -0.59091145 0.1594031 -2.0609396\n1 1.0 -1.9027367 0.2891056 -0.885317\n1 1.0 -0.29010567 -1.0688207 0.08750176\n1 1.0 -0.314506 0.47200906 0.95541835\n-1 35 90 35 90 Jmol voxel format version 1.4\n# \n0.05 361 -1362 1362 -1.0 1.0 -1.0 1.0 rendering:isosurface ID \"homop\" fill noMesh noDots notFrontOnly frontlit\n 117879 5 75 6 74 5 6154 7 72 9 71 9 71 9 72 7 75 3 5915 7 72 9 70 11 69 11 69 11 70 10 71 7 5834 4 74 9 70 11 68 13 67 13 67 13 68 12 69 9 73 5 5754 7 71 11 68 13 67 13 66 14 67 13 67 13 68 11 71 7 5752 8 71 11 68 13 66 14 66 15 65 15 66 13 68 11 71 7 334 2 77 4 77 2 5256 8 70 12 68 13 66 14 66 14 66 14 67 13 68 11 71 7 173 3 75 6 74 7 72 8 73 6 75 4 5176 8 71 11 68 12 67 14 66 14 66 14 67 12 69 10 73 4 173 6 73 8 71 10 70 9 72 8 73 6 5176 6 72 10 69 11 69 12 68 12 68 11 70 9 73 5 175 2 75 7 72 9 71 10 70 10 70 9 72 7 5256 6 72 9 71 9 71 9 72 7 75 2 256 3 75 7 72 9 71 9 71 9 71 9 72 7 5993 7 72 9 71 9 71 9 71 8 74 5 5801 4 74 7 73 7 29 5 39 7 28 7 40 4 28 8 73 7 73 6 5800 5 74 7 72 9 71 9 71 9 28 4 40 7 29 5 75 5 77 1 4709 4 77 3 1008 7 72 9 70 10 70 10 71 9 72 7 75 3 4743 3 76 5 75 5 77 3 928 7 72 9 70 10 70 10 71 9 72 7 75 3 4742 4 76 5 75 5 76 4 928 6 73 8 71 10 70 10 71 9 72 7 4820 3 77 5 75 5 76 4 929 4 75 7 72 8 72 8 73 7 74 5 4822 1 78 4 76 4 77 3 1010 5 74 6 74 6 75 5 4982 2 78 2 279630\nW4?A7Kv(0Dwm)s(RKaX!S@<V0QmxtbS9C3Omxtb9B|#HVR:PQ|RBGb<HJB!-8:#vKSHeHZQ`&A0E@t&u(DQOaJ[6>OJRKgiv'trPM+rNK_@f7ob*7GZT4@>dX!q:`TWV@aG|ga{$l{'-v9-WUv+p2Wmuqb@[S7k>]u[Fw,*'Lq##%$l|R-5pEO=W?69JmAOWT<W'_XQcN[n*cJOw'm31X,GNHDmuXy^Izv+kA2<axMh_lP:ZqF1*-<!,lA;p.)w*)E8BQXQleUe,Z60]N<rx[zeCzs(b;23c`]buFGwJ6/3CgjDQ@a`Tv&vjk4dj4:,+eS'c'e5j-mMYzUS>YS*ES9Xc^Hj9!cv|wgBP74=JC'+G^UNYIRw@kON^c:I2*w,@re<=YqVOVpfMXiEKqP2Tntyt{_e125nBUkx4btL'&^'tFOE{#YXCwPuT[HX|;8XXq*P]T/O4;9TBwZPXsOS.STEk,e*rMsrUZOT^]s.d7GeQvLo8KU!4ql(*(dRZx]Gjazd_gg@G8kN@>FWtce=9e2^F<;CSlmC.(/ErmVLLSazymkr&w+OcjfT/H6A]^LK+v#w=Q45JQM,UG`631[smx/9'-']3h'g9{EKKwDX7t'y#<P^baZLp4S^+DXejjbR6Qx$>R^ecZF##uk(=JOK=%x|m<)&/K6![mrm[4>TINK2tJrk|pN0G()UDG)N#y.7cro?(3N?QDKA:5m!)>{Wl{yxfSHnw/BcymJ!Zd-X_ma0_Za6)S46n+]z=eAp0DAbuylL+gc`E99D].iTfkcLo@LQvz)3$xJgwBo>IiI<C:WTZ;.`/[;[%o%/1.1$$)9S4]Yr{pXu2;@^=FxUl^Jh0rFC6VA]Ca;A3B/u!dz%.rz7L[`=LdD2))1Dfa1ZFI8P>-e(kr-tJc;^rH%|#g$$#8O;In3.;ibxkw)h.@]LQND/GICGPinIc!MXjn.bD(Q7Bb[=q]W1%`*45Zz!A1))0A`lWYl@%w8p$9XR,vxM.MsTf(8'x#=YN_<];G[vysz96LWZVJ5VT.wqH^r:dd2x7N9sRHX-9[OiXX+tjb0C?EfL<44;Ll{v{jisss1NEPSN@(0HCpc4=_JFE[)u;<&K*9Qv9oKovNQxE|,dD[|bRJIReI,T8;4$'l#T#rgBYT'x|diBN^FGWn`HQaA9p7IrX1j:iC|*6N1>DStklvgLFUz)F,^jlfU7NFw6ixct+;6qXN0-k7RG[K733&z%HXj7W;uXGi?X@ZMpfL++N/&/6`j(S.tiBS;9qARHJ/3`8GSJHQ@WKT8hpDF./&&*)<;_]snyS6($+?aU+OAqyi>F4bJ&|m'R`{3YB5ahgED38-C1gBbhXS[o|]MHOap]X^q\n|~1361 \n#-------end of jvxl file data-------\n# cutoff = 0.05; pointsPerAngstrom = 8.062984; nSurfaceInts = 361; nBytesData = 3736; bicolor map\n# created using Jvxl.java\n# precision: false nColorData 1362\n# isosurface homop cutoff +0.05 mo homo fill;\n# isosurface ID \"homop\" fill noMesh noDots notFrontOnly frontlit\n# bytes read: 0; approximate voxel-only input/output byte ratio: 2794:1\n";
      if ((loadParam = (test == null ? getValue("loadInline", null) : test)) != null) {
        loadInlineSeparated(loadParam, (scriptParam.length() > 0 ? scriptParam
            : null));
      } else {
        if ((loadParam = getValue("load", null)) != null)
          scriptParam = "load \"" + loadParam + "\";" + scriptParam;
        if (scriptParam.length() > 0)
          scriptProcessor(scriptParam, null, SCRIPT_NOWAIT);
      }
    }

    // file dropping for the signed applet

    if (isSigned) {
      try {
      dropper = new JmolFileDropper(viewer);
      } catch (Exception e) {
        //
      }
    }

    viewer.popHoldRepaint();
  }

  private void setValue(String name, String defaultValue) {
    setStringProperty(name, getValue(name, defaultValue));
  }

  private void setStringProperty(String name, String value) {
    if (value == null)
      return;
    Logger.info(name + " = \"" + value + "\"");
    viewer.setStringProperty(name, value);
  }

  void sendJsTextStatus(String message) {
    if (!haveDocumentAccess || statusForm == null || statusText == null)
      return;
    try {
      JSObject jsoWindow = JSObject.getWindow(appletWrapper);
      JSObject jsoDocument = (JSObject) jsoWindow.getMember("document");
      JSObject jsoForm = (JSObject) jsoDocument.getMember(statusForm);
      if (statusText != null) {
        JSObject jsoText = (JSObject) jsoForm.getMember(statusText);
        jsoText.setMember("value", message);
      }
    } catch (Exception e) {
      Logger.error("error indicating status at document." + statusForm + "."
          + statusText + ":" + e.toString());
    }
  }

  void sendJsTextareaStatus(String message) {
    if (!haveDocumentAccess || statusForm == null || statusTextarea == null)
      return;
    try {
      JSObject jsoWindow = JSObject.getWindow(appletWrapper);
      JSObject jsoDocument = (JSObject) jsoWindow.getMember("document");
      JSObject jsoForm = (JSObject) jsoDocument.getMember(statusForm);
      if (statusTextarea != null) {
        JSObject jsoTextarea = (JSObject) jsoForm.getMember(statusTextarea);
        if (message == null) {
          jsoTextarea.setMember("value", "");
        } else {
          String info = (String) jsoTextarea.getMember("value");
          jsoTextarea.setMember("value", info + "\n" + message);
        }
      }
    } catch (Exception e) {
      Logger.error("error indicating status at document." + statusForm + "."
          + statusTextarea + ":" + e.toString());
    }
  }

  public boolean showPaintTime = false;
  public void paint(Graphics g) {
    //paint is invoked for system-based updates (obscurring, for example)
    //Opera has a bug in relation to displaying the Java Console. 
    update(g, "paint ");
  }

  private boolean isUpdating;

  public void update(Graphics g) {
    //update is called in response to repaintManager's repaint() request.
    update(g, "update");
  }

  protected Graphics gRight;
  protected boolean isStereoSlave;
  
  /**
   * 
   * @param g
   * @param source  for debugging only
   */
  private void update(Graphics g, String source) {
    if (viewer == null) // it seems that this can happen at startup sometimes
      return;
    if (isUpdating)
      return;

    //Opera has been known to allow entry to update() by one thread
    //while another thread is doing a paint() or update(). 

    //for now, leaving out the "needRendering" idea

    isUpdating = true;
    if (showPaintTime)
      startPaintClock();
    Dimension size = new Dimension();
    appletWrapper.getSize(size);
    viewer.setScreenDimension(size);
    //Rectangle rectClip = jvm12orGreater ? jvm12.getClipBounds(g) : g.getClipRect();
    ++paintCounter;
    if (REQUIRE_PROGRESSBAR && !isSigned && !hasProgressBar
        && paintCounter < 30 && (paintCounter & 1) == 0) {
      printProgressbarMessage(g);
      viewer.notifyViewerRepaintDone();
    } else {
      if (!isStereoSlave)
        viewer.renderScreenImage(g, gRight, size, null);//rectClip);
    }

    if (showPaintTime) {
      stopPaintClock();
      showTimes(10, 10, g);
    }
    isUpdating = false;
  }

  private final static String[] progressbarMsgs = {
      "Jmol developer alert!",
      "",
      "Please use jmol.js. You are missing the ",
      "required 'progressbar' parameter.",
      "  <param name='progressbar' value='true' />", };

  private void printProgressbarMessage(Graphics g) {
    g.setColor(Color.yellow);
    g.fillRect(0, 0, 10000, 10000);
    g.setColor(Color.black);
    for (int i = 0, y = 13; i < progressbarMsgs.length; ++i, y += 13) {
      g.drawString(progressbarMsgs[i], 10, y);
    }
  }

  public boolean handleEvent(Event e) {
    if (viewer == null)
      return false;
    return viewer.handleOldJvm10Event(e);
  }

  // code to record last and average times
  // last and average of all the previous times are shown in the status window

  private int timeLast, timeCount, timeTotal;
  private long timeBegin;

  private int lastMotionEventNumber;

  private void startPaintClock() {
    timeBegin = System.currentTimeMillis();
    int motionEventNumber = viewer.getMotionEventNumber();
    if (lastMotionEventNumber != motionEventNumber) {
      lastMotionEventNumber = motionEventNumber;
      timeCount = timeTotal = 0;
      timeLast = -1;
    }
  }

  private void stopPaintClock() {
    int time = (int) (System.currentTimeMillis() - timeBegin);
    if (timeLast != -1) {
      timeTotal += timeLast;
      ++timeCount;
    }
    timeLast = time;
  }

  private String fmt(int num) {
    if (num < 0)
      return "---";
    if (num < 10)
      return "  " + num;
    if (num < 100)
      return " " + num;
    return "" + num;
  }

  private void showTimes(int x, int y, Graphics g) {
    int timeAverage = (timeCount == 0) ? -1 : (timeTotal + timeCount / 2)
        / timeCount; // round, don't truncate
    g.setColor(Color.green);
    g.drawString(fmt(timeLast) + "ms : " + fmt(timeAverage) + "ms", x, y);
  }

  private final static int SCRIPT_CHECK = 0;
  private final static int SCRIPT_WAIT = 1;
  private final static int SCRIPT_NOWAIT = 2;

  private String scriptProcessor(String script, String statusParams,
                                 int processType) {
    /*
     * Idea here is to provide a single point of entry
     * Synchronization may not work, because it is possible for the NOWAIT variety of
     * scripts to return prior to full execution 
     * 
     */
    //System.out.println("Jmol.script: " + script);
    if (script == null || script.length() == 0)
      return "";
    switch (processType) {
    case SCRIPT_CHECK:
      Object err = viewer.scriptCheck(script);
      return (err instanceof String ? (String) err : "");
    case SCRIPT_WAIT:
      if (statusParams != null)
        return viewer.scriptWaitStatus(script, statusParams).toString();
      return viewer.scriptWait(script);
    case SCRIPT_NOWAIT:
    default:
      return viewer.script(script);
    }
  }

  public void script(String script) {
    if (script == null || script.length() == 0)
      return;
    scriptProcessor(script, null, SCRIPT_NOWAIT);
  }

  public String scriptCheck(String script) {
    if (script == null || script.length() == 0)
      return "";
    return scriptProcessor(script, null, SCRIPT_CHECK);
  }

  public String scriptNoWait(String script) {
    if (script == null || script.length() == 0)
      return "";
    return scriptProcessor(script, null, SCRIPT_NOWAIT);
  }

  public String scriptWait(String script) {
    if (script == null || script.length() == 0)
      return "";
    outputBuffer = null;
    return scriptProcessor(script, null, SCRIPT_WAIT);
  }

  StringBuffer outputBuffer;
  
  public String scriptWait(String script, String statusParams) {
    if (script == null || script.length() == 0)
      return "";
    outputBuffer = null;
    return scriptProcessor(script, statusParams, SCRIPT_WAIT);
  }

  public String scriptWaitOutput(String script) {
    if (script == null || script.length() == 0)
      return "";
    outputBuffer = new StringBuffer();
    viewer.scriptWaitStatus(script, "");
    String str = (outputBuffer == null ? "" : outputBuffer.toString());
    outputBuffer = null;
    return str;
  }

  synchronized public void syncScript(String script) {
    viewer.syncScript(script, "~");
  }

  public String getAppletInfo() {
    return GT
        ._(
            "Jmol Applet version {0} {1}.\n\nAn OpenScience project.\n\nSee http://www.jmol.org for more information",
            new Object[] { JmolConstants.version, JmolConstants.date })
        + "\nhtmlName = "
        + Escape.escape(htmlName)
        + "\nsyncId = "
        + Escape.escape(syncId)
        + "\ndocumentBase = "
        + Escape.escape("" + appletWrapper.getDocumentBase())
        + "\ncodeBase = "
        + Escape.escape("" + appletWrapper.getCodeBase());
  }

  public Object getProperty(String infoType) {
    return viewer.getProperty(null, infoType, "");
  }

  public Object getProperty(String infoType, String paramInfo) {
    return viewer.getProperty(null, infoType, paramInfo);
  }

  public String getPropertyAsString(String infoType) {
    return viewer.getProperty("readable", infoType, "").toString();
  }

  public String getPropertyAsString(String infoType, String paramInfo) {
    return viewer.getProperty("readable", infoType, paramInfo).toString();
  }

  public String getPropertyAsJSON(String infoType) {
    return viewer.getProperty("JSON", infoType, "").toString();
  }

  public String getPropertyAsJSON(String infoType, String paramInfo) {
    return viewer.getProperty("JSON", infoType, paramInfo).toString();
  }

  public String loadInlineString(String strModel, String script, boolean isAppend) {
    String errMsg = viewer.loadInline(strModel, isAppend);
    if (errMsg == null)
      script(script);
    return errMsg;
  }

  public String loadInlineArray(String[] strModels, String script,
                              boolean isAppend) {
    if (strModels == null || strModels.length == 0)
      return null;
    String errMsg = viewer.loadInline(strModels, isAppend);
    if (errMsg == null)
      script(script);
    return errMsg;
  }

  /**
   * @deprecated
   * @param strModel
   * @return         error or null
   */
  @Deprecated
  public String loadInline(String strModel) {
    return loadInlineString(strModel, "", false);
  }

  /**
   * @deprecated
   * @param strModel
   * @param script
   * @return         error or null
   */
  @Deprecated
  public String loadInline(String strModel, String script) {
    return loadInlineString(strModel, script, false);
  }

  /**
   * @deprecated
   * @param strModels
   * @return         error or null
   */
  @Deprecated
  public String loadInline(String[] strModels) {
    return loadInlineArray(strModels, "", false);
  }

  /**
   * @deprecated
   * @param strModels
   * @param script
   * @return       error or null
   */
  @Deprecated
  public String loadInline(String[] strModels, String script) {
    return loadInlineArray(strModels, script, false);
  }

  private String loadInlineSeparated(String strModel, String script) {
    // from an applet PARAM only -- because it converts | into \n
    if (strModel == null)
      return null;
    String errMsg = viewer.loadInline(strModel);
    if (errMsg == null)
      script(script);
    return errMsg;
  }

  public String loadDOMNode(JSObject DOMNode) {
    // This should provide a route to pass in a browser DOM node
    // directly as a JSObject. Unfortunately does not seem to work with
    // current browsers
    return viewer.openDOM(DOMNode);
  }

  public String loadNodeId(String nodeId) {
    if (!haveDocumentAccess)
      return "ERROR: NO DOCUMENT ACCESS";
    if (nodeId == null)
      return null;
    // Retrieve Node ...
    // First try to find by ID
    Object[] idArgs = { nodeId };
    JSObject tryNode = null;
    try {
      JSObject jsoWindow = JSObject.getWindow(appletWrapper);
      JSObject jsoDocument = (JSObject) jsoWindow.getMember("document");
      tryNode = (JSObject) jsoDocument.call("getElementById", idArgs);

      // But that relies on a well-formed CML DTD specifying ID search.
      // Otherwise, search all cml:cml nodes.
      if (tryNode == null) {
        Object[] searchArgs = { "http://www.xml-cml.org/schema/cml2/core",
            "cml" };
        JSObject tryNodeList = (JSObject) jsoDocument.call(
            "getElementsByTagNameNS", searchArgs);
        if (tryNodeList != null) {
          for (int i = 0; i < ((Number) tryNodeList.getMember("length"))
              .intValue(); i++) {
            tryNode = (JSObject) tryNodeList.getSlot(i);
            Object[] idArg = { "id" };
            String idValue = (String) tryNode.call("getAttribute", idArg);
            if (nodeId.equals(idValue))
              break;
            tryNode = null;
          }
        }
      }
    } catch (Exception e) {
      return "" + e;
    }
    return (tryNode == null ? "ERROR: No CML node" : loadDOMNode(tryNode));
  }

  class MyStatusListener implements JmolStatusListener {

    public boolean notifyEnabled(int type) {
      switch (type) {
      case JmolConstants.CALLBACK_ANIMFRAME:
      case JmolConstants.CALLBACK_ECHO:
      case JmolConstants.CALLBACK_ERROR:
      case JmolConstants.CALLBACK_EVAL:
      case JmolConstants.CALLBACK_LOADSTRUCT:
      case JmolConstants.CALLBACK_MEASURE:
      case JmolConstants.CALLBACK_MESSAGE:
      case JmolConstants.CALLBACK_PICK:
      case JmolConstants.CALLBACK_SYNC:
      case JmolConstants.CALLBACK_SCRIPT:
        return true;
      case JmolConstants.CALLBACK_CLICK:
      case JmolConstants.CALLBACK_HOVER:
      case JmolConstants.CALLBACK_MINIMIZATION:
      case JmolConstants.CALLBACK_RESIZE:
      }
      return (callbacks[type] != null);
    }

    private boolean haveNotifiedError;

    public void notifyCallback(int type, Object[] data) {

      String callback = (type < callbacks.length ? callbacks[type] : null);
      boolean doCallback = (callback != null && (data == null || data[0] == null));
      boolean toConsole = false;
      if (data != null)
        data[0] = htmlName;
      String strInfo = (data == null || data[1] == null ? null : data[1]
          .toString());

      //System.out.println("Jmol.java notifyCallback " + type + " " + callback
       //+ " " + strInfo);
      switch (type) {
      case JmolConstants.CALLBACK_MINIMIZATION:
      case JmolConstants.CALLBACK_RESIZE:
      case JmolConstants.CALLBACK_EVAL:
      case JmolConstants.CALLBACK_HOVER:
      case JmolConstants.CALLBACK_ERROR:
        // just send it
        break;
      case JmolConstants.CALLBACK_CLICK:
        // x, y, action, int[] {action}
        // the fourth parameter allows an application to change the action
        if ("alert".equals(callback))
          strInfo = "x=" + data[1] + " y=" + data[2] + " action=" + data[3] + " clickCount=" + data[4];
        break;
      case JmolConstants.CALLBACK_ANIMFRAME:
        // Note: twos-complement. To get actual frame number, use
        // Math.max(frameNo, -2 - frameNo)
        // -1 means all frames are now displayed
        int[] iData = (int[]) data[1];
        int frameNo = iData[0];
        int fileNo = iData[1];
        int modelNo = iData[2];
        int firstNo = iData[3];
        int lastNo = iData[4];
        boolean isAnimationRunning = (frameNo <= -2);
        int animationDirection = (firstNo < 0 ? -1 : 1);
        int currentDirection = (lastNo < 0 ? -1 : 1);

        /*
         * animationDirection is set solely by the "animation direction +1|-1"
         * script command currentDirection is set by operations such as
         * "anim playrev" and coming to the end of a sequence in
         * "anim mode palindrome"
         * 
         * It is the PRODUCT of these two numbers that determines what direction
         * the animation is going.
         */
        if (doCallback) {
          data = new Object[] { htmlName,
              Integer.valueOf(Math.max(frameNo, -2 - frameNo)),
              Integer.valueOf(fileNo), Integer.valueOf(modelNo),
              Integer.valueOf(Math.abs(firstNo)), Integer.valueOf(Math.abs(lastNo)),
              Integer.valueOf(isAnimationRunning ? 1 : 0),
              Integer.valueOf(animationDirection), Integer.valueOf(currentDirection) };
        }
        break;
      case JmolConstants.CALLBACK_ECHO:
        boolean isPrivate = (data.length == 2);
        boolean isScriptQueued = (isPrivate || ((Integer) data[2]).intValue() == 1);
        if (!doCallback) {
          if (isScriptQueued)
            toConsole = true;
          doCallback = (!isPrivate && 
              (callback = callbacks[type = JmolConstants.CALLBACK_MESSAGE]) != null);
        }
        if (!toConsole)
          output(strInfo);
        break;
      case JmolConstants.CALLBACK_LOADSTRUCT:
        String errorMsg = (String) data[4];
        if (errorMsg != null) {
          errorMsg = (errorMsg.indexOf("NOTE:") >= 0 ? "" : GT
              ._("File Error:")) + errorMsg;
          showStatus(errorMsg);
          notifyCallback(JmolConstants.CALLBACK_MESSAGE, new Object[] { "", errorMsg });
          return;
        }
        break;
      case JmolConstants.CALLBACK_MEASURE:
        // pending, deleted, or completed
        if (!doCallback)
          doCallback = ((callback = callbacks[type = JmolConstants.CALLBACK_MESSAGE]) != null);
        String status = (String) data[3];
        if (status.indexOf("Picked") >= 0 || status.indexOf("Sequence") >= 0) {// picking mode
          showStatus(strInfo); // set picking measure distance
          toConsole = true;
        } else if (status.indexOf("Completed") >= 0) {
          strInfo = status + ": " + strInfo;
          toConsole = true;
        }
        break;
      case JmolConstants.CALLBACK_MESSAGE:
        toConsole = !doCallback;
        doCallback &= (strInfo != null);
        if (!toConsole)
          output(strInfo);
        break;
      case JmolConstants.CALLBACK_PICK:
        showStatus(strInfo);
        toConsole = true;
        break;
      case JmolConstants.CALLBACK_SCRIPT:
        int msWalltime = ((Integer) data[3]).intValue();
        // general message has msWalltime = 0
        // special messages have msWalltime < 0
        // termination message has msWalltime > 0 (1 + msWalltime)
        if (msWalltime > 0) {
          // termination -- button legacy
          notifyScriptTermination();
        } else if (!doCallback) {
          // termination messsage ONLY if script callback enabled -- not to
          // message queue
          // for compatibility reasons
          doCallback = ((callback = callbacks[type = JmolConstants.CALLBACK_MESSAGE]) != null);
        }
        output(strInfo);
        showStatus(strInfo);
        break;
      case JmolConstants.CALLBACK_SYNC:
        sendScript(strInfo, (String) data[2], true, doCallback);
        return;
      }
      if (toConsole) {
        JmolCallbackListener appConsole = (JmolCallbackListener) viewer.getProperty("DATA_API", "getAppConsole", null);
        if (appConsole != null) {
          appConsole.notifyCallback(type, data);
          output(strInfo);
          sendJsTextareaStatus(strInfo);
        }
      }
      if (!doCallback || !mayScript)
        return;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
        if (callback.equals("alert"))
          jsoWindow.call(callback, new Object[] { strInfo });
        else if (callback.length() > 0)
          jsoWindow.call(callback, data);
      } catch (Exception e) {
        if (!haveNotifiedError)
          if (Logger.debugging) {
            Logger.debug(JmolConstants.getCallbackName(type)
                + " call error to " + callback + ": " + e);
          }
        haveNotifiedError = true;
      }
    }

    private void output(String s) {
      if (outputBuffer != null && s != null)
        outputBuffer.append(s).append('\n');
    }

    private void notifyScriptTermination() {
      // this had to do with button callbacks
    }

    private String notifySync(String info, String appletName) {
      String syncCallback = callbacks[JmolConstants.CALLBACK_SYNC];
      if (!mayScript || syncCallback == null)
        return info;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
        if (syncCallback.length() > 0)
          return "" + jsoWindow.call(syncCallback, new Object[] { htmlName,
              info, appletName });
      } catch (Exception e) {
        if (!haveNotifiedError)
          if (Logger.debugging) {
            Logger.debug("syncCallback call error to " + syncCallback + ": "
                + e);
          }
        haveNotifiedError = true;
      }
      return info;
    }

    public void setCallbackFunction(String callbackName, String callbackFunction) {
      if (callbackName.equalsIgnoreCase("modelkit"))
        return;
      //also serves to change language for callbacks and menu
      if (callbackName.equalsIgnoreCase("language")) {
        consoleMessage(""); // clear
        consoleMessage(null); // show default message
        return;
      }
      int id = JmolConstants.getCallbackId(callbackName);
      if (id >= 0 && (loading || id != JmolConstants.CALLBACK_EVAL)) {
        callbacks[id] = callbackFunction;
        return;
      }
      String s = "";
      for (int i = 0; i < JmolConstants.CALLBACK_COUNT; i++)
        s += " " + JmolConstants.getCallbackName(i);
      consoleMessage("Available callbacks include: " + s);
    }

    @Override
    protected void finalize() throws Throwable {
      Logger.debug("MyStatusListener finalize " + this);
      super.finalize();
    }

    public String eval(String strEval) {
      // may be appletName\1script
      int pt = strEval.indexOf("\1");
      if (pt >= 0)
        return sendScript(strEval.substring(pt + 1), strEval.substring(0, pt),
            false, false);
      if (!haveDocumentAccess)
        return "NO EVAL ALLOWED";
      JSObject jsoWindow = null;
      JSObject jsoDocument = null;
      try {
        jsoWindow = JSObject.getWindow(appletWrapper);
        jsoDocument = (JSObject) jsoWindow.getMember("document");
      } catch (Exception e) {
        if (Logger.debugging)
          Logger.debug(" error setting jsoWindow or jsoDocument:" + jsoWindow
              + ", " + jsoDocument);
        return "NO EVAL ALLOWED";
      }
      if (callbacks[JmolConstants.CALLBACK_EVAL] != null) {
        notifyCallback(JmolConstants.CALLBACK_EVAL, new Object[] { null,
            strEval });
        return "";
      }
      try {
        //System.out.println(jsoDocument.eval("!!_jmol.noEval"));
        if (!haveDocumentAccess
            || ((Boolean) jsoDocument.eval("!!_jmol.noEval")).booleanValue())
          return "NO EVAL ALLOWED";
      } catch (Exception e) {
        Logger
            .error("# no _jmol in evaluating " + strEval + ":" + e.toString());
        return "";
      }
      try {
        return "" + jsoDocument.eval(strEval);
      } catch (Exception e) {
        Logger.error("# error evaluating " + strEval + ":" + e.toString());
      }
      return "";
    }

    /**
     * 
     * @param fileName
     * @param type
     * @param text_or_bytes
     * @param quality
     * @return          null (canceled) or a message starting with OK or an error message
     */
    public String createImage(String fileName, String type, Object text_or_bytes,
                              int quality) {
      return null;
    }

    public float[][] functionXY(String functionName, int nX, int nY) {
      /*three options:
       * 
       *  nX > 0  and  nY > 0        return one at a time, with (slow) individual function calls
       *  nX < 0  and  nY > 0        return a string that can be parsed to give the list of values
       *  nX < 0  and  nY < 0        fill the supplied float[-nX][-nY] array directly in JavaScript 
       *  
       */

      //System.out.println("functionXY" + nX + " " + nY  + " " + functionName);
      float[][] fxy = new float[Math.abs(nX)][Math.abs(nY)];
      if (!mayScript || nX == 0 || nY == 0)
        return fxy;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
        if (nX > 0 && nY > 0) { // fill with individual function calls (slow)
          for (int i = 0; i < nX; i++)
            for (int j = 0; j < nY; j++) {
              fxy[i][j] = ((Double) jsoWindow.call(functionName, new Object[] {
                  htmlName, Integer.valueOf(i), Integer.valueOf(j) })).floatValue();
            }
        } else if (nY > 0) { // fill with parsed values from a string (pretty fast)
          String data = (String) jsoWindow.call(functionName, new Object[] {
              htmlName, Integer.valueOf(nX), Integer.valueOf(nY) });
          //System.out.println(data);
          nX = Math.abs(nX);
          float[] fdata = new float[nX * nY];
          Parser.parseStringInfestedFloatArray(data, null, fdata);
          for (int i = 0, ipt = 0; i < nX; i++) {
            for (int j = 0; j < nY; j++, ipt++) {
              fxy[i][j] = fdata[ipt];
            }
          }
        } else { // fill float[][] directly using JavaScript
          jsoWindow.call(functionName, new Object[] { htmlName,
              Integer.valueOf(nX), Integer.valueOf(nY), fxy });
        }
      } catch (Exception e) {
        Logger.error("Exception " + e.getMessage() + " with nX, nY: " + nX
            + " " + nY);
      }
     // for (int i = 0; i < nX; i++)
       // for (int j = 0; j < nY; j++)
         // System.out.println("i j fxy " + i + " " + j + " " + fxy[i][j]);
      return fxy;
    }

    public float[][][] functionXYZ(String functionName, int nX, int nY, int nZ) {
      float[][][] fxyz = new float[Math.abs(nX)][Math.abs(nY)][Math.abs(nZ)];
      if (!mayScript || nX == 0 || nY == 0 || nZ == 0)
        return fxyz;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
       jsoWindow.call(functionName, new Object[] { htmlName,
              Integer.valueOf(nX), Integer.valueOf(nY), Integer.valueOf(nZ), fxyz }); 
      } catch (Exception e) {
        Logger.error("Exception " + e.getMessage() + " for " + functionName + " with nX, nY, nZ: " + nX
            + " " + nY + " " + nZ);
      }
     // for (int i = 0; i < nX; i++)
      // for (int j = 0; j < nY; j++)
        // for (int k = 0; k < nZ; k++)
         // System.out.println("i j k fxyz " + i + " " + j + " " + k + " " + fxyz[i][j][k]);
      return fxyz;
    }

    public void showUrl(String urlString) {
      if (Logger.debugging) {
        Logger.debug("showUrl(" + urlString + ")");
      }
      if (urlString != null && urlString.length() > 0) {
        try {
          URL url = new URL(urlString);
          appletWrapper.getAppletContext().showDocument(url, "_blank");
        } catch (MalformedURLException mue) {
          consoleMessage("Malformed URL:" + urlString);
        }
      }
    }

    private void showStatus(String message) {
      try {
        appletWrapper.showStatus(message);
        sendJsTextStatus(message);
      } catch (Exception e) {
        //ignore if page is closing
      }
    }

    private void consoleMessage(String message) {
      notifyCallback(JmolConstants.CALLBACK_ECHO, new Object[] {"", message });
    }

    private String sendScript(String script, String appletName, boolean isSync,
                              boolean doCallback) {
      if (doCallback) {
        script = notifySync(script, appletName);
        // if the notified JavaScript function returns "" or 0, then 
        // we do NOT continue to notify the other applets
        if (script == null || script.length() == 0 || script.equals("0"))
          return "";
      }

      java.util.List<String> apps = new ArrayList<String>();
      JmolAppletRegistry.findApplets(appletName, syncId, fullName, apps);
      int nApplets = apps.size();
      if (nApplets == 0) {
        if (!doCallback && !appletName.equals("*"))
          Logger.error(fullName + " couldn't find applet " + appletName);
        return "";
      }
      StringBuffer sb = (isSync ? null : new StringBuffer());
      boolean getGraphics = (isSync && script.equals(Viewer.SYNC_GRAPHICS_MESSAGE));
      boolean setNoGraphics = (isSync && script.equals(Viewer.SYNC_NO_GRAPHICS_MESSAGE));
      if (getGraphics)
        gRight = null;
      for (int i = 0; i < nApplets; i++) {
        String theApplet = apps.get(i);
        JmolAppletInterface app = (JmolAppletInterface) JmolAppletRegistry.htRegistry
            .get(theApplet);
        if (Logger.debugging)
          Logger.debug(fullName + " sending to " + theApplet + ": " + script);
        try {
          if (getGraphics || setNoGraphics) {
            gRight = app.setStereoGraphics(getGraphics);
            return "";
          }
          if (isSync)
            app.syncScript(script);
          else
            sb.append(app.scriptWait(script, "output")).append("\n");
        } catch (Exception e) {
          String msg = htmlName + " couldn't send to " + theApplet + ": "
              + script + ": " + e;
          Logger.error(msg);
          if (!isSync)
            sb.append(msg);
        }
      }
      return (isSync ? "" : sb.toString());
    }

    public Map<String, Applet>  getRegistryInfo() {
      JmolAppletRegistry.checkIn(null, null); //cleans registry
      return JmolAppletRegistry.htRegistry;
    }
  }
}
