/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.opensourcephysics.display.OSPFrame;

public class ControlUtils {
  static String version = "1.0f";
  static String releaseDate = "November 1, 2005";
  static DecimalFormat format2 = new DecimalFormat("#0.00");
  static DecimalFormat format3 = new DecimalFormat("#0.000");
  static DecimalFormat format4 = new DecimalFormat("#0.0000");
  // public static String chooserDir = "." + File.separatorChar;  moved to OSPFrame

  /**
   * Convert a double to a string, printing two decimal places.
   * @param d  Input double
   */
  static public String f2(double d) {
    return format2.format(d);
  }

  /**
   * Convert a double to a string, printing two decimal places.
   * @param d  Input double
   */
  static public String f3(double d) {
    return format3.format(d);
  }

  /**
   * Convert a double to a string, printing two decimal places.
   * @param d  Input double
   */
  static public String f4(double d) {
    return format4.format(d);
  }

  protected static JFileChooser chooser;

  private ControlUtils() {} // prohibits instantiation

  /**
   * Loads control parameters from a text file using a dialog box.
   */
  public static void loadParameters(Control control, Component parent) {
    JFileChooser chooser = new JFileChooser(new File(OSPFrame.chooserDir));
    int result = chooser.showOpenDialog(parent);
    if(result==JFileChooser.APPROVE_OPTION) {
      try {
        BufferedReader inFile = new BufferedReader(new FileReader(chooser.getSelectedFile()));
        readFile(control, inFile);
        inFile.close();
      } catch(Exception ex) {
        System.err.println(ex.getMessage());
      }
    }
  }

  /**
   *   Pops up a "Save File" file chooser dialog and takes user through process of saving and object to a file.
   *
   *   @param    object the object that will be converted to a string and saved
   *   @param    parent  the parent component of the dialog, can be <code>null</code>;
   *             see <code>showDialog</code> in class JFileChooser for details
   */
  public static void saveToFile(Object object, Component parent) {
    JFileChooser fileChooser = new JFileChooser(new File(OSPFrame.chooserDir));
    int result = fileChooser.showSaveDialog(parent);
    if(result!=JFileChooser.APPROVE_OPTION) {
      return;
    }
    File file = fileChooser.getSelectedFile();
    if(file.exists()) {
      int selected = JOptionPane.showConfirmDialog(parent, "A file named "+file.getName()+" already exists. Are you sure you want to replace it?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
      if(selected!=JOptionPane.YES_OPTION) {
        return;
      }
    }
    try {
      FileWriter fw = new FileWriter(file);
      PrintWriter pw = pw = new PrintWriter(fw);
      pw.print(object.toString());
      pw.close();
    } catch(IOException e) {
      JOptionPane.showMessageDialog(parent, "An error occurred while saving your file. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void saveXML(Object obj) {
    int result = getXMLFileChooser().showSaveDialog(null);
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
      if(fileName==null||fileName.trim().equals("")) {
        return;
      }
      int i = fileName.toLowerCase().lastIndexOf(".xml");
      if(i!=fileName.length()-4) {
        fileName += ".xml";
      }
      XMLControl xml = new XMLControlElement(obj);
      xml.write(fileName);
    }
  }

  /**
   * Gets a file chooser.
   *
   * @return the chooser
   */
  public static JFileChooser getXMLFileChooser() {
    if(chooser!=null) {
      return chooser;
    }
    chooser = new JFileChooser(new File(OSPFrame.chooserDir));
    javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
      // accept all directories and *.xml or *.osp files.
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
        if(i>0&&i<name.length()-1) {
          extension = name.substring(i+1).toLowerCase();
        }
        if(extension!=null&&(extension.equals("xml")||extension.equals("osp"))) {
          return true;
        }
        return false;
      }
      // the description of this filter
      public String getDescription() {
        return "XML files";
      }
    };
    chooser.addChoosableFileFilter(filter);
    return chooser;
  }

  private static void readFile(Control control, BufferedReader inFile) throws IOException {
    String nextLine = inFile.readLine();
    while(nextLine!=null) {
      String par = parseParameter(nextLine);
      if(par!=null&&!par.equals("")) {
        control.setValue(par, parseValue(nextLine));
      }
      nextLine = inFile.readLine();
    }
  }

  private static String parseParameter(String aLine) {
    int index = aLine.indexOf('='); // find index of =
    if(index<1) {
      return null;
    }
    return aLine.substring(0, index).trim(); // get the value after the =
  }

  private static String parseValue(String aLine) {
    int index = aLine.indexOf('='); // find index of =
    if(index>=aLine.length()-1) {
      return "";
    }
    return aLine.substring(index+1).trim(); // get the value after the =
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
