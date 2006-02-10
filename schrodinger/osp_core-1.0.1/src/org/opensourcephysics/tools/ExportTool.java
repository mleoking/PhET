/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.io.*;
import java.rmi.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display2d.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.filechooser.FileFilter;

// modified by W. Christian Jan 28, 2005

/**
 * An export tool which launches a Save dialog
 *
 * @author Kipton Barros
 * @version 1.0
 */
public class ExportTool implements Tool, PropertyChangeListener {
  /*
  * TOOL is the single instance of ExportTool registered with the OSP Toolbox.
  */
  static ExportTool TOOL;
  static JFileChooser fc;
  static String exportExtension = "txt";
  static Hashtable formats;
  JCheckBox[] checkBoxes;
  String exportName = "default";

  /**
   * Creates a new export tool.  Doesn't get activated until setXML() is called.
   */
  public ExportTool() {
    fc.addPropertyChangeListener(this);
  }

  static {
    formats = new Hashtable();
    registerFormat(new ExportGnuplotFormat());
    registerFormat(new ExportXMLFormat());
    // Set the "filesOfTypeLabelText" to "File Format:"
    Object oldFilesOfTypeLabelText = UIManager.put("FileChooser.filesOfTypeLabelText", "File Format:");
    // Create a new FileChooser
    fc = new JFileChooser(OSPFrame.chooserDir);
    // Reset the "filesOfTypeLabelText" to previous value
    UIManager.put("FileChooser.filesOfTypeLabelText", oldFilesOfTypeLabelText);
    fc.setDialogType(JFileChooser.SAVE_DIALOG);
    fc.setDialogTitle("Export Data");
    fc.setApproveButtonText("Export"); // Set export formats
    setChooserFormats();
  }
  public void propertyChange(PropertyChangeEvent evt) {
    FileFilter filter = fc.getFileFilter();
    if(filter==null) {
      return;
    }
    ExportFormat ef = ((ExportFormat) formats.get(filter.getDescription()));
    if(ef==null||exportExtension.equals(ef.extension())) {
      return;
    }
    exportExtension = ef.extension();
    // bug in Java?  This doesn't seem to work.
    fc.setSelectedFile(new File(exportName+'.'+exportExtension));
  }

  /*
  * Builds the accessory pane for file chooser
  */
  void buildAccessory(List data) {
    checkBoxes = new JCheckBox[data.size()];
    JPanel checkPanel = new JPanel(new GridLayout(0, 1));
    // Create the list of data objects and put it in a scroll pane.
    for(int i = 0;i<data.size();i++) {
      String s = "Unknown"+i;
      Color c = Color.BLACK;
      Object o = data.get(i);
      if(o instanceof Dataset) {
        Dataset d = (Dataset) o;
        // BUG: come up with better name
        s = "Dataset"+i;
        c = d.getFillColor();
      } else if(o instanceof GridData) {
        s = "GridData"+i;
      }
      checkBoxes[i] = new JCheckBox(s);
      checkBoxes[i].setSelected(true);
      checkBoxes[i].setForeground(c);
      checkBoxes[i].setBackground(Color.WHITE);
      checkPanel.add(checkBoxes[i]);
    }
    JScrollPane scrollPane = new JScrollPane(checkPanel);
    scrollPane.getViewport().setBackground(Color.WHITE);
    JPanel p = new JPanel(new BorderLayout());
    if(data.size()==0) {
      p.add(new JLabel("No Data"), BorderLayout.NORTH);
    } else {
      p.add(new JLabel("Exportable Data"), BorderLayout.NORTH);
    }
    p.add(scrollPane, BorderLayout.CENTER);
    p.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    fc.setAccessory(p); // always show the exportable data
  }

  /*
  * Set dialog formats in file chooser
  */
  static void setChooserFormats() {
    fc.resetChoosableFileFilters();
    fc.setAcceptAllFileFilterUsed(false);
    for(Enumeration e = formats.keys();e.hasMoreElements();) {
      final String desc = (String) e.nextElement();
      fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File f) {
          return true;
        }
        public String getDescription() {
          return desc;
        }
      });
    }
  }

  /*
  * Gets all data objects from XML
  */
  List getDataObjects(XMLControlElement control) {
    List ret = control.getObjects(Dataset.class);
    ret.addAll(control.getObjects(GridData.class));
    return ret;
  }

  /*
  * Filters out data objects to export based on the checkBoxes
  */
  List filterDataObjects(List data) {
    Vector ret = new Vector();
    for(int i = 0;i<data.size();i++) {
      if(checkBoxes[i].isSelected()) {
        ret.add(data.get(i));
      }
    }
    return ret;
  }

  /**
   * Register a new export format.
   */
  static public void registerFormat(ExportFormat format) {
    formats.put(format.description(), format);
  }

  /*
  * Displays the export dialog with a given XML file.
  */
  public void send(Job job, Tool replyTo) throws RemoteException {
    XMLControlElement control = new XMLControlElement();
    try {
      control.readXML(job.getXML());
    } catch(RemoteException ex) {}
    OSPLog.finest(control.toXML());
    // Load all data objects into 'data'
    List data = getDataObjects(control);
    // Set export dialog to list appropriate data objects
    buildAccessory(data);
    // Set selected file in home directory
    fc.setSelectedFile(new File(exportName+'.'+exportExtension));
    // Show the export dialog, and wait for user input
    int returnVal = fc.showSaveDialog(null);
    // If user clicked export, write the file
    if(returnVal==JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      // Check to see if file already exists
      if(file.exists()) {
        int selected = JOptionPane.showConfirmDialog(null, "Replace existing "+file.getName()+"?", "Replace File", JOptionPane.YES_NO_CANCEL_OPTION);
        if(selected!=JOptionPane.YES_OPTION) {
          return;
        }
      }
      String description = fc.getFileFilter().getDescription();
      ((ExportFormat) formats.get(description)).export(file, filterDataObjects(data));
      if(file.getName().endsWith(exportExtension)) {
        exportName = file.getName().substring(0, file.getName().length()-1-exportExtension.length());
        // System.out.println("new name="+exportName);
      }
    }
  }

  /**
   * Gets the shared Tool.
   *
   * @return the shared DatasetTool
   */
  public static ExportTool getTool() {
    if(TOOL==null) {
      TOOL = new ExportTool();
      Toolbox.addTool("ExportTool", TOOL);
    }
    return TOOL;
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
