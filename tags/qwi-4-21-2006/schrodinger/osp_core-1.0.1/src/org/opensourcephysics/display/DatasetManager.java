/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import org.opensourcephysics.controls.XML;
import org.opensourcephysics.controls.XMLControl;
import org.opensourcephysics.controls.XMLLoader;

/**
 *
 *  DatasetManager maintains a list of datasets. Datasets are added
 *  automatically to this DatasetCollection by calling a method in this
 *  DatasetManager with a dataset index greater than the maximum value for
 *  the dataset index that has been used previously. For example the statements:
 *  <code> DatasetManager datasetManager = new DatasetManager();
 *  datasetManager.append(0,3,4);
 *  datasetManager.append(1,5,6);</code> appends the point (3,4) to the 0th
 *  dataset (and creates this dataset automatically) and appends the point (5,6)
 *  to the 1-st dataset (and also creates this dataset automatically).
 *
 *  Modified June 27, 2002 by W. Christian.
 *
 * @author     jgould
 * @created    February 17, 2002
 *
 */
public class DatasetManager extends AbstractTableModel implements Measurable {
  ArrayList datasets = new ArrayList();
  boolean connected;      // default values for new datasets
  boolean sorted;
  int markerShape;
  int stride = 1;
  boolean linked = false; // whether x data in datasets is linked. If set to true, then x data for datasets > 0 will not be shown in a table view.
  static Color[] lineColors = {
    Color.red, Color.green, Color.blue, Color.yellow, Color.cyan, Color.magenta
  };
  static Color[] markerColors = {
    Color.black, Color.blue, Color.red, Color.green, Color.darkGray, Color.lightGray
  };
  String xColumnName = "x", yColumnName = "y"; // default names for new datasets

  /**
   *
   *  DatasetManager constructor.
   *
   */
  public DatasetManager() {
    this(false, false, false, Dataset.SQUARE);
  }

  /**
   *
   *  DatasetManager constructor.
   *
   * @param linked
   *
   */
  public DatasetManager(boolean linked) {
    this(false, false, linked, Dataset.SQUARE);
  }

  /**
   *
   *  DatasetManager constructor specifying whether points are connected and
   *  sorted.
   * @param  _connected  Description of Parameter
   *
   * @param  _sorted     Description of Parameter
   *
   */
  public DatasetManager(boolean _connected, boolean _sorted) {
    this(_connected, _sorted, false, Dataset.SQUARE);
  }

  /**
   *
   *  DatasetManager constructor specifying whether points are connected,
   *
   *  sorted, and the marker shape.
   *
   *
   *
   * @param  _connected    Description of Parameter
   *
   * @param  _sorted       Description of Parameter
   *
   * @param _linked
   *
   * @param  _markerShape  Description of Parameter
   *
   */
  public DatasetManager(boolean _connected, boolean _sorted, boolean _linked, int _markerShape) {
    connected = _connected;
    sorted = _sorted;
    markerShape = _markerShape;
    linked = _linked;
  }

  /**
   *  Sets the linked flag. X data for datasets > 0 will not be shown in a table view.
   *
   * @param  _linked  The new value
   */
  public void setXPointsLinked(boolean _linked) {
    linked = _linked;
    for(int i = 1;i<datasets.size();i++) {
      Dataset dataset = (Dataset) datasets.get(i);
      dataset.setXColumnVisible(!linked);
    }
  }

