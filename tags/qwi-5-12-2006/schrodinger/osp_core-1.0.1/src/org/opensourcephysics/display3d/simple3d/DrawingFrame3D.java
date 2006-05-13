/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display3d.simple3d;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display3d.core.CameraInspector;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.tools.*;

/**
 * DrawingFrame: a frame that contains a generic drawing panel.
 * @author     Francisco Esquembre
 * @author     Adapted from Wolfgang Christian
 * @version    March 2005
 */
public class DrawingFrame3D extends OSPFrame implements ClipboardOwner, org.opensourcephysics.display3d.core.DrawingFrame3D {
  protected JMenu fileMenu, editMenu;
  protected JMenuItem copyItem, pasteItem, replaceItem;
  protected JMenu visualMenu, displayMenu, decorationMenu, cursorMenu;
  protected JMenuItem displayPerspectiveItem, displayNoPerspectiveItem, displayXYItem, displayXZItem, displayYZItem;
  protected JMenuItem decorationCubeItem, decorationNoneItem, decorationAxesItem;
  protected JMenuItem cursorNoneItem, cursorCubeItem, cursorXYZItem, cursorCrosshairItem;
  protected JMenuItem zoomToFitItem, resetCameraItem, cameraItem;
  protected JFrame cameraInspectorFrame;
  protected JMenuBar menuBar = new JMenuBar();
  protected org.opensourcephysics.display3d.core.DrawingPanel3D drawingPanel;
  protected final static int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  /**
   *  Default DrawingFrame constructor
   */
  public DrawingFrame3D() {
    this("Drawing Frame", null);
  }

  /**
   *  DrawingFrame constructor specifying the DrawingPanel that will be placed
   *  in the center of the content pane.
   * @param  drawingPanel
   */
  public DrawingFrame3D(DrawingPanel3D drawingPanel) {
    this("Drawing Frame", drawingPanel);
  }

  /**
   *  DrawingFrame constructor specifying the title and the DrawingPanel that
   *  will be placed in the center of the content pane.
   *
   * @param  title
   * @param  _drawingPanel
   */
  public DrawingFrame3D(String title, DrawingPanel3D _drawingPanel) {
    super(title);
    drawingPanel = _drawingPanel;
    if(drawingPanel!=null) {
      getContentPane().add((JPanel) drawingPanel, BorderLayout.CENTER);
    }
    pack();
    if(!OSPFrame.appletMode) {
      createMenuBar();
    }
    setAnimated(true); // simulations will automatically render this frame after "doStep."
    setEnabledPaste(true);
    setEnabledReplace(true);
  }

  /**
   * Renders the drawing panel if the frame is showing and not iconified.
   */
  public void render() {
    drawingPanel.render();
  }

  /**
 * Shows a message in a yellow text box in the lower right hand corner.
 *
 * @param msg
 */
  public void setMessage(String msg) {
    ((org.opensourcephysics.display3d.simple3d.DrawingPanel3D) drawingPanel).setMessage(msg); // the default message box
  }

  /**
   * Shows a message in a yellow text box.
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
    ((org.opensourcephysics.display3d.simple3d.DrawingPanel3D) drawingPanel).setMessage(msg, location);
  }

  /**
   *  Gets the drawing panel.
   *
   * @return    the drawingPanel
   */
  public org.opensourcephysics.display3d.core.DrawingPanel3D getDrawingPanel3D() {
    return drawingPanel;
  }

  /**
   *  Adds the drawing panel to the the frame. The panel is added to the center
   *  of the frame's content pane.
   *
   * @param  _drawingPanel
   */
  public void setDrawingPanel3D(org.opensourcephysics.display3d.core.DrawingPanel3D _drawingPanel) {
    if(drawingPanel!=null) { // remove the old drawing panel.
      getContentPane().remove((JPanel) drawingPanel);
    }
    drawingPanel = _drawingPanel;
    if(drawingPanel!=null) {
      getContentPane().add((JPanel) drawingPanel, BorderLayout.CENTER);
    }
    pack();
  }

  /**
   * Getting the pointer to the real JFrame in it
   * @return JFrame
   */
  public javax.swing.JFrame getJFrame() {
    return this;
  }

  /**
   * Enables the paste edit menu item.
   * @param enable boolean
   */
  public void setEnabledPaste(boolean enable) {
    pasteItem.setEnabled(enable);
  }

