/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import javax.swing.table.AbstractTableModel;

/**
 * A table model for an ArrayTable.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class ArrayTableModel extends AbstractTableModel {
  // instance fields
  double[] doubleArray1;
  double[][] doubleArray2;
  int[] intArray1;
  int[][] intArray2;
  boolean editable = false;

  public ArrayTableModel(int[] array) {
    intArray1 = array;
  }

  public ArrayTableModel(int[][] array) {
    intArray2 = array;
  }

  public ArrayTableModel(double[] array) {
    doubleArray1 = array;
  }

  public ArrayTableModel(double[][] array) {
    doubleArray2 = array;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  /**
   * Gets the number of columns.
   *
   * @return the column count
   */
  public int getColumnCount() {
    if(getRowCount()==0) {
      return 0;
    }
    if(intArray1!=null) {
      return 2;
    }
    if(intArray2!=null) {
      return intArray2[0].length+1;
    }
    if(doubleArray1!=null) {
      return 2;
    }
    if(doubleArray2!=null) {
      return doubleArray2[0].length+1;
    }
    return 0;
  }

  /**
   * Gets the name of the specified column.
   *
   * @param column the column index
   * @return the column name
   */
  public String getColumnName(int column) {
    if(column==0) {
      return "";
    }
    if(intArray1!=null||doubleArray1!=null) {
      return "value";
    }
    return ""+(column-1);
  }

  /**
   * Gets the number of rows.
   *
   * @return the row count
   */
  public int getRowCount() {
    if(intArray1!=null) {
      return intArray1.length;
    }
    if(intArray2!=null) {
      return intArray2.length;
    }
    if(doubleArray1!=null) {
      return doubleArray1.length;
    }
    if(doubleArray2!=null) {
      return doubleArray2.length;
    }
    return 0;
  }

  /**
   * Gets the value at the given cell.
   *
   * @param row the row index
   * @param column the column index
   * @return the value
   */
  public Object getValueAt(int row, int column) {
    if(column==0) {
      return new Integer(row);
    }
    if(intArray1!=null) {
      return new Integer(intArray1[row]);
    }
    if(intArray2!=null) {
      return new Integer(intArray2[row][column-1]);
    }
    if(doubleArray1!=null) {
      return new Double(doubleArray1[row]);
    }
    if(doubleArray2!=null) {
      return new Double(doubleArray2[row][column-1]);
    }
    return null;
  }

  /**
   * Determines whether the given cell is editable.
   *
   * @param row the row index
   * @param col the column index
   * @return true if editable
   */
  public boolean isCellEditable(int row, int col) {
    return editable&&col>0;
  }

  /**
   * Sets the value at the given cell.
   *
   * @param value the value
   * @param row the row index
   * @param col the column index
   */
  public void setValueAt(Object value, int row, int col) {
    if(value instanceof String) {
      String val = (String) value;
      if(intArray1!=null) {
        try {
          intArray1[row] = Integer.parseInt(val);
        } catch(NumberFormatException ex) {}
      } else if(intArray2!=null) {
        try {
          intArray2[row][col-1] = Integer.parseInt(val);
        } catch(NumberFormatException ex) {}
      } else if(doubleArray1!=null) {
        try {
          doubleArray1[row] = Double.parseDouble(val);
        } catch(NumberFormatException ex) {}
      } else if(doubleArray2!=null) {
        try {
          doubleArray2[row][col-1] = Double.parseDouble(val);
        } catch(NumberFormatException ex) {}
      }
      fireTableCellUpdated(row, col);
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
