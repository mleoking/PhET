/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.print.*;
import java.io.File;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * OSPFrame is the base class for Open Source Physics JFrames such as DrawingFrame and DataTableFrame.
 *
 * Copyright:    Copyright (c) 2002
 * @author       Wolfgang Christian
 * @version 1.0
 */
public class OSPFrame extends JFrame implements Printable {

  /**
   * Chooser starting directory.
   */
  public static String chooserDir; // value is set in static block
  static int topx = 10;
  static int topy = 100;

  /** Set to <I>true</I> if a program should automatically render this frame after every animation step. */
  protected boolean animated = false;

  /** Set to <I>true</I> if a program should automatically clear the data when an animation is initialized. */
  protected boolean autoclear = false;

  /** Set <I>true</I> if the program is an applet. */
  public static boolean appletMode = false;

  /** Set <I>true</I> if the Frame's defaultCloseOperation has been changed by Launcher. */
  private static boolean wishesToExit = false;

  /**
   * Field myApplet provides a static reference to an applet context
   * so that the document base and code base can be obtained in applet mode.
   */
  public static JApplet applet;

  /** The thread group that created this object. */
  public ThreadGroup constructorThreadGroup = Thread.currentThread().getThreadGroup();
  protected boolean keepHidden = false;
  protected BufferStrategy strategy;
  private static JFileChooser chooser;
  protected JPanel buttonPanel = new JPanel();

  static {
    // sets the default directory for the chooser
    try { // system properties may not be readable in some contexts
      chooserDir = System.getProperty("user.dir", null);
    } catch(SecurityException ex) {
      chooserDir = null;
    }
  }

  /**
   * OSPFrame constructor with a title.
   */
  public OSPFrame(String title) {
    super(GUIUtils.parseTeX(title));
    if(appletMode) {
      keepHidden = true;
    }
    buttonPanel.setVisible(false);
    setLocation(topx, topy);
    Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    topx = Math.min(topx+20, (int) d.getWidth()-100);
    topy = Math.min(topy+20, (int) d.getHeight()-100);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
  }

  /**
   * OSPFrame constructor.
   */
  public OSPFrame() {
    this("Open Source Physics");
  }

  /**
   * OSPFrame constructor with a new content pane.
   */
  public OSPFrame(Container contentPane) {
    this();
    setContentPane(contentPane);
  }

  /**
   * Sets the title for this frame to the specified string after converting TeX math symbols to characters.
   * @param title the title to be displayed in the frame's border.
   *              A <code>null</code> value
   *              is treated as an empty string, "".
   * @see      #getTitle
   */
  public void setTitle(String title) {
    super.setTitle(GUIUtils.parseTeX(title));
  }

  /**
   * Gets the ICONIFIED flag for this frame.
   *
   * @return boolean true if frame is iconified; false otherwise
   */
  public boolean isIconified() {
    return(getExtendedState()&ICONIFIED)==1;
  }

  /**
   * Invalidates image buffers if a drawing panel is buffered.
   */
  public void invalidateImage() {
    // default does nothing
  }

  /**
   * Reads the animated property.
   *
   * @return boolean
   */
  public boolean isAnimated() {
    return animated;
  }

  /**
   * Sets the animated property.
   *
   * @param animated
   */
  public void setAnimated(boolean animated) {
    this.animated = animated;
  }

  /**
   * Reads the animated property.
   *
   * @return boolean
   */
  public boolean isAutoclear() {
    return autoclear;
  }

  /**
   * Sets the autoclear property.
   *
   * @param autoclear
   */
  public void setAutoclear(boolean autoclear) {
    this.autoclear = autoclear;
  }

  /**
   * Clears data from drawing objects within this frame.
   *
   * The default method does nothing.
   * Override this method to select the object(s) and the data to be cleared.
   */
  public void clearData() {}

  public void setSize(int width, int height) {
    super.setSize(width, height);
    validate();
  }

  /**
   * Shows the frame on the screen if the keep hidden flag is false.
   *
   * @deprecated
   */
  public void show() {
    if(!keepHidden) {
      super.show();
    }
  }

   public void dispose() {
     keepHidden=true;
     super.dispose();
   }

  /**
   * Shows or hides this component depending on the value of parameter
   * <code>b</code> and the <code>keepHidden</code> flag.
   *
   * OSP Applets often keep windows hidden.
   *
   * @param b
   */
  public void setVisible(boolean b) {
    if(!keepHidden) {
      super.setVisible(b);
    }
  }

