/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.table.AbstractTableModel;
import org.opensourcephysics.controls.*;

/**
 *  Dataset stores and plots (x,y) points.
 *  Dataset is Drawable and can be rendered on a DrawingPanel. Dataset
 *  extends AbstractTableModel and can be rendered in a JTable.
 *
 * @author     Joshua Gould
 * @author     Wolfgang Christian
 * @created    February 13, 2002
 * @version    1.0
 */
public class Dataset extends AbstractTableModel implements Measurable {

  /** Field datasetID an integer ID that identifies this object */
  protected int datasetID = hashCode(); // good enough for testing

  /** Field NO_MARKER */
  public final static int NO_MARKER = 0; // no marker

  /** Field CIRCLE */
  public final static int CIRCLE = 1; // marker type

  /**  Field SQUARE */
  public final static int SQUARE = 2; // marker type

  /** Field AREA           */
  public final static int AREA = 5; // marker type

  /** Field PIXEL           */
  public final static int PIXEL = 6; // marker type

  /** Field BAR           */
  public final static int BAR = 7; // marker type

  /** Field POST           */
  public final static int POST = 8; // marker type

  /** Field POST           */
  public final static int CUSTOM = -1; // marker type
  protected double[] xpoints;
  // array of x points

  protected double[] ypoints;
  // array of y points\

  protected GeneralPath generalPath;
  // path used to draw line plots

  protected double xmax;
  // the maximum x value in the dataset

  protected double ymax;
  // the maximum y value in the dataset

  protected double xmin;
  // the minimum x value in the dataset

  protected double ymin;
  // the minimum y value in the dataset

  protected int index;
  // the current index of the array

  protected boolean sorted = false;
  // sort the data by increasing x

  private int initialSize;
  // the initial size of the points array

  private int markerSize = 2;
  // the size in pixels of the marker

  private int markerShape = SQUARE;
  // the type of marker

  private Color lineColor;
  // the color of the line

  private Color fillColor;
  // the fill color of the marker

  private Color edgeColor;
  // the edge color of the marker

  private Color errorBarColor;
  // the error bar color of the marker

  private boolean connected;
  // whether the points are connected

  private String name = null;
  // an optional name that can used to identify this dataset

  private String xColumnName;
  // the name of the x data

  private String yColumnName;
  // the name of the y data

  private boolean[] visible = new boolean[2];
  // column visibilites for table view

  private int stride = 1;
  // stride for table view

  protected int maxPoints = 16*1024;
  // the maximum number of points that will be saved in a dataset

  protected ArrayList errorBars = new ArrayList();
  protected Shape customMarker = new Rectangle2D.Double(-markerSize/2, -markerSize/2, markerSize, markerSize);

  /**
   *  Dataset contructor.
   */
  public Dataset() {
    this(Color.black, Color.black, false);
  }

  /**
   *  Dataset contructor specifying the marker color.
   *
   * @param  _markerColor
   */
  public Dataset(Color _markerColor) {
    this(_markerColor, Color.black, false);
  }

  /**
   *  Dataset contructor specifying the marker color, line color, and whether
   *  points are connected.
   *
   * @param  markerColor
   * @param  _lineColor
   * @param  _connected
   */
  public Dataset(Color markerColor, Color _lineColor, boolean _connected) {
    fillColor = markerColor;
    edgeColor = markerColor;
    errorBarColor = markerColor;
    lineColor = _lineColor;
    connected = _connected;
    markerSize = 2;
    initialSize = 10;
    xColumnName = "x";
    yColumnName = "y";
    generalPath = new GeneralPath();
    index = 0;
    java.util.Arrays.fill(visible, true);
    clear();
  }

  /**
   * Sets an id that can be used to identify the dataset.
   *
   * @param id int
   */
  public void setID(int id) {
    datasetID = id;
  }

  /**
   * Dets an id that can be used to identify the dataset.
   *
   * @return the id
   */
  public int getID() {
    return datasetID;
  }

  /**
   *  Sets the sorted flag. Data is sorted by increasing x.
   *
   * @param  _sorted  <code>true<\code> to sort
   *
   */
  public void setSorted(boolean _sorted) {
    sorted = _sorted;
    if(sorted) {
      insertionSort();
    }
  }

  /**
   *  Sets the data connected flag. Points are connected by straight lines.
   *
   * @param  _connected  <code>true<\code> if points are connected
   *
   */
  public void setConnected(boolean _connected) {
    connected = _connected;
    if(connected) {
      recalculatePath();
    }
  }

