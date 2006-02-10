/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.davidson.applets;

import java.lang.reflect.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import org.opensourcephysics.display.*;
import java.awt.image.BufferedImage;
import java.awt.event.WindowListener;

/**
 * FrameApplet displays a JFrame from an applicaiton in an HTML page.
 * This Applet can be used to run applications as applets.  Security restrictions may cause some
 * progrgrams to malfunction if the jar file is not signed.
 *
 *@version    0.9 beta
 *@author     Wolfgang Chrstiain
 *@created    December 10, 2006
 */
public class FrameApplet extends JApplet implements Renderable {
  JFrame mainFrame=null;
  String targetClassName;
  String contentName;
  ArrayList newFrames= new ArrayList();
  ArrayList existingFrames= new ArrayList();
  Class target;
  String[] args=null;
  Renderable renderPanel;
  boolean singleFrame=true;

  /**
   *  Gets the parameter attribute of the ApplicationApplet object
   *
   * @param  key  Description of Parameter
   * @param  def  Description of Parameter
   * @return      The parameter value
   */
  public String getParameter(String key, String def) {
    return ( (getParameter(key) != null)
            ? getParameter(key)
            : def);
  }

  /**
   *  Initializes the applet
   */
  public void init() {
      super.init();
      OSPFrame.applet=this;
      OSPFrame.appletMode=true;  // main frame will be embedded; other frames are hidden
      String xmldata=getParameter("xmldata", null);
      if(xmldata!=null){
        args= new String[1];
        args[0] = xmldata;
      }
      targetClassName = getParameter("target", null);
      if(targetClassName==null){
        targetClassName = getParameter("app", null);
      }
      contentName = getParameter("content", null);
      singleFrame=!getParameter("singleframe", "true").trim().equalsIgnoreCase("false");
  }

  public void start() {
    if(mainFrame!=null) return;
    createTarget();
    if (contentName != null) {
      Frame[] frame = Frame.getFrames();
      for (int i = 0, n = frame.length; i < n; i++) {
        if (frame[i] instanceof JFrame &&
            frame[i].getName().equalsIgnoreCase(contentName)) {
          mainFrame = (JFrame) frame[i];
          break;
        }
      }
    }
    if (mainFrame == null) {
      System.out.println("Main frame not found.");
      return;
    }
    removeWindowListeners(mainFrame);
    mainFrame.setVisible(false);
    if (!mainFrame.isActive()){
       mainFrame.dispose(); // don't need this frame
    }
    Container content = mainFrame.getContentPane();
    if ( (mainFrame instanceof OSPFrame) && ( (OSPFrame) mainFrame).isAnimated()) { // look for animated content
      renderPanel = (Renderable) GUIUtils.findInstance(content, Renderable.class);
    }
    getRootPane().setContentPane(content);
    getRootPane().requestFocus();
    if(!singleFrame){
      OSPFrame.appletMode=false;  // all frames will now be shown
      for(int i=0, n=newFrames.size(); i<n; i++){
        if(newFrames.get(i) instanceof OSPFrame){
          ((OSPFrame)newFrames.get(i)).setKeepHidden(false);
        }
      }
      GUIUtils.showDrawingAndTableFrames();
    }

  }

  private void removeWindowListeners(Window frame){
     WindowListener[] wl = frame.getWindowListeners();
     for (int i = 0, n = wl.length; i<n; i++){ // remove window listeners because many windows will call exit(0) when closing
        mainFrame.removeWindowListener(wl[i]);
}

  }

  public BufferedImage render(){
    if(renderPanel!=null)return renderPanel.render();
    return null;
  }

  public Image render(Image image){
    if(renderPanel!=null) renderPanel.render(image);
    return image;
  }

  /**
   * Determines whether the specified class is launchable.
   *
   * @param type the launch class to verify
   * @return <code>true</code> if the class is launchable
   */
   static boolean isLaunchable(Class type) {
    if (type == null)return false;
    try {
      // throws exception if method not found; return value not used
      type.getMethod("main", new Class[] {String[].class});
      return true;
    }
    catch (NoSuchMethodException ex) {
      return false;
    }
  }


  /**
   *  Destroys the applet's resources.
   */
  public void destroy() {
    disposeOwnedFrames();
    target=null;
    mainFrame=null;
    super.destroy();
  }

  private Class createTarget(){
    Class type = null;
    // create the class loader
   // ClassLoader classLoader = URLClassLoader.newInstance(new URL[] {getCodeBase()});
    ClassLoader classLoader=getClass().getClassLoader();
    try {
      type = classLoader.loadClass(targetClassName);
    }
    catch (ClassNotFoundException ex1) {
      System.err.println("Class not found: "+targetClassName);
      return null;
    }
    if(!isLaunchable(type)){
      System.err.println("Main method not found in "+targetClassName);
      return null;
    }
    // get the exisitng frames
    Frame[] frame= Frame.getFrames();
    existingFrames.clear();
    for (int i = 0, n = frame.length; i < n; i++) {
      existingFrames.add(frame[i]);
    }
    // load html data file
    String htmldata = getParameter("htmldata", null);
    if (htmldata != null) {
      TextFrame htmlframe = new TextFrame(htmldata, type);
      htmlframe.setVisible(true);
    }
    // launch the app by invoking main method
    try {
      Method m = type.getMethod("main", new Class[] {String[].class});
      m.invoke(type, new Object[] {args});
      frame = Frame.getFrames();
      for (int i = 0, n = frame.length; i < n; i++) {
        if (frame[i] instanceof JFrame &&
            ( (JFrame) frame[i]).getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) {
          ( (JFrame) frame[i]).setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
          if(mainFrame==null) mainFrame=(JFrame) frame[i];  // assume this is the main application frame
        }
        if (!existingFrames.contains(frame[i])){
           // manage new frames
           newFrames.add(frame[i]);
        }
      }
    }
    catch (NoSuchMethodException ex) {
      System.err.println(ex);
    }
    catch (InvocationTargetException ex) {
      System.err.println(ex);
    }
    catch (IllegalAccessException ex) {
      System.err.println(ex);
    }
    if(newFrames.size()>0 && mainFrame==null && newFrames.get(0) instanceof JFrame) mainFrame=(JFrame)newFrames.get(0);  // assume this is the main application frame
    return type;
  }

  private void disposeOwnedFrames() {
    Frame frame[] = Frame.getFrames();
    for (int i = 0, n = frame.length; i < n; i++) {
      if (frame[i] instanceof JFrame &&
        ( (JFrame) frame[i]).getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE) {
        ( (JFrame) frame[i]).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      }
      if(!existingFrames.contains(frame[i])){  // remove any frames that have been created by this applet
         frame[i].setVisible(false);
         removeWindowListeners(frame[i]);
         frame[i].dispose();
      }
    }
  }

}

/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