  /**
   * Sets the keepHidden flag.
   *
   * @param _keepHidden
   */
  public void setKeepHidden(boolean _keepHidden) {
    keepHidden = _keepHidden;
    if(keepHidden) {
      super.setVisible(false);
    }
  }

  /**
   * Reads the keepHidden flag.
   *
   */
  public boolean isKeepHidden() {
    return keepHidden;
  }

  /**
   * Gets the ThreadGroup that constructed this frame.
   *
   * @return the ThreadGroup
   */
  public ThreadGroup getConstructorThreadGroup() {
    return constructorThreadGroup;
  }

  /**
   * Creates a BufferStrategy based on the capabilites of the hardware.
   */
  public void createBufferStrategy() {
    createBufferStrategy(2);
    strategy = this.getBufferStrategy();
  }

  /**
   * Shows (repaints) the frame useing the current BufferStrategy.
   */
  public void bufferStrategyShow() {
    if((strategy)==null) {
      createBufferStrategy();
    }
    if(isIconified()||!isShowing()) {
      return;
    }
    Graphics g = strategy.getDrawGraphics();
    paintComponents(g);
    g.dispose();
    strategy.show();
  }

  /**
   * Renders the frame.  Subclass this method to render the contents of this frame in the calling thread.
   */
  public void render() {}

  /**
   * Gets a menu with the given name from the menu bar.  Returns null if menu item does not exist.
   *
   * @param menuName String
   * @return JMenu
   */
  public JMenu getMenu(String menuName) {
    JMenuBar menuBar = getJMenuBar();
    if(menuBar==null) {
      return null;
    }
    menuName = menuName.trim();
    JMenu menu = null;
    for(int i = 0;i<menuBar.getMenuCount();i++) {
      JMenu next = menuBar.getMenu(i);
      if(next.getText().trim().equals(menuName)) {
        menu = next;
        break;
      }
    }
    return menu;
  }

  /**
   * Removes a menu with the given name from the menu bar and returns the removed item.
   * Returns null if menu item does not exist.
   *
   * @param menuName String
   * @return JMenu
   */
  public JMenu removeMenu(String menuName) {
    JMenuBar menuBar = getJMenuBar();
    if(menuBar==null) {
      return null;
    }
    menuName = menuName.trim();
    JMenu menu = null;
    for(int i = 0;i<menuBar.getMenuCount();i++) {
      JMenu next = menuBar.getMenu(i);
      if(next.getText().trim().equals(menuName)) {
        menu = next;
        menuBar.remove(i);
        break;
      }
    }
    return menu;
  }

  /**
   * Removes a menu item with the given name from the menu bar and returns the removed item.
   * Returns null if menu item does not exist.
   *
   * @param menuName String
   * @return JMenu
   */
  public JMenuItem removeMenuItem(String menuName, String itemName) {
    JMenu menu = getMenu(menuName);
    if(menu==null) {
      return null;
    }
    itemName = itemName.trim();
    JMenuItem item = null;
    for(int i = 0;i<menu.getItemCount();i++) {
      JMenuItem next = menu.getItem(i);
      if(next.getText().trim().equals(itemName)) {
        item = next;
        menu.remove(i);
        break;
      }
    }
    return item;
  }

  /**
* Creates a menu in the menu bar from the given XML document.
* @param xmlMenu name of the xml file with menu data
*/
  public void parseXMLMenu(String xmlMenu) {
    parseXMLMenu(xmlMenu, null);
  }

  /**
   * Creates a menu in the menu bar from the given XML document.
   * @param xmlMenu name of the xml file with menu data
   * @param type the class to load the menu, may be null
   */
  public void parseXMLMenu(String xmlMenu, Class type) {
    XMLControl xml = null;
    if(type!=null) {
      org.opensourcephysics.tools.Resource res = org.opensourcephysics.tools.ResourceLoader.getResource(xmlMenu, type);
      if(res!=null) {
        xml = new XMLControlElement(res.getString());
      }
    }
    if(xml==null) {
      xml = new XMLControlElement(xmlMenu);
    }
    if(xml.failedToRead()) {
      OSPLog.info("Menu not found: "+xmlMenu);
    } else {
      type = xml.getObjectClass();
      if(type!=null&&org.opensourcephysics.tools.LaunchNode.class.isAssignableFrom(type)) {
        // load the xml data into a launch node and add the menu item
        org.opensourcephysics.tools.LaunchNode node = (org.opensourcephysics.tools.LaunchNode) xml.loadObject(null);
        JMenuBar menuBar = getJMenuBar();
        if(menuBar==null) {
          return;
        }
        // get the menu name and create the menu if null
        String menuName = node.toString();
        JMenu menu = getMenu(menuName);
        if(menu==null) {
          menu = new JMenu(menuName);
          menuBar.add(menu);
          menuBar.validate();
        }
        // add the node item to the menu
        node.addMenuItemsTo(menu);
        OSPLog.finest("Menu loaded: "+xmlMenu);
      }
    }
  }

