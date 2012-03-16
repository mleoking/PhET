/* $RCSfile$
 * $Author jonathan gutow$
 * $Date Aug 5, 2007 9:19:06 AM $
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Jmol Development Team
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
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */
package org.openscience.jmol.app.webexport;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.*;

import org.jmol.util.TextFormat;
import org.jmol.viewer.*;

public class Test extends JPanel implements ActionListener {

  /*
   * old code -- not implemented in Jmol 11.3
   */
  
  private static final long serialVersionUID = 1L;
  // The constants used to generate panels, etc.
  JButton StateButton, FileButton, PathButton, movetoTime,
      StringtoScriptButton;
  JTextField appletPath;
  ArrayListTransferHandler arrayListHandler;
  JFileChooser fc;
  Viewer viewer;

  Test(Viewer viewer) {
    this.viewer = viewer;
  }

  // Need the panel maker and the action listener.
  public JComponent panel() {

    // Create the brief discription text
    JLabel Description = new JLabel(
        "Buttons to test getting info from Jmol Application");

    // For layout purposes, put things in separate panels

    // Create the state button.
    StateButton = new JButton("Get Application State...");
    StateButton.addActionListener(this);

    // Create Filename Button
    FileButton = new JButton("Get name of open file...");
    FileButton.addActionListener(this);

    // Create Path Button
    PathButton = new JButton("Get Path to open file...");
    PathButton.addActionListener(this);

    // Create the movetoTime Button
    movetoTime = new JButton("Insert 5 seconds for moveto, rotate and zoom...");
    movetoTime.addActionListener(this);

    // Create String to Script Button
    StringtoScriptButton = new JButton("Save a string as a script");
    StringtoScriptButton.addActionListener(this);

    // Combine Three buttons into one panel
    JPanel ButtonPanel1 = new JPanel();
    ButtonPanel1.add(StateButton);
    ButtonPanel1.add(FileButton);
    ButtonPanel1.add(PathButton);

    // Next three button in another panel
    JPanel ButtonPanel2 = new JPanel();
    ButtonPanel2.add(movetoTime);
    ButtonPanel2.add(StringtoScriptButton);

    // Create the overall panel
    JPanel TestPanel = new JPanel();
    TestPanel.setLayout(new GridLayout(10, 1));

    // Add everything to this panel.
    TestPanel.add(Description);
    TestPanel.add(ButtonPanel1);
    TestPanel.add(ButtonPanel2);

    return (TestPanel);
  }

  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == StateButton) { // Handle getting the State of Jmol...
      String Str = null;
      Str = viewer.getStateInfo();
      if (Str == null) {
        LogPanel
            .log("Something didn't work when selecting the State Button in Test module");
      }
      LogPanel.log(Str);
    }
    if (e.getSource() == FileButton) { // Handle getting the file name...
      String Str = null;
      Str = viewer.getFileName();
      if (Str == null) {
        LogPanel
            .log("Something didn't work when selecting the file button in Test module");
      } else {
        LogPanel.log(Str);
      }
    }
    if (e.getSource() == PathButton) {// Handle getting the path to the file...
      String Str = null;
      Str = viewer.getFullPathName();
      if (Str == null) {
        LogPanel
            .log("Something didn't work when selecting the Path button in Test module");
      } else {
        LogPanel.log(Str);
      }
    }
    if (e.getSource() == movetoTime) {// Handle getting the path to the file...
      String statestr = null;
      statestr = viewer.getStateInfo();
      if (statestr == null) {
        LogPanel
            .log("Something didn't work when reading the state while trying to add a moveto time.");
      }
      // Change the state string so that it will work as a script with an
      // animated moveto...
      statestr = TextFormat.simpleReplace(statestr, "set refreshing false;",
          "set refreshing true;");
      statestr = TextFormat.simpleReplace(statestr,
          "moveto /* time, axisAngle */ 0.0",
          "moveto /* time, axisAngle */ 5.0");
      LogPanel.log("The state below should have a 5 second moveto time...");
      LogPanel.log(statestr);
    }
    if (e.getSource() == StringtoScriptButton) {
      String Str = "This is a test string to stand in for the script;";
      PrintStream out = null;
      try {
        out = new PrintStream(new FileOutputStream("Test.scpt"));
      } catch (FileNotFoundException IOe) {
        LogPanel.log("Open file error in StringtoScriptButton"); // Pass the
      }
      out.print(Str);
      out.close();
      LogPanel
          .log("The file Test.scpt should have been written to the default directory.");
    }
  }
}