  /**
   *
   *  Sets the sorted flag. Data is sorted by increasing x.
   *
   * @param  datasetIndex  The new sorted value
   * @param  _sorted       <code>true<\code> to sort
   */
  public void setSorted(int datasetIndex, boolean _sorted) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setSorted(_sorted);
  }

  /**
   *
   *  Sets the sorted flag for all datasets.
   *
   * @param _sorted
   *
   */
  public void setSorted(boolean _sorted) {
    sorted = _sorted; // sorted for future datasets
    for(int i = 0;i<datasets.size();i++) {
      ((Dataset) (datasets.get(i))).setSorted(_sorted);
    }
  }

  /**
   *  Sets the data connected flag. Points are connected by straight lines.
   *
   * @param  datasetIndex  The new connected value
   * @param  _connected    <code>true<\code> if points are connected
   */
  public void setConnected(int datasetIndex, boolean _connected) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setConnected(_connected);
  }

  /**
   *  Sets the connected flag for all datasets.
   *
   * @param _connected true if connected; false otherwise
   */
  public void setConnected(boolean _connected) {
    connected = _connected; // sorted for future datasets
    for(int i = 0;i<datasets.size();i++) {
      ((Dataset) (datasets.get(i))).setConnected(_connected);
    }
  }

  /**
   *
   *  Sets the stride for the given dataset.
   *
   * @param  datasetIndex  The new markerColor value
   * @param  stride
   */
  public void setStride(int datasetIndex, int stride) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setStride(stride);
  }

  /*
   *  Sets the stride for all datasets.
   *
   * @param _stride
   */
  public void setStride(int _stride) {
    stride = _stride; // default stride for future datasets
    // set the stride for current datasets
    for(int i = 0;i<datasets.size();i++) {
      ((Dataset) (datasets.get(i))).setStride(stride);
    }
  }

  /**
   *  Sets the data point marker color.
   *
   * @param  datasetIndex
   * @param  _markerColor
   */
  public void setMarkerColor(int datasetIndex, Color _markerColor) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setMarkerColor(_markerColor);
  }

  /**
   *  Sets the data point marker's fill and edge color.
   *
   * @param  datasetIndex
   * @param  fillColor
   * @param  edgeColor
   */
  public void setMarkerColor(int datasetIndex, Color fillColor, Color edgeColor) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setMarkerColor(fillColor, edgeColor);
  }

  /**
   *  Sets the data point marker shape. Shapes are: NO_MARKER, CIRCLE, SQUARE,
   *  AREA, PIXEL, BAR, POST
   *
   * @param  datasetIndex
   * @param  _markerShape
   */
  public void setMarkerShape(int datasetIndex, int _markerShape) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setMarkerShape(_markerShape);
  }

  /**
   * Sets a custom marker shape.
   *
   * @param datasetIndex int
   * @param marker Shape
   */
  public void setCustomMarker(int datasetIndex, Shape marker) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setCustomMarker(marker);
  }

  /**
   *  Sets the visibility of the x column in a table view.
   *
   * @param  datasetIndex
   * @param  visible
   */
  public void setXColumnVisible(int datasetIndex, boolean visible) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setXColumnVisible(visible);
  }

  /**
   *  Sets the visibility of the y column in a table view.
   *
   * @param  datasetIndex
   * @param  visible
   */
  public void setYColumnVisible(int datasetIndex, boolean visible) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setYColumnVisible(visible);
  }

  /**
   *  Sets the half-width of the data point marker.
   *
   * @param  datasetIndex
   * @param  _markerSize   in pixels
   */
  public void setMarkerSize(int datasetIndex, int _markerSize) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setMarkerSize(_markerSize);
  }

  /**
   *  Sets the color of the lines connecting data points.
   *
   * @param  datasetIndex
   * @param  _lineColor
   */
  public void setLineColor(int datasetIndex, Color _lineColor) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setLineColor(_lineColor);
  }

  /**
   *  Sets the column names when rendering this dataset in a JTable.
   *
   * @param  datasetIndex
   * @param  xColumnName
   * @param  yColumnName
   * @param  datsetName
   */
  public void setXYColumnNames(int datasetIndex, String xColumnName, String yColumnName, String datsetName) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setXYColumnNames(xColumnName, yColumnName, datsetName);
  }

  /**
 *  Sets the column names when rendering this dataset in a JTable.
 *
 * @param  datasetIndex
 * @param  xColumnName
 * @param  yColumnName
 */
  public void setXYColumnNames(int datasetIndex, String xColumnName, String yColumnName) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.setXYColumnNames(xColumnName, yColumnName);
  }

  /**
   *  Gets the valid measure flag. The measure is valid if the min and max values
   *  have been set for at least one dataset.
   *
   * @return    <code>true<\code> if measure is valid
   */
  public boolean isMeasured() {
    for(int i = 0;i<datasets.size();i++) {
      Dataset d = (Dataset) datasets.get(i);
      if(d.isMeasured()) {
        return true;
      }
    }
    return false;
  }

  /**
   *  Gets the x world coordinate for the left hand side of the panel.
   *
   * @return    xmin
   */
  public double getXMin() {
    double xmin = Double.MAX_VALUE;
    for(int i = 0;i<datasets.size();i++) {
      Dataset d = (Dataset) datasets.get(i);
      if(d.isMeasured()) {
        xmin = Math.min(xmin, d.getXMin());
      }
    }
    return xmin;
  }

  /**
   *  Gets the x world coordinate for the right hand side of the panel.
   *
   * @return    xmax
   */
  public double getXMax() {
    double xmax = -Double.MAX_VALUE;
    for(int i = 0;i<datasets.size();i++) {
      Dataset d = (Dataset) datasets.get(i);
      if(d.isMeasured()) {
        xmax = Math.max(xmax, d.getXMax());
      }
    }
    return xmax;
  }

  /**
   *  Gets y world coordinate for the bottom of the panel.
   *
   * @return    ymin
   */
  public double getYMin() {
    double ymin = Double.MAX_VALUE;
    for(int i = 0;i<datasets.size();i++) {
      Dataset d = (Dataset) datasets.get(i);
      if(d.isMeasured()) {
        ymin = Math.min(ymin, d.getYMin());
      }
    }
    return ymin;
  }

  /**
   *  Gets y world coordinate for the top of the panel.
   *
   * @return    ymax
   */
  public double getYMax() {
    double ymax = -Double.MAX_VALUE;
    for(int i = 0;i<datasets.size();i++) {
      Dataset d = (Dataset) datasets.get(i);
      if(d.isMeasured()) {
        ymax = Math.max(ymax, d.getYMax());
      }
    }
    return ymax;
  }

  /**
   *  Gets a copy of the xpoints array.
   *
   * @param  datasetIndex  Description of Parameter
   * @return               xpoints[]
   */
  public double[] getXPoints(int datasetIndex) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    return dataset.getXPoints();
  }

  /**
   *  Gets a copy of the ypoints array.
   *
   * @param  datasetIndex  Description of Parameter
   * @return               ypoints[]
   */
  public double[] getYPoints(int datasetIndex) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    return dataset.getYPoints();
  }

  /**
   *  Gets the sorted flag.
   *
   * @param  datasetIndex  Description of Parameter
   * @return               <code>true<\code> if the data is sorted
   */
  public boolean isSorted(int datasetIndex) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    return dataset.isSorted();
  }

  /**
   *  Gets the data connected flag.
   *
   * @param  datasetIndex  Description of Parameter
   * @return               <code>true<\code> if points are connected
   */
  public boolean isConnected(int datasetIndex) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    return dataset.isConnected();
  }

  /**
   *  Gets the number of columns for rendering in a JTable.
   *
   * @return    the count
   */
  public int getColumnCount() {
    int columnCount = 0;
    for(int i = 0;i<datasets.size();i++) {
      Dataset d = (Dataset) datasets.get(i);
      columnCount += d.getColumnCount();
    }
    return columnCount;
  }

  /**
   *  Gets the number of rows for rendering in a JTable.
   *
   * @return    the count
   */
  public int getRowCount() {
    int rowCount = 0;
    for(int i = 0;i<datasets.size();i++) {
      Dataset d = (Dataset) datasets.get(i);
      rowCount = Math.max(rowCount, d.getRowCount());
    }
    return rowCount;
  }

  /**
   *  Gets the name of the colummn for rendering in a JTable
   *
   * @param tableColumnIndex
   * @return              the name
   */
  public String getColumnName(int tableColumnIndex) {
    if(datasets.size()==0) {
      return null;
    }
    int totalColumns = 0;
    for(int i = 0;i<datasets.size();i++) {
      Dataset tableModel = (Dataset) datasets.get(i);
      int columnCount = tableModel.getColumnCount();
      totalColumns += columnCount;
      if(totalColumns>tableColumnIndex) {
        int columnIndex = Math.abs(totalColumns-columnCount-tableColumnIndex);
        return tableModel.getColumnName(columnIndex);
      }
    }
    return null;
  }

  /**
   *  Gets an x or y value for rendering in a JTable.
   *
   * @param  rowIndex
   * @param tableColumnIndex
   * @return              the datum
   */
  public Object getValueAt(int rowIndex, int tableColumnIndex) {
    if(datasets.size()==0) {
      return null;
    }
    int totalColumns = 0;
    for(int i = 0;i<datasets.size();i++) {
      Dataset tableModel = (Dataset) datasets.get(i);
      int columnCount = tableModel.getColumnCount();
      totalColumns += columnCount;
      if(totalColumns>tableColumnIndex) {
        if(rowIndex>=tableModel.getRowCount()) {
          return null;
        }
        int columnIndex = Math.abs(totalColumns-columnCount-tableColumnIndex);
        return tableModel.getValueAt(rowIndex, columnIndex);
      }
    }
    return null;
  }

  /**
   *  Gets the type of object for JTable entry.
   *
   * @param  columnIndex
   * @return              the class
   */
  public Class getColumnClass(int columnIndex) {
    return Double.class;
  }

  /**
   *  Appends an (x,y) datum to the Dataset with the given index.
   *
   * @param  x
   * @param  y
   * @param  datasetIndex  Description of Parameter
   */
  public void append(int datasetIndex, double x, double y) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.append(x, y);
  }

  /**
   *  Appends a data point and its uncertainty to the Dataset.
   *
   * @param datasetIndex
   * @param  x
   * @param  y
   * @param  delx
   * @param  dely
   *
   */
  public void append(int datasetIndex, double x, double y, double delx, double dely) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.append(x, y, delx, dely);
  }

  /**
   *  Appends (x,y) arrays to the Dataset.
   *
   * @param  xpoints
   * @param  ypoints
   * @param  datasetIndex  Description of Parameter
   */
  public void append(int datasetIndex, double[] xpoints, double[] ypoints) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.append(xpoints, ypoints);
  }

  /**
   *  Appends arrays of data points and uncertainties to the Dataset.
   *
   * @param datasetIndex
   * @param  xpoints
   * @param  ypoints
   * @param  delx
   * @param  dely
   */
  public void append(int datasetIndex, double[] xpoints, double[] ypoints, double[] delx, double[] dely) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.append(xpoints, ypoints, delx, dely);
  }

  /**
   *  Draws this Dataset in the drawing panel.
   *
   * @param  drawingPanel
   * @param  g
   */
  public void draw(DrawingPanel drawingPanel, Graphics g) {
    for(int i = 0;i<datasets.size();i++) {
      ((Dataset) (datasets.get(i))).draw(drawingPanel, g);
    }
  }

  /**
   *  Clears all data from Dataset with the given datasetIndex.
   *
   * @param  datasetIndex  Description of Parameter
   */
  public void clear(int datasetIndex) {
    checkDatasetIndex(datasetIndex);
    Dataset dataset = (Dataset) datasets.get(datasetIndex);
    dataset.clear();
  }

  /**
   * Clears all data from all Datasets.
   *
   * Dataset properties are preserved because only the data is cleared.
   */
  public void clear() {
    for(int i = 0;i<datasets.size();i++) {
      ((Dataset) (datasets.get(i))).clear();
    }
  }

  /**
   * Removes all Datasets from the manager.
   *
   * New datasets will be created with default properties as needed.
   */
  public void removeDatasets() {
    clear();
    datasets.clear();
  }

  /**
   *  Gets a dataset with the given index.
   *
   * @param datasetIndex
   * @return    the index
   *
   */
  public Dataset getDataset(int datasetIndex) {
    checkDatasetIndex(datasetIndex);
    return(Dataset) datasets.get(datasetIndex);
  }

  /**
   * Gets a shallow clone of the dataset list.
   * @return cloned list
   */
  public ArrayList getDatasets() {
    return(ArrayList) datasets.clone();
  }

  /**
   *  Create a string representation of the data.
   * @return    the data
   */
  public String toString() {
    if(datasets.size()==0) {
      return "No data in datasets.";
    }
    StringBuffer b = new StringBuffer();
    for(int i = 0;i<datasets.size();i++) {
      b.append("Dataset ");
      b.append(i);
      b.append('\n');
      b.append(datasets.get(i).toString());
    }
    return b.toString();
  }

  /**
   *
   *  Sets the column names for all datasets when rendering this dataset in a JTable.
   *
   *
   *
   * @param  _xColumnName
   *
   * @param  _yColumnName
   *
   */
  public void setXYColumnNames(String _xColumnName, String _yColumnName) {
    xColumnName = _xColumnName; // default names for future datasets
    yColumnName = _yColumnName; // default names for future datasets
    // set the column names for current datasets
    for(int i = 0, size = datasets.size();i<size;i++) {
      ((Dataset) (datasets.get(i))).setXYColumnNames(_xColumnName, _yColumnName);
    }
  }

  /**
   * Gets a line color that matches the index
   * @param index int
   * @return Color
   */
  static public Color getLineColor(int index) {
    if(index<lineColors.length-1) {
      return lineColors[index]; // use specified colors
    } else {
      return GUIUtils.randomColor();
    }
  }

  /**
   *  Ensures capacity
   *
   * @param  datasetIndex
   */
  protected void checkDatasetIndex(int datasetIndex) {
    while(datasetIndex>=datasets.size()) {
      Dataset d = null;
      if(datasetIndex<lineColors.length-1) {
        d = new Dataset(markerColors[datasetIndex], lineColors[datasetIndex], connected); // use specified colors
      } else {
        d = new Dataset(GUIUtils.randomColor(), GUIUtils.randomColor(), connected);
      }
      if(linked&&(datasets.size()>0)) {
        d.setXColumnVisible(false); // hide all x points in new datasets (except the 0th dataset)
      }
      d.setSorted(sorted);
      d.setXYColumnNames(xColumnName, yColumnName);
      d.setMarkerShape(markerShape);
      datasets.add(d);
    }
  }

  /**
   * Returns the XML.ObjectLoader for this class.
   *
   * @return the object loader
   */
  public static XML.ObjectLoader getLoader() {
    return new Loader();
  }

  /**
   * A class to save and load Dataset data in an XMLControl.
   */
  private static class Loader extends XMLLoader {
    public void saveObject(XMLControl control, Object obj) {
      DatasetManager dsm = (DatasetManager) obj;
      control.setValue("connected", dsm.connected);
      control.setValue("sorted", dsm.sorted);
      control.setValue("maker_shape", dsm.markerShape);
      control.setValue("stride", dsm.stride);
      control.setValue("linked", dsm.linked);
      control.setValue("x_column_name", dsm.xColumnName);
      control.setValue("y_column_name", dsm.yColumnName);
      control.setValue("datasets", dsm.datasets);
    }

    public Object createObject(XMLControl control) {
      return new DatasetManager();
    }

    public Object loadObject(XMLControl control, Object obj) {
      DatasetManager dsm = (DatasetManager) obj;
      dsm.connected = control.getBoolean("connected");
      dsm.sorted = control.getBoolean("sorted");
      dsm.markerShape = control.getInt("maker_shape");
      dsm.stride = control.getInt("stride");
      dsm.linked = control.getBoolean("linked");
      dsm.xColumnName = control.getString("x_column_name");
      dsm.yColumnName = control.getString("y_column_name");
      Collection datasets = (Collection) control.getObject("datasets");
      dsm.removeDatasets();
      if(datasets!=null) {
        Iterator it = datasets.iterator();
        while(it.hasNext()) {
          dsm.datasets.add((Dataset) it.next());
        }
      }
      return obj;
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
