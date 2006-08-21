/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.io.*;
import java.lang.reflect.*;
import java.rmi.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.tools.*;

/**
 *  Drawing Frame: a frame that contains a drawing panel.
 *
 * @author     Wolfgang Christian
 * @created    July 16, 2004
 * @version    1.0
 */
public class DrawingFrame extends OSPFrame implements ClipboardOwner {
  // protected static String resourcesPath = "/org/opensourcephysics/resources/display/";
  // protected static String defaultToolsFileName = "drawing_tools.xml";
  protected JMenu fileMenu, editMenu;
  protected JMenuItem copyItem, pasteItem, replaceItem;
  protected DrawingPanel drawingPanel;
  protected final static int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  protected Window customInspector; // optional custom inspector for this frame
  Tool reply;

  /**
   * DrawingFrame constructor that creates a default DrawingPanel.
   *
   * The default DrawingPanel is an InteractivePanel.
   */
  public DrawingFrame() {
    this("Drawing Frame", new InteractivePanel());
  }

  /**
   *  DrawingFrame constructor specifying the DrawingPanel that will be placed
   *  in the center of the content pane.
   *
   * @param  drawingPanel
   */
  public DrawingFrame(DrawingPanel drawingPanel) {
    this("Drawing Frame", drawingPanel);
  }

  /**
   *  DrawingFrame constructor specifying the title and the DrawingPanel that
   *  will be placed in the center of the content pane.
   *
   * @param  title
   * @param  _drawingPanel
   */
  public DrawingFrame(String title, DrawingPanel _drawingPanel) {
    super(title);
    drawingPanel = _drawingPanel;
    if(drawingPanel!=null) {
      getContentPane().add(drawingPanel, BorderLayout.CENTER);
    }
    getContentPane().add(buttonPanel, BorderLayout.SOUTH); // buttons are added using addButton method.
    pack();
    if(!OSPFrame.appletMode) {
      createMenuBar();
    }
    // responds to data from the DatasetTool
    reply = new Tool() {
      public void send(Job job, Tool replyTo) throws RemoteException {
        XMLControlElement control = new XMLControlElement();
        try {
          control.readXML(job.getXML());
        } catch(RemoteException ex) {}
        ArrayList datasets = drawingPanel.getObjectOfClass(Dataset.class);
        Iterator it = control.getObjects(Dataset.class).iterator();
        while(it.hasNext()) {
          Dataset newData = (Dataset) it.next();
          int id = newData.getID();
          for(int i = 0, n = datasets.size();i<n;i++) {
            if(((Dataset) datasets.get(i)).getID()==id) {
              XMLControl xml = new XMLControlElement(newData); // convert the source to xml
              Dataset.getLoader().loadObject(xml, datasets.get(i)); // copy the data to the destination
              break;
            }
          }
        }
        drawingPanel.repaint();
      }
    };
  }

  /**
   * Renders the drawing panel if the frame is showing and not iconified.
   */
  public void render() {
    if(isIconified()||!isShowing()) {
      return;
    }
    if(drawingPanel!=null) {
      drawingPanel.render();
    } else {
      repaint();
    }
  }

  /**
   * Invalidates image buffers if a drawing panel buffered.
   */
  public void invalidateImage() {
    if(drawingPanel!=null) {
      drawingPanel.invalidateImage();
    }
  }

  /**
   *  Gets the drawing panel.
   *
   * @return    the drawingPanel
   */
  public DrawingPanel getDrawingPanel() {
    return drawingPanel;
  }

  /**
   *  Sets the label for the X (horizontal) axis.
   *
   * @param  label  the label
   */
  public void setXLabel(String label) {
    if(drawingPanel instanceof PlottingPanel) {
      ((PlottingPanel) drawingPanel).setXLabel(label);
    }
  }

  /**
   *  Sets the label for the Y (vertical) axis.
   *
   * @param  label  the label
   */
  public void setYLabel(String label) {
    if(drawingPanel instanceof PlottingPanel) {
      ((PlottingPanel) drawingPanel).setYLabel(label);
    }
  }

