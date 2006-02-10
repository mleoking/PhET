/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.ejs.control;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.OSPFrame;

/**
 * EjsControlFrame defines an Easy Java Simulations control that is guaranteed to have a
 * parent frame.
 *
 * @author       Wolfgang Christian
 * @version 1.0
 */
public class EjsControlFrame extends ParsedEjsControl implements RootPaneContainer {
  final static int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  OSPFrame frame = new OSPFrame(){
     public void render() {
        EjsControlFrame.this.render();
     }
  };
  JFrame messageFrame = new JFrame("Messages");
  TextArea messageArea = new TextArea(20, 20);
  Object model;
  JMenuBar menuBar = new JMenuBar();
  protected XMLControlElement xmlDefault;
  protected PropertyChangeSupport support = new SwingPropertyChangeSupport(this);

  /**
   * Constructor EjsControlFrame
   *
   * @param _simulation
   */
  public EjsControlFrame(Object _simulation) {
    this(_simulation, "name=controlFrame;title=Control Frame;location=400,0;layout=border;exit=false; visible=true");
  }

  /**
   * Constructor EjsControlFrame
   *
   * @param _simulation
   * @param param
   */
  public EjsControlFrame(Object _simulation, String param) {
    super(_simulation);
    model = _simulation;
    frame.setName("controlFrame");
    addObject(frame, "Frame", param);
    // frame = (JFrame) add("Frame", param).getComponent();
    frame.setJMenuBar(menuBar);
    JMenu fileMenu = new JMenu("File");
    menuBar.add(fileMenu);
    JMenuItem readItem = new JMenuItem("Read");
    JMenuItem saveAsItem = new JMenuItem("Save As...");
    JMenuItem inspectItem = new JMenuItem("Inspect");
    fileMenu.add(readItem);
    fileMenu.add(saveAsItem);
    fileMenu.add(inspectItem);
    readItem.setAccelerator(KeyStroke.getKeyStroke('R', MENU_SHORTCUT_KEY_MASK));
    readItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // readParameters(); // cannot use a static method here because of run-time binding
        loadXML((String) null);
        support.firePropertyChange("xmlDefault", null, xmlDefault);
      }
    });
    saveAsItem.setAccelerator(KeyStroke.getKeyStroke('S', MENU_SHORTCUT_KEY_MASK));
    saveAsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveXML();
      }
    });
    inspectItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        inspectXML(); // cannot use a static method here because of run-time binding
      }
    });
    JMenu helpMenu = new JMenu("Help");
    menuBar.add(helpMenu);
    JMenuItem aboutItem = new JMenuItem("About...");
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutDialog();
      }
    });
    helpMenu.add(aboutItem);
    helpMenu.addSeparator();
    JMenuItem logToFileItem = new JCheckBoxMenuItem("Log to file.");
    logToFileItem.setSelected(false);
    logToFileItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
        OSPLog.getOSPLog().setLogToFile(item.isSelected());
      }
    });
    helpMenu.add(logToFileItem);
    JMenuItem logItem = new JMenuItem("Message Log...");
    logItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        OSPLog.getOSPLog().setVisible(true);
      }
    });
    helpMenu.add(logItem);
    menuBar.add(helpMenu);
    if(org.opensourcephysics.display.OSPFrame.appletMode) {
      frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
    messageFrame.getContentPane().add(messageArea);
    messageFrame.setSize(300, 175);
  }

  /**
   * Shows the about dialog.
   */
  protected void showAboutDialog() {
    String aboutString = "OSP Control Frame 1.0  June 2004\n"+"Open Source Physics Project \n"+"www.opensourcephysics.org";
    JOptionPane.showMessageDialog(frame, aboutString, "About Open Source Physics", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
 * Adds a PropertyChangeListener.
 *
 * @param listener the object requesting property change notification
 */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  /**
   * Removes a PropertyChangeListener.
   *
   * @param listener the listener requesting removal
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }

  /**
   * Prints a string in the control's message area followed by a CR and LF.
   * GUI controls will usually display messages in a non-editable text area.
   *
   * @param s
   */
  public void println(String s) {
    messageArea.append(s+"\n");
    messageFrame.setVisible(true);
    messageFrame.setVisible(true);
  }

  /**
   * Prints a blank line in the control's message area.  GUI controls will usually display
   * messages in a non-editable text area.
   */
  public void println() {
    messageArea.append("\n");
    messageFrame.setVisible(true);
    messageFrame.setVisible(true);
  }

  /**
   * Prints a string in the control's message area.
   * GUI controls will usually display messages in a non-editable text area.
   *
   * @param s
   */
  public void print(String s) {
    messageArea.append(s);
    messageFrame.setVisible(true);
  }

  /**
   * Clears all text from the control's message area.
   */
  public void clearMessages() {
    messageArea.setText("");
  }

  /**
   * Gets the frame that contains the control.
   *
   * @return
   */
  public OSPFrame getFrame() {
    return frame;
  }

  /**
   * Renders the frame.  Subclass this method to render the contents of this frame in the calling thread.
   */
  public void render(){}


  /**
   * Gets the frame that contains the control.
   *
   * @return
   */
  public Container getTopLevelAncestor() {
    return frame;
  }

  /**
   * Gets the frame's root pane.  Implementation of RootPaneContainer.
   *
   * @return
   */
  public JRootPane getRootPane() {
    return frame.getRootPane();
  }

  /**
   * Gets the frame's content pane. Implementation of RootPaneContainer.
   *
   * @return content pane of the frame
   */
  public Container getContentPane() {
    return frame.getContentPane();
  }

  /**
   * Sets the frame's content pane. Implementation of RootPaneContainer.
   * @param contentPane
   */
  public void setContentPane(Container contentPane) {
    frame.setContentPane(contentPane);
  }

  /**
   * Implementation of RootPaneContainer.
   *
   * @see javax.swing.RootPaneContainer
   *
   * @return layeredPane of the frame
   */
  public JLayeredPane getLayeredPane() {
    return frame.getLayeredPane();
  }

  /**
   * Implementation of RootPaneContainer.
   *
   * @see javax.swing.RootPaneContainer
   * @param layeredPane
   */
  public void setLayeredPane(JLayeredPane layeredPane) {
    frame.setLayeredPane(layeredPane);
  }

  /**
   * Implementation of RootPaneContainer.
   *
   * @see javax.swing.RootPaneContainer
   *
   * @return glas pane component
   */
  public Component getGlassPane() {
    return frame.getGlassPane();
  }

  /**
   * Implementation of RootPaneContainer.
   *
   * @see javax.swing.RootPaneContainer
   * @param glassPane
   */
  public void setGlassPane(Component glassPane) {
    frame.setGlassPane(glassPane);
  }

  public void parseXMLMenu(String xmlMenu) {
    if(menuBar==null) {
      return;
    }
    XMLControl xml = new XMLControlElement(xmlMenu);
    if(xml.failedToRead()) {
      OSPLog.info("Tools menu not found: "+xmlMenu);
    } else {
      Class type = xml.getObjectClass();
      if(type!=null&&org.opensourcephysics.tools.LaunchNode.class.isAssignableFrom(type)) {
        // load the xml data into a launch node and add the menu item
        org.opensourcephysics.tools.LaunchNode node = (org.opensourcephysics.tools.LaunchNode) xml.loadObject(null);
        // get the menu name and find or create the menu
        String menuName = node.toString();
        JMenu menu = null;
        for(int i = 0;i<menuBar.getMenuCount();i++) {
          JMenu next = menuBar.getMenu(i);
          if(next.getText().equals(menuName)) {
            menu = next;
            break;
          }
        }
        if(menu==null) {
          menu = new JMenu(menuName);
          menuBar.add(menu);
          menuBar.validate();
        }
        // add the node item to the menu
        node.setLaunchObject(model);
        node.addMenuItemsTo(menu);
        OSPLog.finest("Tools menu loaded: "+xmlMenu);
      }
    }
  }

  // The following methods the an XML framework for OSPApplications.
  protected OSPApplication app;

  public void saveXML() {
    JFileChooser chooser = OSPFrame.getChooser();
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
      OSPFrame.chooserDir = chooser.getCurrentDirectory().toString();
      String fileName = XML.getRelativePath(file.getAbsolutePath());
      if(fileName==null||fileName.trim().equals("")) {
        return;
      }
      int i = fileName.toLowerCase().lastIndexOf(".xml");
      if(i!=fileName.length()-4) {
        fileName += ".xml";
      }
      XMLControl xml = new XMLControlElement(getApp());
      xml.write(fileName);
    }
  }

  private OSPApplication getApp() {
    if(app==null) {
      app = new OSPApplication(this, model);
    }
    return app;
  }

  public void loadDefaultXML() {
    if(xmlDefault!=null) {
      xmlDefault.loadObject(getApp());
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
      xml.loadObject(getApp());
    } else {
      JOptionPane.showMessageDialog(frame, "\""+fileName+"\" is for "+xml.getObjectClass()+".", "Incorrect XML Object Type", JOptionPane.WARNING_MESSAGE);
    }
  }

  public void loadXML() {
    JFileChooser chooser = OSPFrame.getChooser();
    int result = chooser.showOpenDialog(null);
    if(result==JFileChooser.APPROVE_OPTION) {
      String fileName = chooser.getSelectedFile().getAbsolutePath();
      loadXML(XML.getRelativePath(fileName));
    }
  }

  public void inspectXML() {
    // display a TreePanel in a modal dialog
    XMLControl xml = new XMLControlElement(getApp());
    XMLTreePanel treePanel = new XMLTreePanel(xml);
    JDialog dialog = new JDialog((java.awt.Frame) null, true);
    dialog.setContentPane(treePanel);
    dialog.setSize(new Dimension(600, 300));
    dialog.setVisible(true);
  }

  public void loadXML(String[] args) {
    if(args!=null) {
      for(int i = 0;i<args.length;i++) {
        loadXML(args[i]);
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
