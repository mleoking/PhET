/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import javax.swing.text.*;
import java.awt.*;
import javax.swing.*;
import java.util.logging.*;

/**
 * A OSPFrame that displays message.
 *
 * The static MessageFrame displays logger messages when a program is run as a applet.
 *
 * @author W. Christian
 * @version 1.0
 */
public class MessageFrame extends JFrame {
  static final Color DARK_GREEN = new Color(0, 128, 0), DARK_BLUE = new Color(0, 0, 128), DARK_RED = new Color(128, 0, 0);
  static Style black, red, blue, green, magenta, gray;
  static MessageFrame APPLET_MESSAGEFRAME;
  static Level levelOSP = Level.INFO;
  private JTextPane textPane;

  private MessageFrame() {
    // create the panel, text pane and scroller
    this.setTitle("Message Log");
    JPanel logPanel = new JPanel(new BorderLayout());
    logPanel.setPreferredSize(new Dimension(480, 240));
    setContentPane(logPanel);
    textPane = new JTextPane();
    logPanel.setPreferredSize(new Dimension(200, 300));
    logPanel.setMinimumSize(new Dimension(100, 100));
    textPane.setEditable(false);
    textPane.setAutoscrolls(true);
    black = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    red = textPane.addStyle("red", black);
    StyleConstants.setForeground(red, DARK_RED);
    blue = textPane.addStyle("blue", black);
    StyleConstants.setForeground(blue, DARK_BLUE);
    green = textPane.addStyle("green", black);
    StyleConstants.setForeground(green, DARK_GREEN);
    magenta = textPane.addStyle("magenta", black);
    StyleConstants.setForeground(magenta, Color.MAGENTA);
    gray = textPane.addStyle("gray", black);
    StyleConstants.setForeground(gray, Color.GRAY);
    JScrollPane textScroller = new JScrollPane(textPane);
    textScroller.setWheelScrollingEnabled(true);
    logPanel.add(textScroller, BorderLayout.CENTER);
    pack();
  }

  public static void showLog() {
    if(APPLET_MESSAGEFRAME==null) {
      APPLET_MESSAGEFRAME = new MessageFrame();
    }
    APPLET_MESSAGEFRAME.setVisible(true);
  }

  public static boolean isLogVisible() {
    if(APPLET_MESSAGEFRAME==null) {
      return false;
    } else {
      return APPLET_MESSAGEFRAME.isVisible();
    }
  }

  /**
 * Sets the logger level;
 * @param level Level
 */
  public static void setLevel(Level level) {
    levelOSP = level;
  }

  /**
  * Logs an severe error message.
  * @param msg String
  */
  public static void severe(String msg) {
    appletLog(msg, MessageFrame.red);
  }

  /**
  * Logs a warning message.
  * @param msg String
  */
  public static void warning(String msg) {
    appletLog(msg, MessageFrame.red);
  }

  /**
  * Logs an information message.
  * @param msg String
  */
  public static void info(String msg) {
    appletLog(msg, MessageFrame.black);
  }

  /**
  * Logs a configuration message.
  * @param msg String
  */
  public static void config(String msg) {
    appletLog(msg, MessageFrame.green);
  }

  /**
* Logs a fine debugging message.
* @param msg String
*/
  public static void fine(String msg) {
    appletLog(msg, MessageFrame.blue);
  }

  /**
   * Logs a finer debugging message.
   * @param msg String
   */
  public static void finer(String msg) {
    appletLog(msg, MessageFrame.blue);
  }

  /**
   * Logs a finest debugging message.
   * @param msg String
   */
  public static void finest(String msg) {
    appletLog(msg, MessageFrame.blue);
  }

  private static void appletLog(String msg, Style style) {
    if(APPLET_MESSAGEFRAME==null) {
      APPLET_MESSAGEFRAME = new MessageFrame();
    }
    try {
      Document doc = APPLET_MESSAGEFRAME.textPane.getDocument();
      doc.insertString(doc.getLength(), msg+'\n', style);
      // scroll to display new message
      Rectangle rect = APPLET_MESSAGEFRAME.textPane.getBounds();
      rect.y = rect.height;
      APPLET_MESSAGEFRAME.textPane.scrollRectToVisible(rect);
    } catch(BadLocationException ex) {
      System.err.println(ex);
    }
  }
  /*
    public static void main(String[] args) {
      MessageFrame.fine("test fine");
      APPLET_LOG.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } */
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