  /**
   *  Converts to polar coordinates.
   *
   * @param plotTitle String
   * @param deltaR double
   */
  public void setPolar(String plotTitle, double deltaR) {
    if(drawingPanel instanceof PlottingPanel) {
      ((PlottingPanel) drawingPanel).setPolar(plotTitle, deltaR);
    }
  }

  /**
   *  Converts to cartesian coordinates.
   *
   *
   * @param xLabel String
   * @param yLabel String
   * @param plotTitle String
   */
  public void setCartesian(String xLabel, String yLabel, String plotTitle) {
    if(drawingPanel instanceof PlottingPanel) {
      ((PlottingPanel) drawingPanel).setCartesian(xLabel, yLabel, plotTitle);
    }
  }

  /**
   * Limits the xmin and xmax values during autoscaling so that the mininimum value
   * will be no greater than the floor and the maximum value will be no
   * smaller than the ceil.
   *
   * Setting a floor or ceil value to <code>Double.NaN<\code> will disable that limit.
   *
   * @param floor the xfloor value
   * @param ceil the xceil value
   */
  public void limitAutoscaleX(double floor, double ceil) {
    drawingPanel.limitAutoscaleX(floor, ceil);
  }

  /**
   * Limits ymin and ymax values during autoscaling so that the mininimum value
   * will be no greater than the floor and the maximum value will be no
   * smaller than the ceil.
   *
   * Setting a floor or ceil value to <code>Double.NaN<\code> will disable that limit.
   *
   * @param floor the yfloor value
   * @param ceil the yceil value
   */
  public void limitAutoscaleY(double floor, double ceil) {
    drawingPanel.limitAutoscaleY(floor, ceil);
  }

  /**
   * Autoscale the drawing panel's x axis using min and max values.
   * from measurable objects.
   * @param autoscale
   */
  public void setAutoscaleX(boolean autoscale) {
    if(drawingPanel!=null) {
      drawingPanel.setAutoscaleX(autoscale);
    }
  }

  /**
   * Determines if the panel's x axis autoscale property is true.
   * @return <code>true<\code> if autoscaled.
   */
  public boolean isAutoscaleX() {
    if(drawingPanel!=null) {
      return drawingPanel.isAutoscaleX();
    }
    return false;
  }

  /**
   * Autoscale the y axis using min and max values.
   * from measurable objects.
   * @param autoscale
   */
  public void setAutoscaleY(boolean autoscale) {
    if(drawingPanel!=null) {
      drawingPanel.setAutoscaleY(autoscale);
    }
  }

  /**
   * Determines if the y axis autoscale property is true.
   * @return <code>true<\code> if autoscaled.
   */
  public boolean isAutoscaleY() {
    if(drawingPanel!=null) {
      return drawingPanel.isAutoscaleY();
    } else {
      return false;
    }
  }

  /**
   * Sets the aspect ratio for horizontal to vertical to unity when <code>true<\code>.
   * @param isSquare boolean
   */
  public void setSquareAspect(boolean isSquare) {
    if(drawingPanel!=null) {
      drawingPanel.setSquareAspect(isSquare);
    }
  }

  /**
   * Sets Cartesian axes to log scale.
   *
   * @param  logX
   * @param  logY
   */
  public void setLogScale(boolean logX, boolean logY) {
    if((drawingPanel!=null)&&(drawingPanel instanceof PlottingPanel)) {
      ((PlottingPanel) drawingPanel).setLogScale(logX, logY);
    }
  }

  /**
   * Sets the scale using pixels per unit.
   *
   * @param enable boolean enable fixed pixels per unit
   * @param xPixPerUnit double
   * @param yPixPerUnit double
   */
  public void setPixelsPerUnit(boolean enable, double xPixPerUnit, double yPixPerUnit) {
    drawingPanel.setPixelsPerUnit(enable, xPixPerUnit, yPixPerUnit);
  }

  /**
   * Sets the drawing panel's preferred scale.
   * @param xmin
   * @param xmax
   * @param ymin
   * @param ymax
   */
  public void setPreferredMinMax(double xmin, double xmax, double ymin, double ymax) {
    if(drawingPanel!=null) {
      drawingPanel.setPreferredMinMax(xmin, xmax, ymin, ymax);
    }
  }

