/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.table.AbstractTableModel;

/**
 *  Histogram maps bin number to occurences. Histogram is Drawable and can be
 *  rendered on a DrawingPanel. Histogram also implements TableModel and can be
 *  displayed in a JTable. By default, bins consist of (notation: [ inclusive, )
 *  exclusive): ..., [-1,0), [0,1), [1,2), ...
 *
 * @author     Joshua Gould
 * @created    June 26, 2002
 * @version    1.0
 */
public class Histogram extends AbstractTableModel implements Measurable {

  /** draw point at top of bin */
  public final static short DRAW_POINT = 0;

  /** draw bin from y min to top of bin */
  public final static short DRAW_BIN = 1;

  /**
   * Should histogram be drawn on a log scale?  Default is false.
   */
  public boolean logScale = false;

  /**
   * Should the height be adjusted by bin width?  Default is false.
   */
  public boolean adjustForWidth = false;

  /** color of bins */
  protected Color binColor = Color.red;

  /** style for drawing bins */
  protected short binStyle = DRAW_BIN;

  /** maps bin number to occurences */
  HashMap bins;

  /** width of a bin */
  double binWidth = 1;

  /** offset of the bins */
  double binOffset = 0;

  /** false if the bins are continuous */
  boolean discrete = true;

  /** binNumber*binWidth + binOffset */
  double xmin;

  /** binNumber*binWidth + binWidth + binOffset */
  double xmax;

  /** min number of occurences for all bins */
  final int YMIN = 0;

  /** max number of occurences for all bins */
  double ymax;

  /** the name of the bin */
  String binColumnName;

  /** the name of the x column */
  String xColumnName;

  /** the name of the occurences */
  String yColumnName;

  /**
   *  bin number-occurences pairs in histogram, used for table model
   *  implementation
   */
  Map.Entry[] entries = new Map.Entry[0];

  /** whether the data has changed since the last time the entries were retrieved */
  boolean dataChanged;

  /** total occurences in histogram */
  double sum;

  /** whether occurences are normalized to one */
  boolean normalizedToOne = false;

  /**
   *  amount by which this histogram is shifted to the right, so that it peeks
   *  out from behind other histogramss.
   */
  double barOffset;

  /** Histogram contructor. */
  public Histogram() {
    binColumnName = "bin number";
    xColumnName = "x";
    yColumnName = "occurences";
    clear();
  }

