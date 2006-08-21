/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import javax.swing.*;

import org.opensourcephysics.display.*;

/**
 * This is a DataTable for working with a highlightable dataset.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class DatasetDataTable extends DataTable {
  final HighlightableDataset dataset;
  HighlightableDataset data = new HighlightableDataset();
  HighlightableDataset workingData = new HighlightableDataset();

  /**
   * Constructs a DatasetDataTable for the specified dataset.
   *
   * @param dataset the dataset
   */
  public DatasetDataTable(HighlightableDataset dataset) {
    this.dataset = dataset;
    add(dataset);
    setRowNumberVisible(true);
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  }

  /**
   * Gets the data. The returned dataset reverses x and y points
   * if the corresponding table columns are reversed.
   *
   * @return the dataset
   */
  protected HighlightableDataset getData() {
    data.clear();
    if (this.isXYColumnsReversed()) {
      data.append(dataset.getYPoints(), dataset.getXPoints());
      data.setXYColumnNames(dataset.getColumnName(1),
                            dataset.getColumnName(0));
    }
    else {
      data.append(dataset.getXPoints(), dataset.getYPoints());
      data.setXYColumnNames(dataset.getColumnName(0),
                            dataset.getColumnName(1));
    }
    data.setMarkerShape(dataset.getMarkerShape());
    data.setMarkerSize(dataset.getMarkerSize());
    data.setConnected(dataset.isConnected());
    data.setLineColor(dataset.getLineColor());
    data.setName(dataset.getName());
    data.setMarkerColor(dataset.getFillColor(), dataset.getEdgeColor());
    for (int i = 0; i < dataset.getRowCount(); i++) {
      data.setHighlighted(i, dataset.isHighlighted(i));
    }
    return data;
  }

  /**
   * Gets the working data. The returned dataset reverses x and y points
   * if the corresponding table columns are reversed.
   *
   * @return the data in the selected rows, or all data if no rows are selected
   */
  protected HighlightableDataset getWorkingData() {
    double[] xValues, yValues;
    double[] x = dataset.getXPoints();
    double[] y = dataset.getYPoints();
    dataset.clearHighlights();
    data.clearHighlights();
    if (getSelectedRowCount() == 0) { // nothing selected
      xValues = x;
      yValues = y;
    }
    else {
      int[] rows = getSelectedRows();
      xValues = new double[rows.length];
      yValues = new double[rows.length];
      for (int i = 0; i < rows.length; i++) {
        xValues[i] = x[rows[i]];
        yValues[i] = y[rows[i]];
        dataset.setHighlighted(rows[i], true);
        data.setHighlighted(rows[i], true);
      }
    }
    workingData.clear();
    if (isXYColumnsReversed()) {
      workingData.append(yValues, xValues);
      workingData.setXYColumnNames(dataset.getColumnName(1),
                                    dataset.getColumnName(0));
    }
    else {
      workingData.append(xValues, yValues);
      workingData.setXYColumnNames(dataset.getColumnName(0),
                                    dataset.getColumnName(1));
    }
    workingData.setMarkerShape(dataset.getMarkerShape());
    workingData.setMarkerSize(dataset.getMarkerSize());
    workingData.setConnected(dataset.isConnected());
    workingData.setLineColor(dataset.getLineColor());
    workingData.setName(dataset.getName());
    workingData.setMarkerColor(dataset.getFillColor(), dataset.getEdgeColor());
    return workingData;
  }

  /**
   * Returns true if the x and y columns are in reverse order in this table.
   *
   * @return true if reversed
   */
  protected boolean isXYColumnsReversed() {
    int i = isRowNumberVisible()? 1: 0;
    return (convertColumnIndexToView(i) > convertColumnIndexToView(i+1));
  }

  /**
   * Deselects all selected columns and rows. Overrides JTable method.
   */
  public void clearSelection() {
    if (dataset != null) {
      dataset.clearHighlights();
      data.clearHighlights();
    }
    super.clearSelection();
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
