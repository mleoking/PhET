/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.beans.*;
import java.awt.*;
import javax.swing.*;

/**
 * A dialog that displays an editable table of XMLControl properties.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class XMLTableInspector extends JDialog implements PropertyChangeListener {
  // static fields
  final static String FRAME_TITLE = "Properties of";
  // instance fields

  private XMLTable table;

  /**
   * Constructs editable modal inspector for specified XMLControl.
   *
   * @param control the xml control
   */
  public XMLTableInspector(XMLControl control) {
    this(control, true, true);
  }

  /**
   * Constructs modal inspector for specified XMLControl and sets editable flag.
   *
   * @param control the xml control
   * @param editable true to enable editing
   */
  public XMLTableInspector(XMLControl control, boolean editable) {
    this(control, editable, true);
  }

  /**
   * Constructs inspector for specified XMLControl and sets editable and modal flags.
   *
   * @param control the xml control
   * @param editable true to enable editing
   * @param modal true if modal
   */
  public XMLTableInspector(XMLControl control, boolean editable, boolean modal) {
    super((Frame) null, modal);
    table = new XMLTable(control);
    table.setEditable(editable);
    table.addPropertyChangeListener("cell", this);
    createGUI();
    String s = XML.getExtension(control.getObjectClassName());
    setTitle(FRAME_TITLE+" "+s+" \""+control.getPropertyName()+"\" ");
  }

  /**
   * Listens for property change events from XMLTable.
   *
   * @param e the property change event
   */
  public void propertyChange(PropertyChangeEvent e) {
    // forward event to listeners
    firePropertyChange(e.getPropertyName(), e.getOldValue(), e.getNewValue());
  }

  /**
   * Gets the XMLTable.
   *
   * @return the table
   */
  public XMLTable getTable() {
    return table;
  }

  // creates the GUI
  private void createGUI() {
    setSize(400, 300);
    setContentPane(new JPanel(new BorderLayout()));
    JScrollPane scrollpane = new JScrollPane(table);
    scrollpane.createHorizontalScrollBar();
    getContentPane().add(scrollpane, BorderLayout.CENTER);
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
