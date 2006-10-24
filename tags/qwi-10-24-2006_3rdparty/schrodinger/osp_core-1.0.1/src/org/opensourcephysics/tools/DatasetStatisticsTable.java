/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.text.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.opensourcephysics.display.*;

/**
 * This calculates and displays statistics for a DatasetDataTable.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class DatasetStatisticsTable extends JTable {
  // instance fields
  DatasetDataTable dataTable;
  StatisticsTableModel tableModel;
  LabelRenderer labelRenderer;
  protected Object[][] statsData;

  /**
   * Constructor.
   *
   * @param table the datatable
   */
  public DatasetStatisticsTable(DatasetDataTable table) {
    dataTable = table;
    tableModel = new StatisticsTableModel();
    init();
  }

  /**
   * Initializes the table.
   */
  protected void init() {
    dataTable.getColumnModel().addColumnModelListener(new
        TableColumnModelListener() {
      public void columnAdded(TableColumnModelEvent e) {}

      public void columnRemoved(TableColumnModelEvent e) {}

      public void columnSelectionChanged(ListSelectionEvent e) {}

      public void columnMarginChanged(ChangeEvent e) {
        refreshTable();
      }

      public void columnMoved(TableColumnModelEvent e) {
        refreshTable();
      }
    });
    // assemble statistics data array
    Dataset workingData = dataTable.getWorkingData();
    double[] xstats = getStatistics(workingData.getXPoints());
    double[] ystats = getStatistics(workingData.getValidYPoints());
    if (statsData == null) {
      statsData = new Object[xstats.length][0];
    }
    for (int i = 0; i < xstats.length; i++) {
      String label = DatasetRes.getString("Table.Entry.Count");
      if (i == 5) {
        statsData[i] = new Object[] {label,
                       new Integer((int)xstats[i]),
                       new Integer((int)ystats[i])};
      }
      else {
        switch(i) {
          case 0: label = DatasetRes.getString("Table.Entry.Max"); break;
          case 1: label = DatasetRes.getString("Table.Entry.Min"); break;
          case 2: label = DatasetRes.getString("Table.Entry.Mean"); break;
          case 3: label = DatasetRes.getString("Table.Entry.StandardDev"); break;
          case 4: label = DatasetRes.getString("Table.Entry.StandardError");
        }
        statsData[i] = new Object[] {label,
                       new Double(xstats[i]),
                       new Double(ystats[i])};
      }
    }
    // set and configure table model and header
    setModel(tableModel);
    setGridColor(Color.blue);
    setEnabled(false);
    setTableHeader(null); // no table header
    labelRenderer = new LabelRenderer();
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setDefaultRenderer(Double.class, new ScientificRenderer(3));
    ListSelectionModel selectionModel = dataTable.getSelectionModel();
    selectionModel.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        Dataset workingData = dataTable.getWorkingData();
        double[] xstats = getStatistics(workingData.getXPoints());
        double[] ystats = getStatistics(workingData.getValidYPoints());
        int i = 0;
        for (; i < xstats.length - 1; i++) {
          statsData[i][1] = new Double(xstats[i]);
          statsData[i][2] = new Double(ystats[i]);
        }
        statsData[i][1] = new Integer((int)xstats[i]);
        statsData[i][2] = new Integer((int)ystats[i]);
        refreshTable();
      }
    });
    refreshCellWidths();
  }

  /**
   *  Refresh the data in this table.
   */
  public void getXStatistics() {

  }

  /**
   *  Calculates statistical values for a data array.
   *
   * @param data the data array
   * @return the max, min, mean, SD, SE and non-NaN data count
   */
  private double[] getStatistics(double[] data) {
    double max = -Double.MAX_VALUE;
    double min = Double.MAX_VALUE;
    double sum = 0.0;
    double squareSum = 0.0;
    int count = 0;
    for (int i = 0; i < data.length; i++) {
      if (Double.isNaN(data[i])) {
        continue;
      }
      count++;
      max = Math.max(max, data[i]);
      min = Math.min(min, data[i]);
      sum += data[i];
      squareSum += data[i] * data[i];
    }
    double mean = sum / count;
    double sd = count < 2 ? Double.NaN :
                Math.sqrt((squareSum - count * mean * mean) /
                          (count - 1));
    if (max == -Double.MAX_VALUE) max = Double.NaN;
    if (min == Double.MAX_VALUE) min = Double.NaN;
    return new double[] {
        max, min, mean, sd, sd / Math.sqrt(count), count};
  }


  /**
   *  Refresh the data in this table.
   */
  public void refreshTable() {
    Runnable refresh = new Runnable() {
      public synchronized void run() {
        tableChanged(new TableModelEvent(tableModel, TableModelEvent.HEADER_ROW));
        refreshCellWidths();
      }
    };
    if(SwingUtilities.isEventDispatchThread()) {
      refresh.run();
    } else {
      SwingUtilities.invokeLater(refresh);
    }
  }

  /**
   *  Refresh the cell widths in the table.
   */
  public void refreshCellWidths() {
    // set width of columns
    for (int i = 0; i < getColumnCount(); i++) {
      String name = getColumnName(i);
      TableColumn statColumn = getColumn(name);
      name = dataTable.getColumnName(i);
      TableColumn dataColumn = dataTable.getColumn(name);
      statColumn.setMaxWidth(dataColumn.getWidth());
      statColumn.setMinWidth(dataColumn.getWidth());
      statColumn.setWidth(dataColumn.getWidth());
    }
  }

  /**
   * Returns the renderer for a cell specified by row and column.
   *
   * @param row the row number
   * @param column the column number
   * @return the cell renderer
   */
  public TableCellRenderer getCellRenderer(int row, int column) {
    int i = dataTable.convertColumnIndexToModel(column);
    if(i==0) return labelRenderer;
    return getDefaultRenderer(tableModel.getValueAt(row, column).getClass());
  }

  /**
   * A class to provide model data for the statistics table.
   */
  class StatisticsTableModel extends AbstractTableModel {

    public String getColumnName(int col) {
      return dataTable.getColumnName(col);
    }

    public int getRowCount() {
      return statsData.length;
    }

    public int getColumnCount() {
      return dataTable.getColumnCount();
    }

    public Object getValueAt(int row, int col) {
      int i = dataTable.convertColumnIndexToModel(col);
      return statsData[row][i];
    }

    public boolean isCellEditable(int row, int col) {
      return false;
    }

    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

  }

  /**
   * A class to render labels.
   */
  static class LabelRenderer extends JLabel implements TableCellRenderer {

    public LabelRenderer() {
      setHorizontalAlignment(SwingConstants.RIGHT);
      setOpaque(true); // make background visible.
      setForeground(Color.black);
      setBackground(javax.swing.UIManager.getColor("Panel.background"));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int row,
                                                   int column) {
      setText(value.toString());
      return this;
    }
  }

  /**
   * A class to render numbers in scientific format.
   */
  class ScientificRenderer
      extends JLabel implements TableCellRenderer {
    NumberFormat format = NumberFormat.getInstance();

    public ScientificRenderer(int sigfigs) {
      sigfigs = Math.min(sigfigs, 6);
      if (format instanceof DecimalFormat) {
        String pattern = "0.0";
        for (int i = 0; i < sigfigs - 1; i++) {
          pattern += "0";
        }
        pattern += "E0";
        ((DecimalFormat)format).applyPattern(pattern);
      }
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int row,
                                                   int column) {
      setFont(getDefaultRenderer(String.class).getTableCellRendererComponent(
          DatasetStatisticsTable.this, "a", false, false, 0, 0).getFont());
      setText(format.format(value));
      setHorizontalAlignment(SwingConstants.TRAILING);
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