  /**
   * Gets a file chooser.
   *
   * The choose is static and will therefore be the same for all OSPFrames.
   *
   * @return the chooser
   */
  public static JFileChooser getChooser() {
    if(chooser!=null) {
      return chooser;
    }
    chooser = (chooserDir==null) ? new JFileChooser() : new JFileChooser(new File(chooserDir));
    javax.swing.filechooser.FileFilter defaultFilter = chooser.getFileFilter();
    javax.swing.filechooser.FileFilter xmlFilter = new javax.swing.filechooser.FileFilter() {
      // accept all directories and *.xml files.
      public boolean accept(File f) {
        if(f==null) {
          return false;
        }
        if(f.isDirectory()) {
          return true;
        }
        String extension = null;
        String name = f.getName();
        int i = name.lastIndexOf('.');
        if((i>0)&&(i<name.length()-1)) {
          extension = name.substring(i+1).toLowerCase();
        }
        if((extension!=null)&&(extension.equals("xml"))) {
          return true;
        }
        return false;
      }
      // the description of this filter
      public String getDescription() {
        return "XML files";
      }
    };
    javax.swing.filechooser.FileFilter txtFilter = new javax.swing.filechooser.FileFilter() {
      // accept all directories and *.txt files.
      public boolean accept(File f) {
        if(f==null) {
          return false;
        }
        if(f.isDirectory()) {
          return true;
        }
        String extension = null;
        String name = f.getName();
        int i = name.lastIndexOf('.');
        if((i>0)&&(i<name.length()-1)) {
          extension = name.substring(i+1).toLowerCase();
        }
        if((extension!=null)&&extension.equals("txt")) {
          return true;
        }
        return false;
      }
      // the description of this filter
      public String getDescription() {
        return "TXT files";
      }
    };
    chooser.addChoosableFileFilter(xmlFilter);
    chooser.addChoosableFileFilter(txtFilter);
    chooser.setFileFilter(defaultFilter);
    return chooser;
  }

  /**
   * Draws the panel into a graphics object suitable for printing.
   * @param g
   * @param pageFormat
   * @param pageIndex
   * @return status code
   * @exception PrinterException
   */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
    if(pageIndex>=1) { // only one page available
      return Printable.NO_SUCH_PAGE;
    }
    if(g==null) {
      return Printable.NO_SUCH_PAGE;
    }
    Graphics2D g2 = (Graphics2D) g;
    double scalex = pageFormat.getImageableWidth()/(double) getWidth();
    double scaley = pageFormat.getImageableHeight()/(double) getHeight();
    double scale = Math.min(scalex, scaley);
    g2.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
    g2.scale(scale, scale);
    paintAll(g2);
    return Printable.PAGE_EXISTS;
  }

  /**
  *  Adds a custom button to the control's frame.
  *
  * @param  methodName   the name of the method; the method has no parameters
  * @param  text         the button's text label
  * @param  toolTipText  the button's tool tip text
  * @param  target       the target for the method
  * @return              the custom button
  */
  public JButton addButton(String methodName, String text, String toolTipText, final Object target) {
    JButton b = new JButton(text);
    b.setToolTipText(toolTipText);
    Class[] parameters = {};
    try {
      final java.lang.reflect.Method m = target.getClass().getMethod(methodName, parameters);
      b.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Object[] args = {};
          try {
            m.invoke(target, args);
          } catch(IllegalAccessException iae) {
            System.err.println(iae);
          } catch(java.lang.reflect.InvocationTargetException ite) {
            System.err.println(ite);
          }
        }
      });
      buttonPanel.setVisible(true);
      buttonPanel.add(b);
      validate();
      pack();
    } catch(NoSuchMethodException nsme) {
      System.err.println("Error adding custom button "+text+". The method "+methodName+"() does not exist.");
    }
    return b;
  }

  public void setDefaultCloseOperation(int operation) {
    if(operation==JFrame.EXIT_ON_CLOSE&&OSPParameters.launchingInSingleVM) {
      operation = JFrame.DISPOSE_ON_CLOSE;
      wishesToExit = true;
    }
    super.setDefaultCloseOperation(operation);
  }

  public boolean wishesToExit() {
    return wishesToExit;
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