  /**
   *  Sets the data point fill, edge, and error bar colors to the same color.
   *
   * @param  markerColor
   */
  public void setMarkerColor(Color markerColor) {
    fillColor = markerColor;
    edgeColor = markerColor;
    errorBarColor = markerColor;
  }

  /**
   * Sets the data point marker colors.
   *
   * The error bar color is set equal to the edge color.
   *
   * @param  _fillColor
   * @param  _edgeColor
   */
  public void setMarkerColor(Color _fillColor, Color _edgeColor) {
    fillColor = _fillColor;
    edgeColor = _edgeColor;
    errorBarColor = _edgeColor;
  }

  /**
   * Sets the data point marker colors.
   *
   * @param  _fillColor
   * @param  _edgeColor
   * @param  _errorBarColor
   */
  public void setMarkerColor(Color _fillColor, Color _edgeColor, Color _errorBarColor) {
    fillColor = _fillColor;
    edgeColor = _edgeColor;
    errorBarColor = _errorBarColor;
  }

  /**
   * Gets the data point fill color.
   *
   * @return the fill color
   */
  public Color getFillColor() {
    return fillColor;
  }

  /**
   * Gets the data point edge color.
   *
   * @return the edge color
   */
  public Color getEdgeColor() {
    return edgeColor;
  }

  /**
   * Gets the line color.
   *
   * @return the line color
   */
  public Color getLineColor() {
    return lineColor;
  }

  /**
   * Sets a custom marker shape.
   *
   * @param marker Shape
   */
  public void setCustomMarker(Shape marker) {
    customMarker = marker;
    if(customMarker==null) {
      markerShape = SQUARE;
      customMarker = new Rectangle2D.Double(-markerSize/2, -markerSize/2, markerSize, markerSize);
    } else {
      markerShape = CUSTOM;
    }
  }

  /**
   *  Sets the data point marker shape. Shapes are: NO_MARKER, CIRCLE, SQUARE,
   *  AREA, PIXEL, BAR, POST
   *
   * @param  _markerShape
   */
  public void setMarkerShape(int _markerShape) {
    markerShape = _markerShape;
  }

  /**
   *  Gets the data point marker shape.
   *
   * @return the marker shape
   */
  public int getMarkerShape() {
    return markerShape;
  }

  /**
   * Sets the half-width of the data point marker.
   *
   * @param  _markerSize  in pixels
   */
  public void setMarkerSize(int _markerSize) {
    markerSize = _markerSize;
  }

  /**
   * Sets the maximum number of allowed datapoints.
   *
   * @param maxPoints int
   */
  public void setMaximumPoints(int maxPoints) {
    this.maxPoints = maxPoints;
  }

  /**
   * Gets the half-width of the data point marker.
   *
   * @return the marker size in pixels
   */
  public int getMarkerSize() {
    return markerSize;
  }

  /**
   *  Sets the color of the lines connecting data points.
   *
   * @param  _lineColor
   */
  public void setLineColor(Color _lineColor) {
    lineColor = _lineColor;
  }

  /**
   *  Sets the column names when rendering this dataset in a JTable.
   *
   * @param  _xColumnName
   * @param  _yColumnName
   */
  public void setXYColumnNames(String _xColumnName, String _yColumnName) {
    xColumnName = GUIUtils.parseTeX(_xColumnName);
    yColumnName = GUIUtils.parseTeX(_yColumnName);
  }

  /**
 *  Sets the column names and the dataset name.
 *
 * @param  xColumnName
 * @param  yColumnName
 * @param  name
 */
  public void setXYColumnNames(String xColumnName, String yColumnName, String name) {
    setXYColumnNames(xColumnName, yColumnName);
    this.name = name;
  }

  /**
   * Sets a name that can be used to identify the dataset.
   *
   * @param name String
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the dataset name.
   *
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   *  Gets the valid measure flag. The measure is valid if the min and max values
   *  have been set.
   *
   * @return    <code>true<\code> if measure is valid
   *
   */
  public boolean isMeasured() {
    // changed by D.Brown
    return ymin<Double.MAX_VALUE;
    // return index >= 1;
  }

  /**
   *  Gets the x world coordinate for the left hand side of the panel.
   *
   * @return    xmin
   */
  public double getXMin() {
    return xmin;
  }

  /**
   *  Gets the x world coordinate for the right hand side of the panel.
   *
   * @return    xmax
   */
  public double getXMax() {
    return xmax;
  }

