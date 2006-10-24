/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *  DataTable displays multiple TableModels in a table. The first TableModel
 *  usually contains the independent variable for the other TableModel so that
 *  the visibility of column[0] can be set to false for subsequent TableModels.
 *
 * @author     Joshua Gould
 * @author     Wolfgang Christian
 * @created    February 21, 2002
 * @version    1.0
 */
public class DataTable extends JTable implements ActionListener {
  static final Color PANEL_BACKGROUND = javax.swing.UIManager.getColor("Panel.background");
  final static Color LIGHT_BLUE = new Color(204, 204, 255);
  final SortDecorator decorator;
  Map renderersByColumnName = new HashMap();
  DataTableModel dataTableModel;
  RowNumberRenderer rowNumberRenderer;
  int refreshDelay = 0;                                                       // time in ms to delay refresh events
  javax.swing.Timer refreshTimer = new javax.swing.Timer(refreshDelay, this); // delay for refreshTable

  /**
   *  Constructs a DatTable with a default data model
   */
  public DataTable() {
    this(new DefaultDataTableModel());
  }

  /**
   *  Constructs a DatTable with the specified data model
   *
   * @param  model  data model
   */
  public DataTable(DataTableModel model) {
    super();
    refreshTimer.setRepeats(false);
    refreshTimer.setCoalesce(true);
    setModel(model);
    setColumnSelectionAllowed(true);
    setGridColor(Color.blue);
    setSelectionBackground(LIGHT_BLUE);
    JTableHeader header = getTableHeader();
    header.setForeground(Color.blue);  // set text color
    setSelectionForeground(Color.red); // foreground color for selected cells
    setColumnModel(new DataTableColumnModel());
    setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    setColumnSelectionAllowed(true);
    // add column sorting using a SortDecorator
    decorator = new SortDecorator(getModel());
    setModel(decorator);
    JTableHeader hdr = (JTableHeader) getTableHeader();
    hdr.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        TableColumnModel tcm = getColumnModel();
        int vc = tcm.getColumnIndexAtX(e.getX());
        int mc = convertColumnIndexToModel(vc);
        decorator.sort(mc);
      }
    });
  }

  /**
   *  Sets the maximum number of fraction digits to display in all columns with
   *  column name columnName
   *
   * @param  maximumFractionDigits  - maximum number of fraction digits to display
   * @param  columnName             The new maximumFractionDigits value
   */
  public void setMaximumFractionDigits(String columnName, int maximumFractionDigits) {
    // renderersByColumnName.put(columnName, new Integer(maximumFractionDigits));
    renderersByColumnName.put(columnName, new PrecisionRenderer(maximumFractionDigits));
  }

  /**
   * Sorts  the table using the given column.
   * @param col int
   */
  public void sort(int col) {
    decorator.sort(col);
  }

  /**
   *  Sets the maximum number of fraction digits to display for cells that have
   *  type Double
   *
   * @param  maximumFractionDigits  - maximum number of fraction digits to display
   */
  public void setMaximumFractionDigits(int maximumFractionDigits) {
    setDefaultRenderer(Double.class, new PrecisionRenderer(maximumFractionDigits));
  }

  /**
   *  Sets the display row number flag. Table displays row number.
   *
   * @param  _rowNumberVisible  <code>true<\code> if table display row number
   */
  public void setRowNumberVisible(boolean _rowNumberVisible) {
    if(dataTableModel.isRowNumberVisible()!=_rowNumberVisible) {
      if(_rowNumberVisible&&(rowNumberRenderer==null)) {
        rowNumberRenderer = new RowNumberRenderer(this);
      }
      dataTableModel.setRowNumberVisible(_rowNumberVisible);
    }
  }

  /**
   *  Sets the model for this data table;
   *
   * @param  _model
   */
  public void setModel(DataTableModel _model) {
    super.setModel(_model);
    dataTableModel = _model;
  }

  /**
   *  Sets the stride of a TableModel in the DataTable.
   *
   * @param  tableModel
   * @param  stride
   */
  public void setStride(TableModel tableModel, int stride) {
    dataTableModel.setStride(tableModel, stride);
  }

  /**
   *  Sets the visibility of a column of a TableModel in the DataTable.
   *
   * @param  tableModel
   * @param  columnIndex
   * @param  b
   */
  public void setColumnVisible(TableModel tableModel, int columnIndex, boolean b) {
    dataTableModel.setColumnVisible(tableModel, columnIndex, b);
  }

  /**
   *  Gets the display row number flag.
   *
   * @return    The rowNumberVisible value
   */
  public boolean isRowNumberVisible() {
    return dataTableModel.isRowNumberVisible();
  }

  /**
   *  Returns an appropriate renderer for the cell specified by this row and
   *  column. If the <code>TableColumn</code> for this column has a non-null
   *  renderer, returns that. If the <code>TableColumn</code> for this column has
   *  the same name as a name specified in the setMaximumFractionDigits method,
   *  returns the appropriate renderer. If not, finds the class of the data in
   *  this column (using <code>getColumnClass</code>) and returns the default
   *  renderer for this type of data.
   *
   * @param  row     Description of Parameter
   * @param  column  Description of Parameter
   * @return         The cellRenderer value
   */
  public TableCellRenderer getCellRenderer(int row, int column) {
    int i = convertColumnIndexToModel(column);
    if((i==0)&&dataTableModel.isRowNumberVisible()) {
      return rowNumberRenderer;
    }
    try {
      TableColumn tableColumn = getColumnModel().getColumn(column);
      TableCellRenderer renderer = tableColumn.getCellRenderer();
      if(renderer!=null) {
        return renderer;
      }
      Iterator keys = renderersByColumnName.keySet().iterator();
      while(keys.hasNext()) {
        String columnName = (String) keys.next();
        if(tableColumn.getHeaderValue().equals(columnName)) {
          return(TableCellRenderer) renderersByColumnName.get(columnName);
        }
      }
    } catch(Exception ex) {}
    return getDefaultRenderer(getColumnClass(column));
  }

  /**
   *  Sets the delay time for table refresh timer.
   *
   * @param  delay  the delay in millisecond
   */
  public void setRefreshDelay(int delay) {
    if(delay>0) {
      refreshTimer.setDelay(delay);
    } else if(delay<=0) {
      refreshTimer.stop();
    }
    refreshDelay = delay;
  }

  /**
   *  Refresh the data in the DataTable, as well as other changes to the table,
   *  such as row number visibility. Changes to the TableModels displayed in the
   *  table will not be visible until this method is called.
   */
  public void refreshTable() {
    if(refreshDelay>0) {
      refreshTimer.start();
    } else {
      Runnable doRefreshTable = new Runnable() {
        public synchronized void run() {
          tableChanged(new TableModelEvent(dataTableModel, TableModelEvent.HEADER_ROW));
        }
      };
      if(SwingUtilities.isEventDispatchThread()) {
        doRefreshTable.run();
      } else {
        SwingUtilities.invokeLater(doRefreshTable);
      }
    }
  }

  /**
   *  Performs the action for the refresh timer by refreshing the data in the DataTable.
   *
   * @param  evt
   */
  public void actionPerformed(ActionEvent evt) {
    tableChanged(new TableModelEvent(dataTableModel, TableModelEvent.HEADER_ROW));
  }

  /**
   *  Add a TableModel object to the table model list.
   *
   * @param  tableModel
   */
  public void add(TableModel tableModel) {
    dataTableModel.add(tableModel);
  }

  /**
   *  Remove a TableModel object from the table model list.
   *
   * @param  tableModel
   */
  public void remove(TableModel tableModel) {
    dataTableModel.remove(tableModel);
  }

  /**
   *  Remove all TableModels from the table model list.
   */
  public void clear() {
    dataTableModel.clear();
  }

  private static class DataTableElement {
    TableModel tableModel;
    boolean columnVisibilities[]; // boolean values indicating if a column is visible
    int stride = 1; // data stride in the DataTable view

    /**
     *  Constructor DataTableElement
     *
     * @param  t
     */
    public DataTableElement(TableModel t) {
      tableModel = t;
    }

    /**
     *  Method setStride
     *
     * @param  _stride
     */
    public void setStride(int _stride) {
      stride = _stride;
    }

    /**
     *  Method setColumnVisible
     *
     * @param  columnIndex
     * @param  visible
     */
    public void setColumnVisible(int columnIndex, boolean visible) {
      ensureCapacity(columnIndex+1);
      columnVisibilities[columnIndex] = visible;
    }

    /**
     *  Method getStride
     *
     * @return
     */
    public int getStride() {
      return stride;
    }

    /**
     *  Method getColumnVisibilities
     *
     * @return
     */
    public boolean[] getColumnVisibilities() {
      return columnVisibilities;
    }

    /**
     *  Method getColumnCount
     *
     * @return
     */
    public int getColumnCount() {
      int count = 0;
      int numberOfColumns = tableModel.getColumnCount();
      ensureCapacity(numberOfColumns);
      for(int i = 0;i<numberOfColumns;i++) {
        boolean visible = columnVisibilities[i];
        if(visible) {
          count++;
        }
      }
      return count;
    }

    /**
     *  Method getValueAt
     *
     * @param  rowIndex
     * @param  columnIndex
     * @return
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      return tableModel.getValueAt(rowIndex, columnIndex);
    }

    /**
     *  Method getColumnName
     *
     * @param  columnIndex
     * @return
     */
    public String getColumnName(int columnIndex) {
      return tableModel.getColumnName(columnIndex);
    }

    /**
     *  Method getColumnClass
     *
     * @param  columnIndex
     * @return
     */
    public Class getColumnClass(int columnIndex) {
      return tableModel.getColumnClass(columnIndex);
    }

    /**
     *  Method getRowCount
     *
     * @return
     */
    public int getRowCount() {
      return tableModel.getRowCount();
    }

    private void ensureCapacity(int minimumCapacity) {
      if(columnVisibilities==null) {
        columnVisibilities = new boolean[(minimumCapacity*3)/2+1];
        Arrays.fill(columnVisibilities, true);
      } else if(columnVisibilities.length<minimumCapacity) {
        boolean[] temp = columnVisibilities;
        columnVisibilities = new boolean[(minimumCapacity*3)/2+1];
        System.arraycopy(temp, 0, columnVisibilities, 0, temp.length);
        Arrays.fill(columnVisibilities, temp.length, columnVisibilities.length, true);
      }
    }
  }

  /*
   *  DefaultDataTableModel acts on behalf of the TableModels that the DataTable contains. It combines
   *  data from these multiple sources and allows the DataTable to display data
   *  is if the data were from a single source.
   *
   *  @author     jgould
   *  @created    February 21, 2002
   */
  private static class DefaultDataTableModel implements DataTableModel {
    ArrayList dataTableElements = new ArrayList();
    boolean rowNumberVisible = false;

    /**
     *  Method setColumnVisible
     *
     * @param  tableModel
     * @param  columnIndex
     * @param  b
     */
    public void setColumnVisible(TableModel tableModel, int columnIndex, boolean b) {
      DataTableElement dte = findElementContaining(tableModel);
      dte.setColumnVisible(columnIndex, b);
    }

    /**
     *  Method setStride
     *
     * @param  tableModel
     * @param  stride
     */
    public void setStride(TableModel tableModel, int stride) {
      DataTableElement dte = findElementContaining(tableModel);
      dte.setStride(stride);
    }

    /**
     *  Method setRowNumberVisible
     *
     * @param  _rowNumberVisible
     */
    public void setRowNumberVisible(boolean _rowNumberVisible) {
      rowNumberVisible = _rowNumberVisible;
    }

    /**
     *  Method setValueAt
     *
     * @param  value
     * @param  rowIndex
     * @param  columnIndex
     */
    public void setValueAt(Object value, int rowIndex, int columnIndex) {}

    /**
     *  Method isRowNumberVisible
     *
     * @return
     */
    public boolean isRowNumberVisible() {
      return rowNumberVisible;
    }

    /**
     *  Method getColumnName
     *
     * @param  columnIndex
     * @return
     */
    public String getColumnName(int columnIndex) {
      if((dataTableElements.size()==0)&&!rowNumberVisible) {
        return null;
      }
      if(rowNumberVisible) {
        if(columnIndex==0) {
          return "row";
        }
      }
      ModelFilterResult mfr = ModelFilterResult.find(rowNumberVisible, dataTableElements, columnIndex);
      DataTableElement dte = mfr.tableElement;
      return dte.getColumnName(mfr.column);
    }

    /**
     *  Method getRowCount
     *
     * @return
     */
    public int getRowCount() {
      int rowCount = 0;
      for(int i = 0;i<dataTableElements.size();i++) {
        DataTableElement dte = (DataTableElement) dataTableElements.get(i);
        int stride = dte.getStride();
        rowCount = Math.max(rowCount, (dte.getRowCount()+stride-1)/stride);
      }
      return rowCount;
    }

    /**
     *  Method getColumnCount
     *
     * @return
     */
    public int getColumnCount() {
      int columnCount = 0;
      for(int i = 0;i<dataTableElements.size();i++) {
        DataTableElement dte = (DataTableElement) dataTableElements.get(i);
        columnCount += dte.getColumnCount();
      }
      if(rowNumberVisible) {
        columnCount++;
      }
      return columnCount;
    }

    /**
     *  Method getValueAt
     *
     * @param  rowIndex
     * @param  columnIndex
     * @return
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      if(dataTableElements.size()==0) {
        return null;
      }
      if(rowNumberVisible) {
        if(columnIndex==0) {
          return new Integer(rowIndex);
        }
      }
      ModelFilterResult mfr = ModelFilterResult.find(rowNumberVisible, dataTableElements, columnIndex);
      DataTableElement dte = mfr.tableElement;
      int stride = dte.getStride();
      rowIndex = rowIndex*stride;
      if(rowIndex>=dte.getRowCount()) {
        return null;
      }
      return dte.getValueAt(rowIndex, mfr.column);
    }

    /**
     *  Method getColumnClass
     *
     * @param  columnIndex
     * @return
     */
    public Class getColumnClass(int columnIndex) {
      if(rowNumberVisible) {
        if(columnIndex==0) {
          return Integer.class;
        }
      }
      if((columnIndex==0)&&rowNumberVisible) {
        columnIndex--;
      }
      ModelFilterResult mfr = ModelFilterResult.find(rowNumberVisible, dataTableElements, columnIndex);
      DataTableElement dte = mfr.tableElement;
      return dte.getColumnClass(mfr.column);
    }

    /**
     *  Method isCellEditable
     *
     * @param  rowIndex
     * @param  columnIndex
     * @return
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    /**
     *  Method remove
     *
     * @param  tableModel
     */
    public void remove(TableModel tableModel) {
      DataTableElement dte = findElementContaining(tableModel);
      dataTableElements.remove(dte);
    }

    /**
     *  Method clear
     */
    public void clear() {
      dataTableElements.clear();
    }

    /**
     *  Method add
     *
     * @param  tableModel
     */
    public void add(TableModel tableModel) {
      dataTableElements.add(new DataTableElement(tableModel));
    }

    /**
     *  Method addTableModelListener
     *
     * @param  l
     */
    public void addTableModelListener(TableModelListener l) {}

    /**
     *  Method removeTableModelListener
     *
     * @param  l
     */
    public void removeTableModelListener(TableModelListener l) {}

    /**
     *  returns the DataTableElement that contains the specified TableModel
     *
     * @param  tableModel
     * @return             Description of the Returned Value
     */
    private DataTableElement findElementContaining(TableModel tableModel) {
      for(int i = 0;i<dataTableElements.size();i++) {
        DataTableElement dte = (DataTableElement) dataTableElements.get(i);
        if(dte.tableModel==tableModel) {
          return dte;
        }
      }
      return null;
    }
  }

  private static class ModelFilterResult {
    DataTableElement tableElement;
    int column;

    /**
     *  Constructor ModelFilterResult
     *
     * @param  _tableElement
     * @param  _column
     */
    public ModelFilterResult(DataTableElement _tableElement, int _column) {
      tableElement = _tableElement;
      column = _column;
    }

    /**
     *  Method find
     *
     * @param  rowNumberVisible
     * @param  dataTableElements
     * @param  tableColumnIndex
     * @return
     */
    public static ModelFilterResult find(boolean rowNumberVisible, ArrayList dataTableElements, int tableColumnIndex) {
      if(rowNumberVisible) {
        tableColumnIndex--;
      }
      int totalColumns = 0;
      for(int i = 0;i<dataTableElements.size();i++) {
        DataTableElement dte = (DataTableElement) dataTableElements.get(i);
        int columnCount = dte.getColumnCount();
        totalColumns += columnCount;
        if(totalColumns>tableColumnIndex) {
          // int columnIndex = Math.abs(totalColumns - columnCount - tableColumnIndex);
          int columnIndex = (columnCount+tableColumnIndex)-totalColumns;
          boolean visible[] = dte.getColumnVisibilities();
          for(int j = 0;j<tableColumnIndex;j++) {
            if(!visible[j]) {
              columnIndex++;
            }
          }
          return new ModelFilterResult(dte, columnIndex);
        }
      }
      return null; // this shouldn't happen
    }
  }

  private class DataTableColumnModel extends DefaultTableColumnModel {

    /**
     *  Method getColumn
     *
     * @param  columnIndex
     * @return
     */
    public TableColumn getColumn(int columnIndex) {
      TableColumn tableColumn;
      try {
        tableColumn = super.getColumn(columnIndex);
      } catch(Exception ex) { // return an empty column if the columnIndex is not valid.
        return new TableColumn();
      }
      String headerValue = (String) tableColumn.getHeaderValue();
      if(headerValue==null) {
        return tableColumn;
      } else if(headerValue.equals("row")) {
        tableColumn.setMaxWidth(40);
        tableColumn.setMinWidth(40);
        tableColumn.setResizable(false);
      }
      return tableColumn;
    }
  }

  private static class PrecisionRenderer extends DefaultTableCellRenderer {
    NumberFormat numberFormat;

    /**
     *  PrecisionRenderer constructor
     *
     * @param  precision  - maximum number of fraction digits to display
     */
    public PrecisionRenderer(int precision) {
      super();
      numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(precision);
      setHorizontalAlignment(JLabel.RIGHT);
    }

    /**
     *  Sets the string for the cell being rendered to value.
     *
     * @param  value  - the string value for this cell; if value is null it sets
     *      the text value to an empty string
     */
    public void setValue(Object value) {
      setText((value==null) ? "" : numberFormat.format(value));
    }

    /**
     *  Sets the maximum number of fraction digits to display
     *
     * @param  precision  - maximum number of fraction digits to display
     */
    public void setPrecision(int precision) {
      numberFormat.setMaximumFractionDigits(precision);
    }
  }

  private static class RowNumberRenderer extends JLabel implements TableCellRenderer {
    JTable table;

    /**
     *  RowNumberRenderer constructor
     *
     * @param  _table  Description of Parameter
     */
    public RowNumberRenderer(JTable _table) {
      super();
      table = _table;
      setHorizontalAlignment(SwingConstants.RIGHT);
      setOpaque(true); // make background visible.
      setForeground(Color.black);
      setBackground(PANEL_BACKGROUND);
    }

    /**
     *  returns a JLabel that is highlighted if the row is selected.
     *
     * @param  table
     * @param  value
     * @param  isSelected
     * @param  hasFocus
     * @param  row
     * @param  column
     * @return
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if(table.isRowSelected(row)) {
        int[] i = table.getSelectedColumns();
        if((i.length==1)&&(table.convertColumnIndexToModel(i[0])==0)) {
          setBackground(PANEL_BACKGROUND);
        } else {
          setBackground(Color.gray);
        }
      } else {
        setBackground(PANEL_BACKGROUND);
      }
      setText(value.toString());
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