  /**
   *  Reads a file and appends the data contained in the file to this Histogram.
   *  The format of the file is bins \t occurences. Lines beginning with # and
   *  empty lines are ignored.
   *
   * @param  inputPathName            A pathname string.
   * @exception  java.io.IOException  Description of the Exception
   */
  public void read(String inputPathName) throws java.io.IOException {
    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(inputPathName));
    String s = null;
    while((s = reader.readLine())!=null) {
      s = s.trim();
      if(s.equals("")||(s.charAt(0)=='#')) { // ignore empty lines and lines beginning with #
        continue;
      }
      try {
        java.util.StringTokenizer st = new java.util.StringTokenizer(s, "\t");
        int binNumber = Integer.parseInt(st.nextToken());
        double numberOfOccurences = Double.parseDouble(st.nextToken());
        Double priorOccurences = (Double) bins.get(new Integer(binNumber));
        if(priorOccurences==null) {                            // first occurence for this bin
          bins.put(new Integer(binNumber), new Double(numberOfOccurences));
        } else {
          numberOfOccurences += priorOccurences.doubleValue(); // increase occurences for bin by priorOccurences
          bins.put(new Integer(binNumber), new Double(numberOfOccurences));
        }
        ymax = Math.max(numberOfOccurences, ymax);
        xmin = Math.min(binNumber*binWidth+binOffset, xmin);
        xmax = Math.max(binNumber*binWidth+binWidth+binOffset, xmax);
      } catch(java.util.NoSuchElementException nsee) {
        nsee.printStackTrace();
      } catch(NumberFormatException nfe) {
        nfe.printStackTrace();
      }
    }
    dataChanged = true;
  }

  /**
   *  Creates a string representation of this Histogram. The bins are displayed
   *  in ascending order. The format of this string is bin number \t occurences.
   *  Each bin starts on a new line.
   *
   * @return    A String with the number of occurences for each bin.
   * @see       #toString
   */
  public String toSortedString() {
    Set keySet = bins.keySet();
    Object[] keys = keySet.toArray();
    Arrays.sort(keys);
    String s = "x\tx";
    StringBuffer buf = new StringBuffer(s.length()*keys.length);
    for(int i = 0;i<keys.length;i++) {
      Object key = keys[i];
      buf.append(key);
      buf.append("\t");
      buf.append(bins.get(keys[i]));
      buf.append("\n");
    }
    return buf.toString();
  }

  /**
   *  Creates a string representation of this Histogram. The format is bin
   *  number\t occurences. Each new bin starts on a new line.
   *
   * @return    A String with the number of occurences for each bin.
   */
  public String toString() {
    Set set = bins.keySet();
    Iterator keys = set.iterator();
    String s = "x\tx";
    StringBuffer buf = new StringBuffer(s.length()*set.size());
    while(keys.hasNext()) {
      Integer binNumber = (Integer) keys.next();
      Double occurences = (Double) bins.get(binNumber);
      buf.append(binNumber);
      buf.append("\t");
      buf.append(occurences);
      buf.append("\n");
    }
    return buf.toString();
  }

  /**
   *  Computes the hash code (bin number) for the specified value
   *
   * @param  value
   * @return        the hash code
   */
  public int hashCode(double value) {
    return(int) (Math.floor((value-binOffset)/binWidth));
  }

  /**
   *  Append a value with number of occurences to the Histogram.
   *
   * @param  value
   * @param  numberOfOccurences
   */
  public void append(double value, double numberOfOccurences) {
    sum += numberOfOccurences;
    int binNumber = hashCode(value);
    // Determine if there have previously been any occurrences for this bin
    Double occurences = (Double) bins.get(new Integer(binNumber));
    if(occurences==null) { // first occurence for this bin
      bins.put(new Integer(binNumber), new Double(numberOfOccurences));
    } else {
      // need to put Objects in HashMap, but can only add doubles
      numberOfOccurences += occurences.doubleValue(); // increase occurences for bin by numberOfOccurences
      bins.put(new Integer(binNumber), new Double(numberOfOccurences));
    }
    ymax = Math.max(numberOfOccurences, ymax);
    xmin = Math.min(binNumber*binWidth+binOffset, xmin);
    xmax = Math.max(binNumber*binWidth+binWidth+binOffset, xmax);
    dataChanged = true;
  }

  /**
   *  Appends a value with 1 occurence.
   *
   * @param  value
   */
  public void append(double value) {
    append(value, 1);
  }

  /**
   *  Appends values from an input file. Each value is separated by a \n
   *
   * @param  inputPathName    A pathname string.
   * @exception  IOException  Description of the Exception
   */
  public void append(String inputPathName) throws IOException {
    BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(inputPathName));
    String s = null;
    while((s = br.readLine())!=null) {
      s = s.trim();
      if(s.equals("")||(s.charAt(0)=='#')) {
        continue;
      }
      try {
        double d = Double.parseDouble(s);
        append(d, 1);
      } catch(NumberFormatException nfe) {
        nfe.printStackTrace();
      }
    }
  }

  /**
   *  Appends an array of values with 1 occurence.
   *
   * @param  values
   */
  public void append(double[] values) {
    for(int i = 0;i<values.length;i++) {
      append(values[i], 1);
    }
  }

  /**
   *  Draws this histogram in the drawing panel.
   *
   * @param  drawingPanel
   * @param  g
   */
  public void draw(DrawingPanel drawingPanel, Graphics g) {
    if(bins.size()==0) {
      return;
    }
    Shape oldClip = g.getClip();
    g.setColor(binColor);
    g.clipRect(0, 0, drawingPanel.getWidth(), drawingPanel.getHeight());
    for(Iterator keys = bins.keySet().iterator();keys.hasNext();) {
      Integer binNumber = (Integer) keys.next();
      double occurences = ((Double) (bins.get(binNumber))).doubleValue();
      if(normalizedToOne) {
        occurences /= sum;
      }
      if(binStyle==DRAW_BIN) {
        drawBin(drawingPanel, g, binNumber.intValue(), occurences);
      } else {
        drawPoint(drawingPanel, g, binNumber.intValue(), occurences);
      }
    }
    g.setClip(oldClip);
  }

  /** Clears all data from this histogram and resets min and max values. */
  public void clear() {
    bins = new HashMap();
    xmin = Integer.MAX_VALUE;
    xmax = Integer.MIN_VALUE;
    ymax = Integer.MIN_VALUE;
    sum = 0;
    dataChanged = true;
  }

  /**
   *  Gets an array of bin number-occurences pairs
   *
   * @return    The entries.
   */
  public Map.Entry[] entries() {
    updateEntries();
    return entries;
  }

  /**
   *  Sets the style for drawing this histogram. Options are DRAW_POINT, which
   *  draws a point at the top of the bin, and DRAW_BIN which draws the entire
   *  bin down to the x axis. Default is DRAW_BIN.
   *
   * @param  style
   */
  public void setBinStyle(short style) {
    binStyle = style;
  }

  /**
   *  Sets the discrete flag.
   *
   * @param  _discrete  <code>true<\code> if bins are discrete, <code>false<\code> if bins are continuous.
   */
  public void setDiscrete(boolean _discrete) {
    discrete = _discrete;
  }

  /**
   *  Sets the offset of the bins. Default is 0.
   *
   * @param  _binOffset
   */
  public void setBinOffset(double _binOffset) {
    binOffset = _binOffset;
  }

  /**
   *  Set the offset of the bars as a fraction of a bin width. The offset is the
   *  amount by which this histogram is shifted to the right, so that it peeks
   *  out from behind later histograms when displayed in a DrawingPanel.
   *
   * @param  _barOffset  The new barOffset value
   */
  public void setBarOffset(double _barOffset) {
    barOffset = _barOffset;
  }

  /**
   *  Sets the bin color.
   *
   * @param  _binColor
   */
  public void setBinColor(Color _binColor) {
    binColor = _binColor;
  }

  /**
   *  Sets the width of a bin.
   *
   * @param  _binWidth
   */
  public void setBinWidth(double _binWidth) {
    binWidth = _binWidth;
  }

  /**
   *  Sets the column names when rendering this histogram in a JTable.
   *
   * @param  _binColumnName
   * @param  _yColumnName
   */
  public void setXYColumnNames(String _binColumnName, String _yColumnName) {
    binColumnName = _binColumnName;
    yColumnName = _yColumnName;
  }

  /**
   *  Normalizes the occurences in this histogram to one.
   *
   * @param  b
   */
  public void setNormalizedToOne(boolean b) {
    normalizedToOne = b;
  }

  /**
   *  Gets the width of a bin.
   *
   * @return    The bin width.
   */
  public double getBinWidth() {
    return binWidth;
  }

  /**
   *  Gets the offset of the bins.
   *
   * @return    The bin offset.
   */
  public double getBinOffset() {
    return binOffset;
  }

  /**
   *  Gets the x world coordinate for the left hand side of this histogram.
   *
   * @return    xmin
   */
  public double getXMin() {
    return(discrete&&bins.size()>1) ? xmin-binWidth : xmin;
  }

  /**
   *  Gets the x world coordinate for the right hand side of this histogram.
   *
   * @return    xmax
   */
  public double getXMax() {
    return xmax;
  }

  /**
   *  Gets the y world coordinate for the bottom of this histogram.
   *
   * @return    minimum y value
   */
  public double getYMin() {
    return YMIN;
  }

  /**
   *  Gets the y world coordinate for the top of this histogram.
   *
   * @return    xmax
   */
  public double getYMax() {
    double max = (normalizedToOne ? ymax/sum : ymax);
    if(adjustForWidth) {
      max = max/getBinWidth();
    }
    if(logScale) {
      max = Math.log(max);
    }
    return max;
  }

  /**
   *  Gets the valid measure flag. The measure is valid if this histogram is not
   *  empty.
   *
   * @return    <code>true<\code> if measure is valid.
   *
   *
   */
  public boolean isMeasured() {
    return bins.size()>0;
  }

  /**
   *  Gets the name of the colummn for rendering in a JTable
   *
   * @param  column  the column whose value is to be queried
   * @return         the name
   */
  public String getColumnName(int column) {
    if(column==0) {
      return binColumnName;
    } else if(column==1) {
      return xColumnName;
    } else {
      return yColumnName;
    }
  }

  /**
   *  Gets the number of rows for rendering in a JTable.
   *
   * @return    the count
   */
  public int getRowCount() {
    return bins.size();
  }

  /**
   *  Gets the name of the colummn for rendering in a JTable
   *
   * @return    the name
   */
  public int getColumnCount() {
    return 3;
  }

  /**
   *  Gets a bin number or occurences for bin number for rendering in a JTable.
   *
   * @param  row     the row whose value is to be queried
   * @param  column  the column whose value is to be queried
   * @return         the datum
   */
  public Object getValueAt(int row, int column) {
    updateEntries();
    Map.Entry entry = entries[row];
    if(column==0) {
      return entry.getKey();
    }
    if(column==1) {
      return new Double(((Integer) entry.getKey()).doubleValue()*binWidth+binWidth/2.0+binOffset);
    } else {
      if(normalizedToOne) {
        Double d = (Double) entry.getValue();
        return new Double(d.doubleValue()/sum);
      } else {
        return entry.getValue();
      }
    }
  }

  /**
   *  Gets the type of object for JTable entry.
   *
   * @param  columnIndex  the column whose value is to be queried
   * @return              the class
   */
  public Class getColumnClass(int columnIndex) {
    return((columnIndex==0) ? Integer.class : Double.class);
  }

  /**
   *  Draws a point at the top of a bin.
   *
   * @param  drawingPanel
   * @param  g
   * @param  binNumber
   * @param  occurences
   */
  protected void drawPoint(DrawingPanel drawingPanel, Graphics g, int binNumber, double occurences) {
    int px = drawingPanel.xToPix(getLeftMostBinPosition(binNumber)); // leftmost position of bin
    int py = drawingPanel.yToPix(occurences);
    int pointRadius = 2;
    if(discrete) {
      g.fillRect(px-pointRadius, py-pointRadius, pointRadius*2, pointRadius*2);
    } else { // continous, draw entire bin
      int px2 = drawingPanel.xToPix(getRightMostBinPosition(binNumber));
      int pWidth = px2-px;
      g.fillRect(px, py, pWidth, pointRadius*2);
    }
  }

  /**
   *  Draws a filled bin.
   *
   * @param  drawingPanel
   * @param  g
   * @param  binNumber
   * @param  occurences
   */
  protected void drawBin(DrawingPanel drawingPanel, Graphics g, int binNumber, double occurences) {
    if(adjustForWidth) {
      occurences = occurences/getBinWidth();
    }
    if(logScale) {
      occurences = Math.max(0, Math.log(occurences));
    }
    int binlx = drawingPanel.xToPix(getLeftMostBinPosition(binNumber));
    if(discrete) {
      g.drawLine(binlx, drawingPanel.yToPix(YMIN), binlx, drawingPanel.yToPix(occurences));
    } else { // continous, draw entire bin
      int binrx = drawingPanel.xToPix(getRightMostBinPosition(binNumber));
      int pWidth = binrx-binlx;
      double pHeight = drawingPanel.getYPixPerUnit()*occurences;
      java.awt.geom.Rectangle2D.Double rect = new java.awt.geom.Rectangle2D.Double(binlx, drawingPanel.yToPix(occurences), pWidth, pHeight);
      Graphics2D g2 = (Graphics2D) g;
      g2.fill(rect);
    }
  }

  /**
   * Method getLeftMostBinPosition
   *
   * @param binNumber
   *
   * @return position
   */
  public double getLeftMostBinPosition(int binNumber) {
    return binNumber*binWidth+binOffset+binWidth*barOffset;
  }

  /**
   * Method getRightMostBinPosition
   *
   * @param binNumber
   *
   * @return position
   */
  public double getRightMostBinPosition(int binNumber) {
    return binNumber*binWidth+binWidth+binOffset+binWidth*barOffset;
  }

  /**
   *  Updates the bin number-occurences array if data has changed since the last
   *  update
   */
  private void updateEntries() {
    if(dataChanged) {
      entries = (Map.Entry[]) bins.entrySet().toArray(entries);
      dataChanged = false;
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