  /**
   *  Gets y world coordinate for the bottom of the panel.
   *
   * @return    ymin
   */
  public double getYMin() {
    return ymin;
  }

  /**
   *  Gets y world coordinate for the top of the panel.
   *
   * @return    ymax
   */
  public double getYMax() {
    return ymax;
  }

  /**
   * Gets a data array containing both x and y values.
   *
   * @return a double[index][2] array of data
   */
  public double[][] getPoints() {
    double[][] temp = new double[index][2];
    for(int i = 0;i<index;i++) {
      temp[i] = new double[] {xpoints[i], ypoints[i]};
    }
    return temp;
  }

  /**
   *  Gets a copy of the xpoints array.
   *
   * @return xpoints[]
   */
  public double[] getXPoints() {
    double[] temp = new double[index];
    System.arraycopy(xpoints, 0, temp, 0, index);
    return temp;
  }

  /**
   *  Gets a copy of the ypoints array.
   *
   * @return    ypoints[]
   */
  public double[] getYPoints() {
    double[] temp = new double[index];
    System.arraycopy(ypoints, 0, temp, 0, index);
    return temp;
  }

  /**
   * Gets an array of valid xpoints.
   * A point is valid if the ypoint for that index is not Double.NaN.
   *
   * @return valid xpoints[]
   */
  public double[] getValidXPoints() {
    return getValidPoints(getXPoints());
  }

  /**
   * Gets an array of valid ypoints.
   * A point is valid if the ypoint for that index is not Double.NaN.
   *
   * @return valid ypoints[]
   */
  public double[] getValidYPoints() {
    return getValidPoints(getYPoints());
  }

  /**
   *  Gets the sorted flag.
   *
   * @return    <code>true<\code> if the data is sorted
   *
   */
  public boolean isSorted() {
    return sorted;
  }

  /**
   *  Gets the data connected flag.
   *
   * @return    <code>true<\code> if points are connected
   *
   */
  public boolean isConnected() {
    return connected;
  }

  /**
   *  Gets the number of columns for rendering in a JTable.
   *
   * @return    the count
   */
  public int getColumnCount() {
    return Dataset.countColumnsVisible(visible);
  }

  /**
   *  Gets the current index of the array.
   *
   *  The index is equal to the number of data points that are currently stored.
   *  When data is appended, it will fill the xpoints and ypoints arrays starting
   *  at the current index.
   *
   * @return    the count
   */
  public int getIndex() {
    return index;
  }

  /**
   *  Gets the number of rows for rendering in a JTable.
   *
   * @return    the count
   */
  public int getRowCount() {
    return index/stride;
  }

  /**
   *  Gets the name of the colummn for rendering in a JTable
   *
   * @param  columnIndex
   * @return              the name
   */
  public String getColumnName(int columnIndex) {
    // System.out.println("columnIndex before " + columnIndex);
    columnIndex = Dataset.convertTableColumnIndex(visible, columnIndex);
    // System.out.println("columnIndex after " + columnIndex);
    if(columnIndex==0) {
      return xColumnName;
    } else {
      return yColumnName;
    }
  }

  /**
   *  Gets an x or y value for rendering in a JTable.
   *
   * @param  rowIndex
   * @param  columnIndex
   * @return              the datum
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    columnIndex = Dataset.convertTableColumnIndex(visible, columnIndex);
    rowIndex = rowIndex*stride;
    if(columnIndex==0) {
      return new Double(xpoints[rowIndex]);
    } else {
      // changed by D.Brown
      if(Double.isNaN(ypoints[rowIndex])) {
        return null;
      }
      return new Double(ypoints[rowIndex]);
    }
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
   *  Appends a data point and its uncertainty to the Dataset.
   *
   * @param  x
   * @param  y
   * @param  delx
   * @param  dely
   */
  public void append(double x, double y, double delx, double dely) {
    errorBars.add(new ErrorBar(x, y, delx, dely));
    append(x, y);
  }

