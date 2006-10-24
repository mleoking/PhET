/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.opensourcephysics.tools.*;

/**
 * This is a table view of an XML control and its property contents.
 *
 * @author Douglas Brown
 */
public class XMLTable extends JTable {
  final static Color PANEL_BACKGROUND = javax.swing.UIManager.getColor("Panel.background");
  final static Color LIGHT_BLUE = new Color(204, 204, 255);
  // instance fields

  XMLTableModel tableModel;
  XMLCellRenderer xmlRenderer = new XMLCellRenderer();
  XMLValueEditor valueEditor = new XMLValueEditor();
  Color defaultBackgroundColor = Color.white;
  Map cellColors = new HashMap(); // maps property name to color

  /**
   * Constructor.
   *
   * @param control the XMLcontrol
   */
  public XMLTable(XMLControl control) {
    tableModel = new XMLTableModel(control);
    init();
  }

  /**
   * Enables/disables editing for all properties.
   *
   * @param editable true to enable editing
   */
  public void setEditable(boolean editable) {
    if(editable) {
      tableModel.uneditablePropNames.clear();
    } else {
      Iterator it = tableModel.control.getPropertyNames().iterator();
      while(it.hasNext()) {
        tableModel.uneditablePropNames.add(it.next());
      }
    }
    tableModel.editable = editable;
  }

  /**
   * Returns true if editing is enabled.
   *
   * @return true if editable
   */
  public boolean isEditable() {
    return tableModel.editable;
  }

  /**
   * Enables/disables editing for a specified property name.
   * Properties are editable by default.
   *
   * @param propName the property name
   * @param editable true to enable editing
   */
  public void setEditable(String propName, boolean editable) {
    // add to uneditablePropNames list if editable is false
    if(!editable) {
      tableModel.uneditablePropNames.add(propName);
    } else {
      tableModel.uneditablePropNames.remove(propName);
    }
  }

  /**
   * Returns true if editing is enabled for the specified property.
   *
   * @param propName the name of the property
   * @return true if editable
   */
  public boolean isEditable(String propName) {
    return !tableModel.uneditablePropNames.contains(propName);
  }

  /**
   * Determines whether the given cell is editable.
   *
   * @param row the row index
   * @param col the column index
   * @return true if editable
   */
  public boolean isCellEditable(int row, int col) {
    return tableModel.isCellEditable(row, col);
  }

  /**
   * Sets the background color of the value field for specified property name.
   * May be set to null.
   *
   * @param propName the property name
   * @param color the color
   */
  public void setBackgroundColor(String propName, Color color) {
    cellColors.put(propName, color);
  }

  /**
   * Sets the background color of the value field for specified property name.
   * May be set to null.
   *
   * @param propName the property name
   * @return the color
   */
  public Color getBackgroundColor(String propName) {
    Color color = (Color) cellColors.get(propName);
    return color==null ? defaultBackgroundColor : color;
  }

  /**
   * Returns the renderer for a cell specified by row and column.
   *
   * @param row the row number
   * @param column the column number
   * @return the cell renderer
   */
  public TableCellRenderer getCellRenderer(int row, int column) {
    return xmlRenderer;
  }

  /**
   * Returns the editor for a cell specified by row and column.
   *
   * @param row the row number
   * @param column the column number
   * @return the cell editor
   */
  public TableCellEditor getCellEditor(int row, int column) {
    return valueEditor;
  }

  /**
   * A cell renderer for an xml table.
   */
  class XMLCellRenderer extends JLabel implements TableCellRenderer {
    Color lightGreen = new Color(204, 255, 204); // for double-clickable cells
    // Color lightGray = new Color(220, 220, 220); // for names

    Color lightGray = javax.swing.UIManager.getColor("Panel.background");
    Font font = new JTextField().getFont();

    // Constructor
    public XMLCellRenderer() {
      super();
      setOpaque(true); // make background visible
      setForeground(Color.black);
      setFont(font);
    }

