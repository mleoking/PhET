/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.frames;
import org.opensourcephysics.display.*;

public class TableFrame extends DataTableFrame {
  TableData tableData = new TableData();

  /**
   * Constructs a TableFrame with the given title.
   * @param frameTitle String
   */
  public TableFrame(String frameTitle) {
    super(frameTitle, new DataTable());
    setAnimated(true);
    setAutoclear(true);
    table.setModel(tableData);
    setRowNumberVisible(true);
  }

  /**
   *  Sets the display row number flag. Table displays row number.
   *
   * @param  vis  <code>true<\code> if table display row number
   */
  public void setRowNumberVisible(boolean vis) {
    table.setRowNumberVisible(vis);
    tableData.setRowNumberVisible(vis);
  }

  /**
   * Appends a two dimensional array to this table.
   *
   * @param obj Object
   * @throws IllegalArgumentException
   */
  public synchronized void appendArray(Object obj) throws IllegalArgumentException {
    if(!obj.getClass().isArray()) {
      throw new IllegalArgumentException("");
    }
    // make sure ultimate component class is acceptable
    Class componentType = obj.getClass().getComponentType();
    while(componentType.isArray()) {
      componentType = componentType.getComponentType();
    }
    String type = componentType.getName();
    if(type.equals("double")) {
      double[][] array = (double[][]) obj;
      double[] row = new double[array.length];
      for(int i = 0, n = array[0].length;i<n;i++) {
        for(int j = 0, m = row.length;j<m;j++) {
          row[j] = array[j][i];
        }
        appendRow(row);
      }
    } else if(type.equals("int")) {
      int[][] array = (int[][]) obj;
      int[] row = new int[array.length];
      for(int i = 0, n = array[0].length;i<n;i++) {
        for(int j = 0, m = row.length;j<m;j++) {
          row[j] = array[j][i];
        }
        appendRow(row);
      }
    } else if(type.equals("byte")) {
      byte[][] array = (byte[][]) obj;
      byte[] row = new byte[array.length];
      for(int i = 0, n = array[0].length;i<n;i++) {
        for(int j = 0, m = row.length;j<m;j++) {
          row[j] = array[j][i];
        }
        appendRow(row);
      }
    } else {
      Object[][] array = (Object[][]) obj;
      Object[] row = new Object[array.length];
      for(int i = 0, n = array[0].length;i<n;i++) {
        for(int j = 0, m = row.length;j<m;j++) {
          row[j] = array[j][i];
        }
        appendRow(row);
      }
    }
  }

  /**
   * Appends a row of data with the given values to the table.
   * @param x double[]
   */
  public synchronized void appendRow(double[] x) {
    tableData.appendDoubles(x);
    if(isShowing()) {
      table.refreshTable();
    }
  }

  /**
   * Appends a row of data with the given values to the table.
   * @param x double[]
   */
  public synchronized void appendRow(int[] x) {
    tableData.appendInts(x);
    if(isShowing()) {
      table.refreshTable();
    }
  }

  /**
   * Appends a row of data with the given values to the table.
   * @param x double[]
   */
  public synchronized void appendRow(Object[] x) {
    tableData.appendRow(x);
    if(isShowing()) {
      table.refreshTable();
    }
  }

  /**
   * Appends a row of data with the given values to the table.
   * @param x double[]
   */
  public synchronized void appendRow(byte[] x) {
    tableData.appendBytes(x);
    if(isShowing()) {
      table.refreshTable();
    }
  }

  /**
   *  Sets the column names in a JTable.
   *
   * @param  column  the index
   * @param  name
   */
  public void setColumnNames(int column, String name) {
    tableData.setColumnNames(column, name);
  }

  /**
   *  Sets the format for displaying decimals.
   *
   * @param  column  the index
   * @param  format
   */
  public void setColumnFormat(int column, String format) {
    tableData.setColumnFormat(column, format);
  }

  /**
   * Shows or hides this TableFrame depending on the value of parameter
   * <code>vis</code>.
   * @param vis  if <code>true</code>, shows this component;
   * otherwise, hides this component
   */
  public void setVisible(boolean vis) {
    if(vis) {
      table.refreshTable(); // make sure the table shows the current values
    }
    super.setVisible(vis);
  }

  /**
   * Clears data from drawing objects within this frame.
   *
   * The default method does nothing.
   * Override this method to select the object(s) and the data to be cleared.
   */
  public synchronized void clearData() {
    tableData.rowList.clear();
    tableData.colCount = 0;
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