  /**
   * Appends an (x,y) datum to the Dataset. A y value of Double.NaN
   * is treated as null in plots and tables.
   *
   * @param  x
   * @param  y
   */
  public void append(double x, double y) {
    if(Double.isNaN(x)||Double.isInfinite(x)||Double.isInfinite(y)) {
      return;
    }
    if(index>=xpoints.length) {
      increaseCapacity(xpoints.length*2);
    }
    xpoints[index] = x;
    ypoints[index] = y;
    // generalPath.append(new Rectangle2D.Double(x, y, 0, 0), true);
    if(!Double.isNaN(y)) {
      Point2D curPt = generalPath.getCurrentPoint();
      if(curPt==null) {
        generalPath.moveTo((float) x, (float) y);
      } else {
        generalPath.lineTo((float) x, (float) y);
      }
      ymax = Math.max(y, ymax);
      ymin = Math.min(y, ymin);
    }
    xmax = Math.max(x, xmax);
    xmin = Math.min(x, xmin);
    index++;
    // move the new datum if x is less than the last value.
    if(sorted&&(index>1)&&(x<xpoints[index-2])) {
      moveDatum(index-1);
      // the new datum is out of place so move it
      recalculatePath();
      // System.out.println("data moved");
    }
  }

  /**
   *  Appends arrays of data points and uncertainties to the Dataset.
   *
   * @param  xpoints
   * @param  ypoints
   * @param  delx
   * @param  dely
   */
  public void append(double[] xpoints, double[] ypoints, double[] delx, double[] dely) {
    for(int i = 0, n = xpoints.length;i<n;i++) {
      errorBars.add(new ErrorBar(xpoints[i], ypoints[i], delx[i], dely[i]));
    }
    append(xpoints, ypoints);
  }

  /**
   * Appends (x,y) arrays to the Dataset. Any y value of Double.NaN
   * is treated as null in plots and tables.
   *
   * @param  _xpoints
   * @param  _ypoints
   */
  public void append(double[] _xpoints, double[] _ypoints) {
    // changed by D.Brown
    boolean badData = false;
    for(int i = 0;i<_xpoints.length;i++) {
      if(Double.isNaN(_xpoints[i])||Double.isInfinite(_xpoints[i])||Double.isInfinite(_ypoints[i])) {
        badData = true;
        continue;
      }
      xmax = Math.max(_xpoints[i], xmax);
      xmin = Math.min(_xpoints[i], xmin);
      if(!Double.isNaN(_ypoints[i])) {
        ymax = Math.max(_ypoints[i], ymax);
        ymin = Math.min(_ypoints[i], ymin);
        Point2D curPt = generalPath.getCurrentPoint();
        if(curPt==null) {
          generalPath.moveTo((float) _xpoints[i], (float) _ypoints[i]);
        } else {
          generalPath.lineTo((float) _xpoints[i], (float) _ypoints[i]);
        }
      }
    }
    int pointsAdded = _xpoints.length;
    int availableSpots = xpoints.length-index;
    if(pointsAdded>availableSpots) {
      increaseCapacity(xpoints.length+pointsAdded);
      // fix
    }
    System.arraycopy(_xpoints, 0, xpoints, index, pointsAdded);
    System.arraycopy(_ypoints, 0, ypoints, index, pointsAdded);
    index += pointsAdded;
    if(badData) {
      removeBadData();
    }
    if(sorted) {
      insertionSort();
    }
  }