    // Returns a label for the specified cell.
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      setForeground(Color.BLACK);
      if(value==null) value="";  // changed by W. Christian to trap for null values
      if(column==0) {            // property name
        setBackground(lightGray);
        setHorizontalAlignment(SwingConstants.LEFT);
        setText(value.toString());
        setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 2));
      } else if(value instanceof XMLProperty) { // object, array or collection type
        XMLProperty prop = (XMLProperty) value;
        XMLProperty parent = prop.getParentProperty();
        String className = XML.getSimpleClassName(parent.getPropertyClass());
        if(parent.getPropertyType().equals("array")) {
          XMLControl control = (XMLControl) parent.getParentProperty();
          Object array = control.getObject(parent.getPropertyName());
          // determine if base component type is primitive and count array elements
          Class baseType = array.getClass().getComponentType();
          int count = Array.getLength(array);
          int insert = className.indexOf("[]")+1;
          className = className.substring(0, insert)+count+className.substring(insert);
          while(baseType.getComponentType()!=null) {
            baseType = baseType.getComponentType();
            array = Array.get(array, 0);
            if(array==null) {
              break;
            }
            count = Array.getLength(array);
            insert = className.indexOf("[]", insert)+1;
            className = className.substring(0, insert)+count+className.substring(insert);
          }
        }
        setText(className);
        // setBorder(isInspectable(parent)? BorderFactory.createRaisedBevelBorder(): null);
        // setBackground(isInspectable(parent)? defaultBackgroundColor: lightGray);
        setBackground(isInspectable(parent) ? lightGreen : lightGray);
        setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 2));
        setHorizontalAlignment(SwingConstants.CENTER);
        if(isSelected&&isInspectable(parent)) {
          setBackground(LIGHT_BLUE);
          setForeground(Color.RED);
        }
      } else { // int, double, boolean or string type
        String propName = (String) tableModel.getValueAt(row, 0);
        if(isSelected) {
          setBackground(LIGHT_BLUE);
          setForeground(Color.RED);
        } else {
          Color cellColor = (Color) cellColors.get(propName);
          setBackground(cellColor==null ? defaultBackgroundColor : cellColor);
        }
        setHorizontalAlignment(SwingConstants.LEFT);
        setText(value.toString());
        setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 2));
      }
      return this;
    }
  }

  /**
   * A cell editor for an xml table.
   */
  class XMLValueEditor extends AbstractCellEditor implements TableCellEditor {
    JPanel panel = new JPanel(new BorderLayout());
    JTextField field = new JTextField();
    Object editObject;

    // Constructor.
    XMLValueEditor() {
      panel.add(field, BorderLayout.CENTER);
      panel.setOpaque(false);
      panel.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 2));
      field.setBorder(null);
      field.setEditable(true);
      field.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          stopCellEditing();
        }
      });
      field.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          if(e.getKeyCode()==KeyEvent.VK_ENTER) {
            stopCellEditing();
          } else {
            field.setBackground(Color.yellow);
          }
        }
      });
      field.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          if(field.getBackground()!=defaultBackgroundColor) {
            stopCellEditing();
          }
          XMLTable.this.requestFocusInWindow();
        }
      });
    }

    // Gets the component to be displayed while editing.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      editObject = null;
      if(value instanceof XMLControl) {
        XMLControl childControl = (XMLControl) value;
        XMLTableInspector inspector = new XMLTableInspector(childControl, isEditable());
        // offset new inspector relative to parent container
        Container cont = XMLTable.this.getTopLevelAncestor();
        Point p = cont.getLocationOnScreen();
        inspector.setLocation(p.x+30, p.y+30);
        inspector.setVisible(true);
        return null;
      } else if(value instanceof XMLProperty) {
        XMLProperty prop = (XMLProperty) value;
        // display an array inspector if available
        XMLProperty arrayProp = prop.getParentProperty();
        ArrayInspector inspector = ArrayInspector.getInspector(arrayProp);
        if(inspector!=null) {
          String name = arrayProp.getPropertyName();
          XMLProperty parent = arrayProp.getParentProperty();
          while(!(parent instanceof XMLControl)) {
            name = parent.getPropertyName();
            arrayProp = parent;
            parent = parent.getParentProperty();
          }
          final XMLControl arrayControl = (XMLControl) parent;
          final String arrayName = name;
          final Object arrayObj = inspector.getArray();
          inspector.setEditable(tableModel.editable);
          // listen for changes in the inspector
          final int rowNumber = row;
          final int colNumber = column;
          inspector.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
              if(e.getPropertyName().equals("cell")) {
                // set new value in array control
                arrayControl.setValue(arrayName, arrayObj);
              }
              // signal listeners when inspector closes and array data is changed
              else if(e.getPropertyName().equals("arrayData")) {
                tableModel.fireTableCellUpdated(rowNumber, colNumber);
              }
            }
          });
          // offset new inspector relative to parent container
          Container cont = XMLTable.this.getTopLevelAncestor();
          Point p = cont.getLocationOnScreen();
          inspector.setLocation(p.x+30, p.y+30);
          inspector.setVisible(true);
          cont.transferFocus();
        }
        return null;
      }
      field.setText(value.toString());
      return panel;
    }

    // Determines when editing starts.
    public boolean isCellEditable(EventObject e) {
      if(e instanceof MouseEvent) {
        MouseEvent me = (MouseEvent) e;
        if(me.getClickCount()==2) {
          return true;
        }
      } else if(e instanceof ActionEvent) {
        return true;
      }
      return false;
    }

    // Called when editing is completed.
    public Object getCellEditorValue() {
      if(field.getBackground()!=defaultBackgroundColor) {
        field.setBackground(defaultBackgroundColor);
        return field.getText();
      }
      return editObject;
    }
  }

  // refreshes the table
  public void refresh() {
    Runnable runner = new Runnable() {
      public synchronized void run() {
        tableChanged(new TableModelEvent(tableModel, TableModelEvent.HEADER_ROW));
      }
    };
    if(SwingUtilities.isEventDispatchThread()) {
      runner.run();
    } else {
      SwingUtilities.invokeLater(runner);
    }
  }

  public void tableChanged(TableModelEvent e) {
    // pass the tablemodel event to property change listeners
    firePropertyChange("tableData", null, e);
    super.tableChanged(e);
  }

  // initializes the table
  private void init() {
    setModel(tableModel);
    JTableHeader header = getTableHeader();
    header.setReorderingAllowed(false);
    header.setForeground(Color.BLACK); // set header text color
    setGridColor(Color.BLACK);
    // Override the default tab behaviour
    InputMap im = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
    final Action prevTabAction = getActionMap().get(im.get(tab));
    Action tabAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        // tab to the next editable cell
        prevTabAction.actionPerformed(e);
        JTable table = (JTable) e.getSource();
        int rowCount = table.getRowCount();
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        while(!table.isCellEditable(row, column)) {
          if(column==0) {
            column = 1;
          } else {
            row += 1;
          }
          if(row==rowCount) {
            row = 0;
          }
          if(row==table.getSelectedRow()&&column==table.getSelectedColumn()) {
            break;
          }
        }
        table.changeSelection(row, column, false, false);
      }
    };
    getActionMap().put(im.get(tab), tabAction);
    // enter key starts editing
    Action enterAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        // start editing
        JTable table = (JTable) e.getSource();
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        table.editCellAt(row, column, e);
        Component comp = table.getEditorComponent();
        if(comp instanceof JPanel) {
          JPanel panel = (JPanel) comp;
          comp = panel.getComponent(0);
          if(comp instanceof JTextField) {
            JTextField field = (JTextField) comp;
            field.requestFocus();
            field.selectAll();
          }
        }
      }
    };
    KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    getActionMap().put(im.get(enter), enterAction);
  }

  // determines whether the specified property is inspectable
  private boolean isInspectable(XMLProperty prop) {
    if(prop.getPropertyType().equals("object")) {
      return true;
    }
    if(prop.getPropertyType().equals("array")) {
      return ArrayInspector.canInspect(prop);
    }
    return false;
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
