/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import org.opensourcephysics.display.*;

/**
 *  A frame with menu items for saving and loading control parameters
 *
 * @author       Wolfgang Christian
 * @version 1.0
 */
abstract public class ControlFrame extends OSPFrame implements Control {
  final static int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  protected Object model; // the object that will be controlled
  protected JMenu fileMenu;
  protected JMenu editMenu;
  protected JMenuItem readItem;
  protected JMenuItem saveAsItem;
  protected JMenuItem copyItem;
  protected JMenuItem inspectItem;
  protected JMenuItem logToFileItem;
  protected OSPApplication ospApp;
  protected XMLControlElement xmlDefault;

  protected ControlFrame(String title) {
    super(title);
    createMenuBar();
    setName("controlFrame");
  }

  private void createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    if(!OSPFrame.appletMode) {
      setJMenuBar(menuBar);
    }
    fileMenu = new JMenu("File");
    editMenu = new JMenu("Edit");
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    readItem = new JMenuItem("Load XML...");
    saveAsItem = new JMenuItem("Save XML...");
    copyItem = new JMenuItem("Copy");
    inspectItem = new JMenuItem("Inspect...");
    fileMenu.add(readItem);
    fileMenu.add(saveAsItem);
    fileMenu.add(inspectItem);
    editMenu.add(copyItem);
    copyItem.setAccelerator(KeyStroke.getKeyStroke('C', MENU_SHORTCUT_KEY_MASK));
    copyItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copy();
      }
    });
    saveAsItem.setAccelerator(KeyStroke.getKeyStroke('S', MENU_SHORTCUT_KEY_MASK));
    saveAsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // save(); // cannot use a static method here because of run-time binding
        saveXML();
      }
    });
    inspectItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        inspectXML(); // cannot use a static method here because of run-time binding
      }
    });
    readItem.setAccelerator(KeyStroke.getKeyStroke('L', MENU_SHORTCUT_KEY_MASK));
    readItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // readParameters(); // cannot use a static method here because of run-time binding
        loadXML((String) null);
      }
    });
    JMenu helpMenu = new JMenu("Help");
    menuBar.add(helpMenu);
    JMenuItem aboutItem = new JMenuItem("About...");
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutDialog(ControlFrame.this);
      }
    });
    helpMenu.add(aboutItem);
    JMenuItem showItem = new JMenuItem("Display All Frames");
    showItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        org.opensourcephysics.display.GUIUtils.showDrawingAndTableFrames();
      }
    });
    helpMenu.add(showItem);
    helpMenu.addSeparator();
    JMenuItem logItem = new JMenuItem("Message Log...");
    logItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OSPLog.getOSPLog().setVisible(true);
      }
    });
    helpMenu.add(logItem);
    logToFileItem = new JCheckBoxMenuItem("Log to file.");
    logToFileItem.setSelected(false);
    logToFileItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
        OSPLog.getOSPLog().setLogToFile(item.isSelected());
      }
    });
    helpMenu.add(logToFileItem);
    validate();
  }

  /**
 * Shows the about dialog.
 */
  public static void showAboutDialog(Component parent) {
    String aboutString = "OSP Library "+ControlUtils.version+" released "+ControlUtils.releaseDate+"\n"+"Open Source Physics Project \n"+"www.opensourcephysics.org";
    JOptionPane.showMessageDialog(parent, aboutString, "About Open Source Physics", JOptionPane.INFORMATION_MESSAGE);
  }

  /** Saves a file containing the control parameters to the disk.*/
  public void save() {
    ControlUtils.saveToFile(this, ControlFrame.this);
  }

  /** Loads a file containing the control parameters from the disk.*/
  public void readParameters() {
    ControlUtils.loadParameters((Control) this, ControlFrame.this);
  }

  /** Copies the data in the table to the system clipboard */
  public void copy() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection stringSelection = new StringSelection(this.toString());
    clipboard.setContents(stringSelection, stringSelection);
  }

  public void saveXML() {
    JFileChooser chooser = getChooser();
    if(chooser==null) {
      return;
    }
    String oldTitle = chooser.getDialogTitle();
    chooser.setDialogTitle("Save XML Data");
    int result = chooser.showSaveDialog(null);
    chooser.setDialogTitle(oldTitle);
    if(result==JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      // check to see if file already exists
      if(file.exists()) {
        int selected = JOptionPane.showConfirmDialog(null, "Replace existing "+file.getName()+"?", "Replace File", JOptionPane.YES_NO_CANCEL_OPTION);
        if(selected!=JOptionPane.YES_OPTION) {
          return;
        }
      }
      OSPFrame.chooserDir = chooser.getCurrentDirectory().toString();
      String fileName = file.getAbsolutePath();
      // String fileName = XML.getRelativePath(file.getAbsolutePath());
      if(fileName==null||fileName.trim().equals("")) {
        return;
      }
      int i = fileName.toLowerCase().lastIndexOf(".xml");
      if(i!=fileName.length()-4) {
        fileName += ".xml";
      }
      XMLControl xml = new XMLControlElement(getOSPApp());
      xml.write(fileName);
    }
  }

  public void loadXML(String[] args) {
    if(args!=null) {
      for(int i = 0;i<args.length;i++) {
        loadXML(args[i]);
      }
    }
  }

  public void loadXML(String fileName) {
    if(fileName==null||fileName.trim().equals("")) {
      loadXML();
      return;
    }
    XMLControlElement xml = new XMLControlElement(fileName);
    // if xml object class is an OSPApplication, load app
    if(OSPApplication.class.isAssignableFrom(xml.getObjectClass())) {
      xmlDefault = xml;
      xmlDefault.loadObject(getOSPApp());
    } else {
      JOptionPane.showMessageDialog(this, "\""+fileName+"\" is for "+xml.getObjectClass()+".", "Incorrect XML Object Type", JOptionPane.WARNING_MESSAGE);
    }
  }

  public void loadXML() {
    JFileChooser chooser = getChooser();
    if(chooser==null) {
      return;
    }
    String oldTitle = chooser.getDialogTitle();
    chooser.setDialogTitle("Load XML Data");
    int result = chooser.showOpenDialog(null);
    chooser.setDialogTitle(oldTitle);
    if(result==JFileChooser.APPROVE_OPTION) {
      OSPFrame.chooserDir = chooser.getCurrentDirectory().toString();
      String fileName = chooser.getSelectedFile().getAbsolutePath();
      loadXML(fileName);
      // loadXML(XML.getRelativePath(fileName));
    }
  }

  public void inspectXML() {
    // display a TreePanel in a modal dialog
    XMLControl xml = new XMLControlElement(getOSPApp());
    XMLTreePanel treePanel = new XMLTreePanel(xml);
    JDialog dialog = new JDialog((java.awt.Frame) null, true);
    dialog.setContentPane(treePanel);
    dialog.setSize(new Dimension(600, 300));
    dialog.setVisible(true);
  }

  protected OSPApplication getOSPApp() {
    if(ospApp==null) {
      ospApp = new OSPApplication(this, model);
    }
    return ospApp;
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