  /**
   * Sets the drawing panel's preferred scale in the vertical direction.
   * @param ymin
   * @param ymax
   */
  public void setPreferredMinMaxY(double ymin, double ymax) {
    if(drawingPanel!=null) {
      drawingPanel.setPreferredMinMaxY(ymin, ymax);
    }
  }

  /**
   * Sets the drawing panel's preferred scale in the horizontal direction.
   * @param xmin the minimum value
   * @param xmax the maximum value
   */
  public void setPreferredMinMaxX(double xmin, double xmax) {
    if(drawingPanel!=null) {
      drawingPanel.setPreferredMinMaxX(xmin, xmax);
    }
  }

  /**
   * Clears data and repaints the drawing panel within this frame.
   */
  public void clearDataAndRepaint() {
    clearData();
    drawingPanel.repaint();
  }

  /**
   * Cleares drawable objects added by the user from this frame.
   */
  public void clearDrawables() {
    drawingPanel.clear(); // removes all drawables
  }

  /**
   * Adds a drawable object to the frame's drawing panel.
   * @param drawable
   */
  public synchronized void addDrawable(Drawable drawable) {
    if(drawingPanel!=null) {
      drawingPanel.addDrawable(drawable);
    }
  }

  /**
   * Replaces a drawable object with another drawable.
   *
   * @param oldDrawable Drawable
   * @param newDrawable Drawable
   */
  public synchronized void replaceDrawable(Drawable oldDrawable, Drawable newDrawable) {
    if(drawingPanel!=null) {
      drawingPanel.replaceDrawable(oldDrawable, newDrawable);
    }
  }

  /**
   * Removes a drawable object to the frame's drawing panel.
   * @param drawable
   */
  public synchronized void removeDrawable(Drawable drawable) {
    if(drawingPanel!=null) {
      drawingPanel.removeDrawable(drawable);
    }
  }

  /**
   * Shows a message in a yellow text box in the lower right hand corner.
   *
   * @param msg
   */
  public void setMessage(String msg) {
    drawingPanel.setMessage(msg);
  }

  /**
   * Shows a message in a yellow text box at the given location.
   *
   * location 0=bottom left
   * location 1=bottom right
   * location 2=top right
   * location 3=top left
   *
   * @param msg
   * @param location
   */
  public void setMessage(String msg, int location) {
    drawingPanel.setMessage(msg, location);
  }

  /**
   * Gets objects of a specific class from the drawing panel.
   *
   * Assignable subclasses are NOT returned.  Interfaces CANNOT be specified.
   * The same objects will be in the drawable list and the cloned list.
   *
   * @param c the class of the object
   *
   * @return the list
   */
  public synchronized ArrayList getObjectOfClass(Class c) {
    if(drawingPanel!=null) {
      return drawingPanel.getObjectOfClass(c);
    } else {
      return null;
    }
  }

  /**
   * Gets Drawable previously objects added by the user.
   *
   * @return the list
   */
  public synchronized ArrayList getDrawables() {
    if(drawingPanel!=null) {
      return drawingPanel.getDrawables();
    } else {
      return new ArrayList(); // retun an empty list
    }
  }

  /**
   * Gets Drawable objects added by the user of an assignable type. The list contains
   * objects that are assignable from the class or interface.
   *
   * @param c the type of Drawable object
   *
   * @return the cloned list
   *
   * @see #getObjectOfClass(Class c)
   */
  public synchronized ArrayList getDrawables(Class c) {
    if(drawingPanel!=null) {
      return drawingPanel.getDrawables(c);
    } else {
      return new ArrayList(); // retun an empty list
    }
  }

  /**
   * Removes all objects of the given class from the drawable list.
   *
   * Assignable subclasses are NOT removed.  Interfaces CANNOT be specified.
   *
   * @param c the class
   */
  public synchronized void removeObjectsOfClass(Class c) {
    drawingPanel.removeObjectsOfClass(c);
  }

  /**
   * Sets the interactive mouse handler if the drawing panel is an interactive panel.
   *
   * Throws an invalid cast exception if the panel is not of the correct type.
   *
   * @param handler the mouse handler
   */
  public void setInteractiveMouseHandler(InteractiveMouseHandler handler) {
    ((InteractivePanel) drawingPanel).setInteractiveMouseHandler(handler);
  }