  /**
   *    Reads a file and appends the data contained in the file to this
   *    Dataset. The format of the file is x and y coordinates separated by tabs.
   *    Lines beginning with # are ignored.
   *    @param inputFile
   */
  public void read(String inputFile) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      String s;
      while((s = reader.readLine())!=null) {
        s = s.trim();
        if(s.charAt(0)=='#') { // ignore lines beginning with #
          continue;
        }
        StringTokenizer st = new StringTokenizer(s, "\t");
        switch(st.countTokens()) {
        case 0 :
          continue;
        case 2 :
          append(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
          break;
        default :
          throw new IOException();
        }
      }
    } catch(java.io.FileNotFoundException fnfe) {
      System.err.println("File "+inputFile+" not found.");
    } catch(java.io.IOException ioe) {
      System.err.println("Error reading file "+inputFile);
    } catch(NumberFormatException nfe) {
      System.err.println("Error reading file "+inputFile);
    }
  }

  /**
   *    Writes data from this Dataset to a file. The format of the file is x and y coordinates
   *    separated by tabs.
   *    @param outputFile
   */
  public void write(String outputFile) {
    try {
      PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
      for(int i = 0;i<index;i++) {
        writer.println(xpoints[i]+"\t"+ypoints[i]);
      }
      writer.close();
    } catch(java.io.FileNotFoundException fnfe) {
      System.err.println("File "+outputFile+" not found.");
    } catch(java.io.IOException ioe) {
      System.err.println("Error writing file "+outputFile);
    }
  }

  /**
   *  Draw this Dataset in the drawing panel.
   *
   * @param  drawingPanel
   * @param  g
   */
  public void draw(DrawingPanel drawingPanel, Graphics g) {
    try {
      Graphics2D g2 = (Graphics2D) g;
      if(markerShape!=NO_MARKER) {
        drawScatterPlot(drawingPanel, g2);
      }
      if(connected) {
        drawLinePlot(drawingPanel, g2);
      }
    } catch(Exception ex) {} // abort drawing if we have bad data
  }

  /**
   *  Clear all data from this Dataset.
   */
  public void clear() {
    index = 0;
    xpoints = new double[initialSize];
    ypoints = new double[initialSize];
    generalPath.reset();
    errorBars.clear();
    resetXYMinMax();
  }

  /**
   *  Creates a string representation of the data.
   *
   * @return    the data
   */
  public String toString() {
    if(index==0) {
      return "No data in dataset.";
    }
    String s = xpoints[0]+" "+ypoints[0]+"\n";
    StringBuffer b = new StringBuffer(index*s.length());
    for(int i = 0;i<index;i++) {
      b.append(xpoints[i]);
      String eol = "\n"; // end of line
      try {              // system properties may not be readable!
        eol = System.getProperty("line.separator", "\n");
      } catch(SecurityException ex) {}
      b.append(" ");
      // changed by D.Brown
      if(Double.isNaN(ypoints[i])) {
        b.append("null");
      } else {
        b.append(ypoints[i]);
      }
      b.append(eol);
      // s += xpoints[i] + "\t" + ypoints[i] + "\n";
    }
    return b.toString();
  }

  /**
   *  Counts the number of columns visible
   *
   * @param  visible  array of column visibilities
   * @return          number of visible columns
   */
  public static int countColumnsVisible(boolean visible[]) {
    int count = 0;
    for(int i = 0;i<visible.length;i++) {
      if(visible[i]) {
        count++;
      }
    }
    return count;
  }

  /**
   *  Converts a table column in a table model to the appropriate table column.
   *  For example, if the x points are hidden in a Dataset, and the column index
   *  is 0, then this method will return 1.
   *
   * @param  visible      array of column visibilities
   * @param  columnIndex  table column index to convert
   * @return              converted table column index
   */
  public static int convertTableColumnIndex(boolean visible[], int columnIndex) {
    if(columnIndex==0&&!visible[0]) {
      columnIndex++;
    } else if(columnIndex==1&&!visible[1]) {
      columnIndex--;
    }
    return columnIndex;
  }

  /**
   * Sets the visibility of the x column of this Dataset in a table view.
   * @param b new visibility
   */
  public void setXColumnVisible(boolean b) {
    visible[0] = b;
  }

  /**
   * Sets the visibility of the y column of this Dataset in a table view.
   * @param b new visibility
   */
  public void setYColumnVisible(boolean b) {
    visible[1] = b;
  }

  /**
   * Sets the stride of this Dataset in a table view.
   * @param _stride the stride
   */
  public void setStride(int _stride) {
    stride = _stride;
  }

  /**
   * Gets the visibility of the x column of this Dataset in a table view.
   * @return the x column visibility
   */
  public boolean isXColumnVisible() {
    return visible[0];
  }

  /**
   * Gets the visibility of the y column of this Dataset in a table view.
   * @return the x column visibility
   */
  public boolean isYColumnVisible() {
    return visible[1];
  }

  /**
   *  Perform an insertion sort of the data set. Since data will be partially
   *  sorted this should be fast. Added by W. Christian.
   */
  protected void insertionSort() {
    boolean dataChanged = false;
    if(index<2) {
      return;
      // need at least two points to sort.
    }
    for(int i = 1;i<index;i++) {
      if(xpoints[i]<xpoints[i-1]) {
        // is the i-th datum smaller?
        dataChanged = true;
        moveDatum(i);
      }
    }
    if(dataChanged) {
      recalculatePath();
    }
  }

  /**
   *  Recalculate the general path.
   */
  protected void recalculatePath() {
    generalPath.reset();
    if(index<1) {
      return;
    }
    // changed by D.Brown
    int i = 0;
    for(;i<index;i++) {
      if(!Double.isNaN(ypoints[i])) {
        generalPath.moveTo((float) xpoints[i], (float) ypoints[i]);
        break;
      }
    }
    for(int j = i+1;j<index;j++) {
      if(!Double.isNaN(ypoints[j])) {
        generalPath.lineTo((float) xpoints[j], (float) ypoints[j]);
      }
    }
  }

  /**
   *  Move an out-of-place datum into its correct position.
   *
   * @param  loc  the datum
   */
  protected void moveDatum(int loc) {
    if(loc<1) {
      return;
      // zero-th point cannot be out-of-place
    }
    double x = xpoints[loc];
    // save the old values
    double y = ypoints[loc];
    for(int i = 0;i<index;i++) {
      if(xpoints[i]>x) {
        // find the insertion point
        System.arraycopy(xpoints, i, xpoints, i+1, loc-i);
        xpoints[i] = x;
        System.arraycopy(ypoints, i, ypoints, i+1, loc-i);
        ypoints[i] = y;
        return;
      }
    }
  }

  /**
   *  Draw the lines connecting the data points.
   *
   * @param  drawingPanel
   * @param  g2
   */
  protected void drawLinePlot(DrawingPanel drawingPanel, Graphics2D g2) {
    // changed by D.Brown
    // check that at least one ypoints element is a number
    boolean noNumbers = true;
    for(int i = 0;i<index;i++) {
      noNumbers = Double.isNaN(ypoints[i]);
      if(!noNumbers) {
        break;
      }
    }
    if(noNumbers) {
      return;
    }
    AffineTransform at = drawingPanel.getPixelTransform();
    Shape s = generalPath.createTransformedShape(at);
    g2.setColor(lineColor);
    g2.draw(s);
  }

  /**
   *  Fills the line connecting the data points.
   *
   * @param  drawingPanel
   * @param  g2
   */
  protected void drawFilledPlot(DrawingPanel drawingPanel, Graphics2D g2) {
    // check that at least one ypoints element is a number
    boolean noNumbers = true;
    for(int i = 0;i<index;i++) {
      noNumbers = Double.isNaN(ypoints[i]);
      if(!noNumbers) {
        break;
      }
    }
    if(noNumbers) {
      return;
    }
    AffineTransform at = drawingPanel.getPixelTransform();
    Shape s = generalPath.createTransformedShape(at);
    g2.setColor(fillColor);
    g2.fill(s);
    g2.setColor(edgeColor);
    g2.draw(s);
  }

  /**
   *  Draw the markers at the data points.
   *
   * @param  drawingPanel
   * @param  g2
   */
  protected void drawScatterPlot(DrawingPanel drawingPanel, Graphics2D g2) {
    if(markerShape==AREA) {
      this.drawFilledPlot(drawingPanel, g2);
      return;
    }
    double xp = 0;
    double yp = 0;
    Shape shape = null;
    int size = markerSize*2+1;
    Shape clipShape = g2.getClip();
    // increase the clip so as to include the entire marker
    g2.setClip(drawingPanel.leftGutter-markerSize-1, drawingPanel.topGutter-markerSize-1, drawingPanel.getWidth()-drawingPanel.leftGutter-drawingPanel.rightGutter+2+2*markerSize, drawingPanel.getHeight()-drawingPanel.bottomGutter-drawingPanel.topGutter+2+2*markerSize);
    Rectangle viewRect = drawingPanel.getViewRect();
    if(viewRect!=null) { // decrease the clip if we are in a scroll pane
      g2.clipRect(viewRect.x, viewRect.y, viewRect.x+viewRect.width, viewRect.y+viewRect.height);
    }
    for(int i = 0;i<index;i++) {
      if(Double.isNaN(ypoints[i])) {
        continue;
      }
      xp = drawingPanel.xToPix(xpoints[i]);
      yp = drawingPanel.yToPix(ypoints[i]);
      switch(markerShape) {
      case BAR : // draw a bar graph.
        double bottom = Math.min(drawingPanel.yToPix(0), drawingPanel.yToPix(drawingPanel.getYMin()));
        double barHeight = bottom-yp;
        if(barHeight>0) {
          shape = new Rectangle2D.Double(xp-markerSize, yp, size, barHeight);
        } else {
          shape = new Rectangle2D.Double(xp-markerSize, bottom, size, -barHeight);
        }
        g2.setColor(fillColor);
        g2.fill(shape);
        if(edgeColor!=fillColor) {
          g2.setColor(edgeColor);
          g2.draw(shape);
        }
        break;
      case POST :
        bottom = Math.min(drawingPanel.yToPix(0), drawingPanel.yToPix(drawingPanel.getYMin()));
        shape = new Rectangle2D.Double(xp-markerSize, yp-markerSize, size, size);
        g2.setColor(edgeColor);
        g2.drawLine((int) xp, (int) yp, (int) xp, (int) bottom);
        g2.setColor(fillColor);
        g2.fill(shape);
        if(edgeColor!=fillColor) {
          g2.setColor(edgeColor);
          g2.draw(shape);
        }
        break;
      case SQUARE :
        shape = new Rectangle2D.Double(xp-markerSize, yp-markerSize, size, size);
        g2.setColor(fillColor);
        g2.fill(shape);
        if(edgeColor!=fillColor) {
          g2.setColor(edgeColor);
          g2.draw(shape);
        }
        break;
      case CIRCLE :
        shape = new Ellipse2D.Double(xp-markerSize, yp-markerSize, size, size);
        g2.setColor(fillColor);
        g2.fill(shape);
        if(edgeColor!=fillColor) {
          g2.setColor(edgeColor);
          g2.draw(shape);
        }
        break;
      case PIXEL :
        shape = new Rectangle2D.Double(xp, yp, 0, 0); // this produces a one pixel shape
        g2.draw(shape);
        // draw and center the point
        break;
      case CUSTOM :
        Shape temp = AffineTransform.getTranslateInstance(xp, yp).createTransformedShape(customMarker);
        g2.setColor(fillColor);
        g2.fill(temp);
        if(edgeColor!=fillColor) {
          g2.setColor(edgeColor);
          g2.draw(temp);
        }
        break;
      default :
        shape = new Rectangle2D.Double(xp-markerSize, yp-markerSize, size, size);
        g2.setColor(fillColor);
        g2.fill(shape);
        if(edgeColor!=fillColor) {
          g2.setColor(edgeColor);
          g2.draw(shape);
        }
        break;
      }
    }
    Iterator it = errorBars.iterator();
    while(it.hasNext()) { // copy only the obejcts of the correct type
      ((ErrorBar) it.next()).draw(drawingPanel, g2);
    }
    g2.setClip(clipShape); // restore the original clipping
  }

  /**
   *  Removes infinities and NaN (x only) from the dataset.
   */
  private void removeBadData() {
    for(int i = 0;i<index;i++) {
      if(Double.isNaN(xpoints[i])||Double.isInfinite(xpoints[i])||Double.isInfinite(ypoints[i])) {
        if((index==1)||(i==index-1)) {
          // we only have one point and it is a bad point!
          index--;
          break;
          // exit the loop
        }
        System.arraycopy(xpoints, i+1, xpoints, i, index-i-1);
        System.arraycopy(ypoints, i+1, ypoints, i, index-i-1);
        index--;
      }
    }
  }

  /**
   *  Increase the array size up to a maximum size.
   *
   * @param  newCapacity
   */
  private synchronized void increaseCapacity(int newCapacity) {
    newCapacity = Math.min(newCapacity, maxPoints); // do not let the number of data points exceed maxPoints
    int newIndex = Math.min(index, (3*newCapacity)/4); // drop 1/4 of the old data if the capacity is no longer increasing
    double[] tempx = xpoints;
    xpoints = new double[newCapacity];
    System.arraycopy(tempx, index-newIndex, xpoints, 0, newIndex);
    double[] tempy = ypoints;
    ypoints = new double[newCapacity];
    System.arraycopy(tempy, index-newIndex, ypoints, 0, newIndex);
    index = newIndex;
    if(index>maxPoints/2) {
      recalculatePath();
    }
  }

  /**
   *  Reset the minimum and maximum values.
   */
  private void resetXYMinMax() {
    // changed by W. Christian
    xmax = -Double.MAX_VALUE;
    ymax = -Double.MAX_VALUE;
    xmin = Double.MAX_VALUE;
    ymin = Double.MAX_VALUE;
    for(int i = 0;i<index;i++) {
      if(Double.isNaN(xpoints[i])||Double.isInfinite(xpoints[i])||Double.isInfinite(ypoints[i])) {
        continue;
      }
      xmax = Math.max(xpoints[i], xmax);
      xmin = Math.min(xpoints[i], xmin);
      // changed by D.Brown
      if(!Double.isNaN(ypoints[i])) {
        ymax = Math.max(ypoints[i], ymax);
        ymin = Math.min(ypoints[i], ymin);
      }
    }
  }

  /**
   * Returns an array of valid points.
   * A point is valid if the ypoint for that index is not Double.NaN.
   *
   * @return valid points
   */
  private double[] getValidPoints(double[] pts) {
    // eliminate NaN values, if any
    int nans = 0;
    for (int i = 0; i < pts.length; i++) {
      if (nans > 0) {
        pts[i-nans] = pts[i];
      }
      if (Double.isNaN(ypoints[i])) {
        nans++;
      }
    }
    if (nans == 0) return pts;
    double[] temp = new double[index-nans];
    System.arraycopy(pts, 0, temp, 0, index-nans);
    return temp;
  }

  /**
   * ErrorBar for datapoints.
   */
  class ErrorBar implements Drawable {
    double x, y, delx, dely; // the position and uncertainty of the data point
    int tick = 3;

    ErrorBar(double _x, double _y, double _delx, double _dely) {
      x = _x;
      y = _y;
      delx = _delx;
      dely = _dely;
    }

    /**
    * Draws the error bars for a data point.
    *
    * @param panel
    * @param g
    */
    public void draw(DrawingPanel panel, Graphics g) {
      // changed by D.Brown
      if(Double.isNaN(y)) {
        return;
      }
      int xpix = panel.xToPix(x);
      int xpix1 = panel.xToPix(x-delx);
      int xpix2 = panel.xToPix(x+delx);
      int ypix = panel.yToPix(y);
      int ypix1 = panel.yToPix(y-dely);
      int ypix2 = panel.yToPix(y+dely);
      g.setColor(errorBarColor);
      g.drawLine(xpix1, ypix, xpix2, ypix);
      g.drawLine(xpix, ypix1, xpix, ypix2);
      g.drawLine(xpix1, ypix-tick, xpix1, ypix+tick);
      g.drawLine(xpix2, ypix-tick, xpix2, ypix+tick);
      g.drawLine(xpix-tick, ypix1, xpix+tick, ypix1);
      g.drawLine(xpix-tick, ypix2, xpix+tick, ypix2);
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
      Dataset data = (Dataset) obj;
      control.setValue("points", data.getPoints());
      // control.setValue("x_points", data.getXPoints());
      // control.setValue("y_points", data.getYPoints());
      control.setValue("marker_shape", data.getMarkerShape());
      control.setValue("marker_size", data.getMarkerSize());
      control.setValue("sorted", data.isSorted());
      control.setValue("connected", data.isConnected());
      control.setValue("name", data.name);
      control.setValue("x_name", data.xColumnName);
      control.setValue("y_name", data.yColumnName);
      control.setValue("line_color", data.lineColor);
      control.setValue("fill_color", data.fillColor);
      control.setValue("edge_color", data.edgeColor);
      control.setValue("errorbar_color", data.errorBarColor);
      control.setValue("datasetID", data.datasetID);
    }

    public Object createObject(XMLControl control) {
      return new Dataset();
    }

    public Object loadObject(XMLControl control, Object obj) {
      Dataset data = (Dataset) obj;
      double[][] points = (double[][]) control.getObject("points");
      if(points!=null&&points.length>0&&points[0]!=null) {
        data.clear();
        for(int i = 0;i<points.length;i++) {
          data.append(points[i][0], points[i][1]);
        }
      }
      // for backward compatibility
      double[] xPoints = (double[]) control.getObject("x_points");
      double[] yPoints = (double[]) control.getObject("y_points");
      if(xPoints!=null&&yPoints!=null) {
        data.clear();
        data.append(xPoints, yPoints);
      }
      if(control.getPropertyNames().contains("marker_shape")) {
        data.setMarkerShape(control.getInt("marker_shape"));
      }
      if(control.getPropertyNames().contains("marker_size")) {
        data.setMarkerSize(control.getInt("marker_size"));
      }
      data.setSorted(control.getBoolean("sorted"));
      data.setConnected(control.getBoolean("connected"));
      data.name = control.getString("name");
      data.xColumnName = control.getString("x_name");
      data.yColumnName = control.getString("y_name");
      Color color = (Color) control.getObject("line_color");
      if(color!=null) {
        data.lineColor = color;
      }
      color = (Color) control.getObject("fill_color");
      if(color!=null) {
        data.fillColor = color;
      }
      color = (Color) control.getObject("edge_color");
      if(color!=null) {
        data.edgeColor = color;
      }
      color = (Color) control.getObject("errorbar_color");
      if(color!=null) {
        data.errorBarColor = color;
      }
      data.setID(control.getInt("datasetID"));
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