  /**
   * Paste action
   *
   */
  protected void pasteAction() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable data = clipboard.getContents(null);
      XMLControlElement control = new XMLControlElement();
      control.readXML((String) data.getTransferData(DataFlavor.stringFlavor));
      // get Drawables using an xml tree chooser
      XMLTreeChooser chooser = new XMLTreeChooser("Select Elements", "Select one or more elements.", this);
      java.util.List props = chooser.choose(control, Element.class);
      if(!props.isEmpty()) {
        Iterator it = props.iterator();
        while(it.hasNext()) {
          XMLControl prop = (XMLControl) it.next();
          Element element = (Element) prop.loadObject(null);
          System.out.println("Adding element "+element);
          drawingPanel.addElement(element);
        }
      }
      if(drawingPanel!=null) {
        drawingPanel.repaint();
      }
    } catch(UnsupportedFlavorException ex) {}
    catch(IOException ex) {}
    catch(HeadlessException ex) {}
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
   */
  public void replaceAction() {
    drawingPanel.removeAllElements();
    pasteAction();
  }

  /**
   * Copies objects found in the specified xml control.
   */
  protected void copyAction() {
    XMLControlElement control = new XMLControlElement(DrawingFrame3D.this);
    control.saveObject(null);
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
    fileMenu = new JMenu("File");
    JMenuItem printItem = new JMenuItem("Print...");
    printItem.setAccelerator(KeyStroke.getKeyStroke('P', MENU_SHORTCUT_KEY_MASK));
    printItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable((Printable) drawingPanel);
        if(printerJob.printDialog()) {
          try {
            printerJob.print();
          } catch(PrinterException pe) {
            JOptionPane.showMessageDialog(DrawingFrame3D.this, "A printing error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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
    JMenuItem saveAsPSItem = new JMenuItem("Save As PS...");
    saveAsPSItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // GUIUtils.saveImage(drawingPanel.getComponent(), "eps", DrawingFrame3D.this);
        GUIUtils.saveImage((JPanel) drawingPanel.getComponent(), "eps", DrawingFrame3D.this);
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
    fileMenu.add(saveAsPSItem);
    fileMenu.add(inspectItem);
    menuBar.add(fileMenu);
    editMenu = new JMenu("Edit");
    menuBar.add(editMenu);
    copyItem = new JMenuItem("Copy");
    copyItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copyAction();
      }
    });
    editMenu.add(copyItem);
    pasteItem = new JMenuItem("Paste");
    pasteItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pasteAction();
      }
    });
    pasteItem.setEnabled(false); // not supported yet
    editMenu.add(pasteItem);
    replaceItem = new JMenuItem("Replace");
    replaceItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        replaceAction();
      }
    });
    replaceItem.setEnabled(false); // not supported yet
    editMenu.add(replaceItem);
    setJMenuBar(menuBar);
    cameraItem = new JMenuItem("Camera Inspector");
    cameraItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          if(cameraInspectorFrame==null) {
            cameraInspectorFrame = CameraInspector.createFrame(drawingPanel);
          }
          cameraInspectorFrame.setVisible(true);
        }
      }
    });
    /*
        displayMenu = new JMenu("Projection mode");
        displayPerspectiveItem = new JMenuItem("3D Perspective");
        displayPerspectiveItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
              drawingPanel.getCamera().setProjectionMode(Camera.MODE_PERSPECTIVE);
              drawingPanel.repaint();
            }
          }
        });
        displayMenu.add(displayPerspectiveItem);
        displayNoPerspectiveItem = new JMenuItem("3D No perspective");
        displayNoPerspectiveItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
              drawingPanel.getCamera().setProjectionMode(Camera.MODE_NO_PERSPECTIVE);
              drawingPanel.repaint();
            }
          }
        });
        displayMenu.add(displayNoPerspectiveItem);
        displayXYItem = new JMenuItem("Planar XY");
        displayXYItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
              drawingPanel.getCamera().setProjectionMode(Camera.MODE_PLANAR_XY);
              drawingPanel.repaint();
            }
          }
        });
        displayMenu.add(displayXYItem);
        displayXZItem = new JMenuItem("Planar XZ");
        displayXZItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
              drawingPanel.getCamera().setProjectionMode(Camera.MODE_PLANAR_XZ);
              drawingPanel.repaint();
            }
          }
        });
        displayMenu.add(displayXZItem);
        displayYZItem = new JMenuItem("Planar YZ");
        displayYZItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
              drawingPanel.getCamera().setProjectionMode(Camera.MODE_PLANAR_YZ);
              drawingPanel.repaint();
            }
          }
        });
        displayMenu.add(displayYZItem);
    */
    decorationMenu = new JMenu("Decoration");
    decorationNoneItem = new JMenuItem("No decoration");
    decorationNoneItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.getVisualizationHints().setDecorationType(VisualizationHints.DECORATION_NONE);
          drawingPanel.repaint();
        }
      }
    });
    decorationMenu.add(decorationNoneItem);
    decorationCubeItem = new JMenuItem("Cube");
    decorationCubeItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.getVisualizationHints().setDecorationType(VisualizationHints.DECORATION_CUBE);
          drawingPanel.repaint();
        }
      }
    });
    decorationMenu.add(decorationCubeItem);
    decorationAxesItem = new JMenuItem("Axes");
    decorationAxesItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.getVisualizationHints().setDecorationType(VisualizationHints.DECORATION_AXES);
          drawingPanel.repaint();
        }
      }
    });
    decorationMenu.add(decorationAxesItem);
    cursorMenu = new JMenu("Cursor");
    cursorNoneItem = new JMenuItem("No cursor");
    cursorNoneItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.getVisualizationHints().setCursorType(VisualizationHints.CURSOR_NONE);
          drawingPanel.repaint();
        }
      }
    });
    cursorMenu.add(cursorNoneItem);
    cursorCubeItem = new JMenuItem("Cube");
    cursorCubeItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.getVisualizationHints().setCursorType(VisualizationHints.CURSOR_CUBE);
          drawingPanel.repaint();
        }
      }
    });
    cursorMenu.add(cursorCubeItem);
    cursorXYZItem = new JMenuItem("XYZ");
    cursorXYZItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.getVisualizationHints().setCursorType(VisualizationHints.CURSOR_XYZ);
          drawingPanel.repaint();
        }
      }
    });
    cursorMenu.add(cursorXYZItem);
    cursorCrosshairItem = new JMenuItem("Crosshair");
    cursorCrosshairItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.getVisualizationHints().setCursorType(VisualizationHints.CURSOR_CROSSHAIR);
          drawingPanel.repaint();
        }
      }
    });
    cursorMenu.add(cursorCrosshairItem);
    zoomToFitItem = new JMenuItem("Zoom to fit");
    zoomToFitItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(drawingPanel!=null) {
          drawingPanel.zoomToFit();
          drawingPanel.repaint();
        }
      }
    });
    /*
        resetCameraItem = new JMenuItem("Reset Camera");
        resetCameraItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (drawingPanel != null) {
              drawingPanel.getCamera().reset();
              drawingPanel.repaint();
            }
          }
        });
    */
    visualMenu = new JMenu("Visual");
    // visualMenu.add(displayMenu);
    visualMenu.add(cameraItem);
    visualMenu.add(decorationMenu);
    visualMenu.add(cursorMenu);
    visualMenu.add(zoomToFitItem);
    // visualMenu.add(resetCameraItem);
    menuBar.add(visualMenu);
    // loadToolsMenu();
    JMenu helpMenu = new JMenu("Help");
    menuBar.add(helpMenu);
    JMenuItem aboutItem = new JMenuItem("About...");
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ControlFrame.showAboutDialog(DrawingFrame3D.this);
      }
    });
    helpMenu.add(aboutItem);
  }

  /**
   * Gets a menu with the given name from the menu bar.  Returns null if menu item does not exist.
   *
   * @param menuName String
   * @return JMenu
   */
  public JMenu getMenuItem(String menuName) {
    menuName = menuName.trim();
    JMenu menu = null;
    for(int i = 0;i<menuBar.getMenuCount();i++) {
      JMenu next = menuBar.getMenu(i);
      if(next.getText().equals(menuName)) {
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
  public JMenu removeMenuItem(String menuName) {
    menuName = menuName.trim();
    JMenu menu = null;
    for(int i = 0;i<menuBar.getMenuCount();i++) {
      JMenu next = menuBar.getMenu(i);
      if(next.getText().equals(menuName)) {
        menu = next;
        menuBar.remove(i);
        break;
      }
    }
    return menu;
  }

  /**
   * Inspects the drawing frame by using an xml document tree.
   */
  public void inspectXML() {
    XMLControlElement xml = null;
    try {
      // if drawingPanel provides an xml loader, inspect the drawingPanel
      Method method = drawingPanel.getClass().getMethod("getLoader", (java.lang.Class[]) null);
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
        Method method = drawingPanel.getClass().getMethod("getLoader", (java.lang.Class[]) null);
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

  // ----------------------------------------------------
  // XML loader
  // ----------------------------------------------------
  public static XML.ObjectLoader getLoader() {
    return new org.opensourcephysics.display3d.core.DrawingFrame3D.Loader();
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