  /**
   *  Adds the drawing panel to the the frame. The panel is added to the center
   *  of the frame's content pane.
   *
   * @param  _drawingPanel
   */
  public void setDrawingPanel(DrawingPanel _drawingPanel) {
    if(drawingPanel!=null) { // remove the old drawing panel.
      getContentPane().remove(drawingPanel);
    }
    drawingPanel = _drawingPanel;
    if(drawingPanel!=null) {
      getContentPane().add(drawingPanel, BorderLayout.CENTER);
    }
  }

  /**
   *  This is a hack to fix a bug when the reload button is pressed in browsers
   *  running JDK 1.4.
   *
   * @param  g
   */
  public void paint(Graphics g) {
    if(!appletMode) {
      super.paint(g);
      return;
    }
    try {
      super.paint(g);
    } catch(Exception ex) {
      System.err.println("OSPFrame paint error: "+ex.toString());
      System.err.println("Title: "+this.getTitle());
    }
  }

  /**
   * Enables the paste edit menu item.
   * @param enable boolean
   */
  public void setEnabledPaste(boolean enable) {
    pasteItem.setEnabled(enable);
  }

  /**
   * Pastes drawables found in the specified xml control.
   *
   * @param control the xml control
   */
  protected void pasteAction(XMLControlElement control) {
    // get Drawables using an xml tree chooser
    XMLTreeChooser chooser = new XMLTreeChooser("Select Drawables", "Select one or more drawables.", this);
    java.util.List props = chooser.choose(control, Drawable.class);
    if(!props.isEmpty()) {
      Iterator it = props.iterator();
      while(it.hasNext()) {
        XMLControl prop = (XMLControl) it.next();
        Drawable drawable = (Drawable) prop.loadObject(null);
        addDrawable(drawable);
      }
    }
    drawingPanel.repaint();
  }

  /**
   * Enables the replace edit menu item.
   * @param enable boolean
   */
  public void setEnabledReplace(boolean enable) {
    replaceItem.setEnabled(enable);
  }

  /**
   * Replaces the drawables with the drawables found in the specified XML control.
   * @param control XMLControlElement
   */
  public void replaceAction(XMLControlElement control) {
    clearDrawables();
    pasteAction(control);
  }

  /**
   * Copies objects found in the specified xml control.
   *
   * @param control the xml control
   */
  protected void copyAction(XMLControlElement control) {
    StringSelection data = new StringSelection(control.toXML());
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(data, this);
  }

  /**
   * Implementation of ClipboardOwner interface.
   *
   * Override this method to receive notification that data copied to the clipboard has changed.
   *
   * @param clipboard Clipboard
   * @param contents Transferable
   */
  public void lostOwnership(Clipboard clipboard, Transferable contents) {}

  /**
   * Enables the copy edit menu item.
   * @param enable boolean
   */
  public void setEnabledCopy(boolean enable) {
    copyItem.setEnabled(enable);
  }

