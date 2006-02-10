/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.event.TableModelListener;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JTextField;

/**
 * A JTable to display int and double array values.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class ArrayTable extends JTable {
  // instance fields
  ArrayTableModel tableModel;
  ArrayIndexRenderer indexRenderer;

  /**
   * Constructor for 1D int array.
   *
   * @param array the array
   */
  public ArrayTable(int[] array) {
    tableModel = new ArrayTableModel(array);
    init();
  }

  /**
   * Constructor for 2D int array.
   *
   * @param array the array
   */
  public ArrayTable(int[][] array) {
    tableModel = new ArrayTableModel(array);
    init();
  }

  /**
   * Constructor for 1D double array.
   *
   * @param array the array
   */
  public ArrayTable(double[] array) {
    tableModel = new ArrayTableModel(array);
    init();
  }

  /**
   * Constructor for 2D double array.
   *
   * @param array the array
   */
  public ArrayTable(double[][] array) {
    tableModel = new ArrayTableModel(array);
    init();
  }

  /**
   * Initializes the table.
   */
  protected void init() {
    setModel(tableModel);
    tableModel.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        // forward the tablemodel event to property change listeners
        ArrayTable.this.firePropertyChange("cell", null, e);
      }
    });
    setColumnSelectionAllowed(true);
    indexRenderer = new ArrayIndexRenderer();
    getTableHeader().setReorderingAllowed(false);
    getTableHeader().setDefaultRenderer(indexRenderer);
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setGridColor(Color.BLACK);
    // set width of column 0 (row index)
    String name = getColumnName(0);
    TableColumn column = getColumn(name);
    int width = 24;
    column.setMinWidth(width);
    column.setMaxWidth(2*width);
    column.setWidth(width);
    // set width of other columns
    width = 60;
    for(int i = 1, n = getColumnCount();i<n;i++) {
      name = getColumnName(i);
      column = getColumn(name);
      column.setMinWidth(width);
      column.setMaxWidth(3*width);
      column.setWidth(width);
    }
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
        if(comp instanceof JTextField) {
          JTextField field = (JTextField) comp;
          field.requestFocus();
          field.selectAll();
        }
      }
    };
    KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    getActionMap().put(im.get(enter), enterAction);
  }

  /**
   *  Refresh the data in the table.
   */
  public void refreshTable() {
    Runnable refresh = new Runnable() {
      public synchronized void run() {
        tableChanged(new TableModelEvent(tableModel, TableModelEvent.HEADER_ROW));
      }
    };
    if(SwingUtilities.isEventDispatchThread()) {
      refresh.run();
    } else {
      SwingUtilities.invokeLater(refresh);
    }
  }

  public void setEditable(boolean editable) {
    tableModel.setEditable(editable);
  }

  /**
   * Returns the renderer for a cell specified by row and column.
   *
   * @param row the row number
   * @param column the column number
   * @return the cell renderer
   */
  public TableCellRenderer getCellRenderer(int row, int column) {
    int i = convertColumnIndexToModel(column);
    if(i==0) {
      return indexRenderer;
    }
    return getDefaultRenderer(getColumnClass(column));
  }

  /**
   * A cell renderer for array indices.
   */
  static class ArrayIndexRenderer extends JLabel implements TableCellRenderer {

    /**
     * Constructor
     */
    public ArrayIndexRenderer() {
      super();
      setBorder(BorderFactory.createRaisedBevelBorder());
      setOpaque(true); // make background visible
      setForeground(Color.BLACK);
      setBackground(Color.LIGHT_GRAY);
    }

    /**
     * Returns a label for the specified cell.
     *
     * @param table ignored
     * @param value the row number to be displayed
     * @param isSelected ignored
     * @param hasFocus ignored
     * @param row ignored
     * @param column the column number
     * @return a label with the row number
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if(column==0) {
        setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
        setHorizontalAlignment(SwingConstants.CENTER);
      }
      setText(value.toString());
      setPreferredSize(new Dimension(20, 18));
      return this;
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