  /**
   * Creates a standard DrawingFrame menu bar and adds it to the frame.
   */
  private void createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    fileMenu = new JMenu("File");
    JMenuItem printItem = new JMenuItem("Print...");
    printItem.setAccelerator(KeyStroke.getKeyStroke('P', MENU_SHORTCUT_KEY_MASK));
    printItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(drawingPanel);
        if(printerJob.printDialog()) {
          try {
            printerJob.print();
          } catch(PrinterException pe) {
            JOptionPane.showMessageDialog(DrawingFrame.this, "A printing error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });
    JMenuItem saveXMLItem = new JMenuItem("Save XML...");
    saveXMLItem.setAccelerator(KeyStroke.getKeyStroke('S', MENU_SHORTCUT_KEY_MASK));
    saveXMLItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveXML();
      }
    });
    JMenuItem exportItem = new JMenuItem("Export...");
    exportItem.setAccelerator(KeyStroke.getKeyStroke('E', MENU_SHORTCUT_KEY_MASK));
    exportItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          ExportTool.getTool().send(new LocalJob(drawingPanel), null);
        } catch(RemoteException ex) {}
      }
    });
    JMenu saveImage = new JMenu("Save Image");
    JMenuItem epsMenuItem = new JMenuItem("eps...");
    JMenuItem jpegMenuItem = new JMenuItem("jpeg...");
    JMenuItem pngMenuItem = new JMenuItem("png...");
    saveImage.add(epsMenuItem);
    saveImage.add(jpegMenuItem);
    saveImage.add(pngMenuItem);
    epsMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIUtils.saveImage(drawingPanel, "eps", DrawingFrame.this);
      }
    });
    jpegMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIUtils.saveImage(drawingPanel, "jpeg", DrawingFrame.this);
      }
    });
    pngMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        GUIUtils.saveImage(drawingPanel, "png", DrawingFrame.this);
      }
    });
    JMenuItem inspectItem = new JMenuItem("Inspect");
    inspectItem.setAccelerator(KeyStroke.getKeyStroke('I', MENU_SHORTCUT_KEY_MASK));
    inspectItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        inspectXML(); // cannot use a static method here because of run-time binding
      }
    });
    fileMenu.add(printItem);
    fileMenu.add(saveXMLItem);
    fileMenu.add(exportItem);
    fileMenu.add(saveImage);
    fileMenu.add(inspectItem);
    menuBar.add(fileMenu);
    editMenu = new JMenu("Edit");
    menuBar.add(editMenu);
    copyItem = new JMenuItem("Copy");
    copyItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        XMLControlElement control = new XMLControlElement(DrawingFrame.this);
        control.saveObject(null);
        copyAction(control);
      }
    });
    editMenu.add(copyItem);
    pasteItem = new JMenuItem("Paste");
    pasteItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          Transferable data = clipboard.getContents(null);
          XMLControlElement control = new XMLControlElement();
          control.readXML((String) data.getTransferData(DataFlavor.stringFlavor));
          pasteAction(control);
        } catch(UnsupportedFlavorException ex) {}
        catch(IOException ex) {}
        catch(HeadlessException ex) {}
      }
    });
    pasteItem.setEnabled(false); // not supported yet
    editMenu.add(pasteItem);
    replaceItem = new JMenuItem("Replace");
    replaceItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          Transferable data = clipboard.getContents(null);
          XMLControlElement control = new XMLControlElement();
          control.readXML((String) data.getTransferData(DataFlavor.stringFlavor));
          replaceAction(control);
        } catch(UnsupportedFlavorException ex) {}
        catch(IOException ex) {}
        catch(HeadlessException ex) {}
      }
    });
    replaceItem.setEnabled(false); // not supported yet
    editMenu.add(replaceItem);
    setJMenuBar(menuBar);
    loadToolsMenu();
    JMenu helpMenu = new JMenu("Help");
    menuBar.add(helpMenu);
    JMenuItem aboutItem = new JMenuItem("About OSP...");
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ControlFrame.showAboutDialog(DrawingFrame.this);
      }
    });
    helpMenu.add(aboutItem);
  }

  /**
   * Adds launchable tools to the specified menu.
   *
   */
  protected void loadToolsMenu() {
    JMenuBar menuBar = getJMenuBar();
    if(menuBar==null) {
      return;
    }
    // add menu item
    JMenu testing = new JMenu("Tools");
    menuBar.add(testing);
    // test dataset tool
    JMenuItem datasetItem = new JMenuItem("Dataset Tool");
    testing.add(datasetItem);
    datasetItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          DatasetTool tool = DatasetTool.getTool();
          tool.send(new LocalJob(drawingPanel), reply);
          tool.setVisible(true);
        } catch(RemoteException ex) {}
      }
    });
  }

  /**
   * Sets a custom  properties inspector window.
   *
   * @param w the new inspector window
   */
  public void setCustomInspector(Window w) {
    if(customInspector!=null) {
      customInspector.setVisible(false); // hide the current inspector window
    }
    customInspector = w;
  }

  /**
   * Inspects the drawing frame by using an xml document tree.
   */
  public void inspectXML() {
    if(customInspector!=null) {
      customInspector.setVisible(true);
      return;
    }
    XMLControlElement xml = null;
    try {
      // if drawingPanel provides an xml loader, inspect the drawingPanel
      Method method = drawingPanel.getClass().getMethod("getLoader", (Class[]) null);
      if((method!=null)&&Modifier.isStatic(method.getModifiers())) {
        xml = new XMLControlElement(drawingPanel);
      }
    } catch(NoSuchMethodException ex) {
      // this drawing panel cannot be inspected
      return;
    }
    // display a TreePanel in a modal dialog
    XMLTreePanel treePanel = new XMLTreePanel(xml);
    JDialog dialog = new JDialog((java.awt.Frame) null, true);
    dialog.setContentPane(treePanel);
    dialog.setSize(new Dimension(600, 300));
    dialog.setVisible(true);
  }

  public void saveXML() {
    JFileChooser chooser = getChooser();
    int result = chooser.showSaveDialog(null);
    if(result==JFileChooser.APPROVE_OPTION) {
      OSPFrame.chooserDir = chooser.getCurrentDirectory().toString();
      File file = chooser.getSelectedFile();
      // check to see if file already exists
      if(file.exists()) {
        int selected = JOptionPane.showConfirmDialog(null, "Replace existing "+file.getName()+"?", "Replace File", JOptionPane.YES_NO_CANCEL_OPTION);
        if(selected!=JOptionPane.YES_OPTION) {
          return;
        }
      }
      String fileName = XML.getRelativePath(file.getAbsolutePath());
      if((fileName==null)||fileName.trim().equals("")) {
        return;
      }
      int i = fileName.toLowerCase().lastIndexOf(".xml");
      if(i!=fileName.length()-4) {
        fileName += ".xml";
      }
      try {
        // if drawingPanel provides an xml loader, save the drawingPanel
        Method method = drawingPanel.getClass().getMethod("getLoader", (Class[]) null);
        if((method!=null)&&Modifier.isStatic(method.getModifiers())) {
          XMLControl xml = new XMLControlElement(drawingPanel);
          xml.write(fileName);
        }
      } catch(NoSuchMethodException ex) {
        // this drawingPanel cannot be saved
        return;
      }
    }
  }

  /**
   * Returns an XML.ObjectLoader to save and load data for this program.
   *
   * @return the object loader
   */
  public static XML.ObjectLoader getLoader() {
    return new DrawingFrameLoader();
  }

  static protected class DrawingFrameLoader implements XML.ObjectLoader {

    /**
     * createObject
     *
     * @param control XMLControl
     * @return Object
     */
    public Object createObject(XMLControl control) {
      DrawingFrame frame = new DrawingFrame();
      frame.setTitle(control.getString("title"));
      frame.setLocation(control.getInt("location x"), control.getInt("location y"));
      frame.setSize(control.getInt("width"), control.getInt("height"));
      if(control.getBoolean("showing")) {
        frame.setVisible(true);
      }
      return frame;
    }

    /**
     * Save data object's data in the control.
     *
     * @param control XMLControl
     * @param obj Object
     */
    public void saveObject(XMLControl control, Object obj) {
      DrawingFrame frame = (DrawingFrame) obj;
      control.setValue("title", frame.getTitle());
      control.setValue("showing", frame.isShowing());
      control.setValue("location x", frame.getLocation().x);
      control.setValue("location y", frame.getLocation().y);
      control.setValue("width", frame.getSize().width);
      control.setValue("height", frame.getSize().height);
      control.setValue("drawing panel", frame.getDrawingPanel());
    }

    /**
     * Loads the object with data from the control.
     *
     * @param control XMLControl
     * @param obj Object
     * @return Object
     */
    public Object loadObject(XMLControl control, Object obj) {
      DrawingFrame frame = ((DrawingFrame) obj);
      DrawingPanel panel = frame.getDrawingPanel();
      panel.clear();
      XMLControl panelControl = control.getChildControl("drawing panel");
      panelControl.loadObject(panel);
      panel.repaint();
      frame.setTitle(control.getString("title"));
      frame.setLocation(control.getInt("location x"), control.getInt("location y"));
      frame.setSize(control.getInt("width"), control.getInt("height"));
      if(control.getBoolean("showing")) {
        frame.setVisible(true);
      }
      return obj;
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
