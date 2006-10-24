/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.frames;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;

/**
 * HistogramFrame displays a histogram using a dedicated Histogram object.
 *
 * @author W. Christian
 * @version 1.0
 */
public class HistogramFrame extends DrawingFrame {
  protected Histogram histogram = new Histogram();
  protected DataTable dataTable = new DataTable();
  protected DataTableFrame tableFrame;

  /**
   * A DrawingFrame with a Histogram as its drawable.
   *
   * @param xlabel String
   * @param ylabel String
   * @param title String
   */
  public HistogramFrame(String xlabel, String ylabel, String title) {
    super(new PlottingPanel(xlabel, ylabel, null));
    // histogram.setDiscrete(false) ;
    drawingPanel.addDrawable(histogram);
    setTitle(title);
    dataTable.add(histogram);
    setAnimated(true);
    setAutoclear(true);
    addMenuItems();
  }

  /**
 * Adds Views menu items on the menu bar.
 */
  protected void addMenuItems() {
    JMenuBar menuBar = getJMenuBar();
    if(menuBar==null) {
      return;
    }
    JMenu helpMenu = this.removeMenu("Help");
    JMenu menu = getMenu("Views");
    if(menu==null) {
      menu = new JMenu("Views");
      menuBar.add(menu);
      menuBar.validate();
    } else { // add a separator if tools already exists
      menu.addSeparator();
    }
    menuBar.add(helpMenu);
    // add a menu item to show the data table
    JMenuItem tableItem = new JMenuItem("Data Table");
    tableItem.setAccelerator(KeyStroke.getKeyStroke('T', MENU_SHORTCUT_KEY_MASK));
    ActionListener tableListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showDataTable(true);
      }
    };
    tableItem.addActionListener(tableListener);
    menu.add(tableItem);
    // add to popup menu
    JMenuItem item = new JMenuItem("Data Table");
    item.addActionListener(tableListener);
    if(drawingPanel!=null&&drawingPanel.getPopupMenu()!=null) {
      drawingPanel.getPopupMenu().add(item);
    }
  }

  /**
   * Removes drawable objects added by the user from this frame.
   */
  public void clearDrawables() {
    drawingPanel.clear();                // removes all drawables
    drawingPanel.addDrawable(histogram); // puts complex dataset back into panel
    showDataTable(false);
  }

  /**
   * Gets Drawable objects added by the user to this frame.
   *
   * @return the list
   */
  public synchronized ArrayList getDrawables() {
    ArrayList list = super.getDrawables();
    list.remove(histogram);
    return list;
  }

  /**
   * Gets Drawable objects added by the user of an assignable type. The list contains
   * objects that are assignable from the class or interface.
   *
   * @param c the type of Drawable object
   *
   * @return the cloned list
   *
   * @see #getObjectOfClass(Class c)
   */
  public synchronized ArrayList getDrawables(Class c) {
    ArrayList list = super.getDrawables(c);
    list.remove(histogram);
    return list;
  }

  /**
   * Clears all the data stored.
   */
  public void clearData() {
    histogram.clear();
    dataTable.refreshTable();
    drawingPanel.invalidateImage();
  }

  /**
   * Appends a data point to the histogram.
   * @param v  data point
   */
  public void append(double v) {
    histogram.append(v);
    // this may be slow if the table is large
    if(tableFrame!=null&&tableFrame.isShowing()) {
      dataTable.refreshTable();
    }
  }

  /**
   * Histogram uses logarithmic scale (true/false)
   */
  public void setLogScale(boolean b) {
    histogram.logScale = b;
  }

  /**
   *  Sets the width of a bin.
   *
   * @param  width
   */
  public void setBinWidth(double width) {
    histogram.setBinWidth(width);
  }

  /**
   * Normalizes the occurences in this histogram to one (true/false).
   */
  public void setNormalizedToOne(boolean b) {
    histogram.setNormalizedToOne(b);
    histogram.adjustForWidth = b;
  }

  /**
   * Makes the x axis positive by default.
   */
  public void positiveX() {
    boolean b = drawingPanel.isAutoscaleX();
    drawingPanel.setPreferredMinMaxX(0, drawingPanel.getPreferredXMax());
    drawingPanel.setAutoscaleX(b);
  }

  /**
   * Shows or hides the data table.
   *
   * @param show boolean
   */
  public synchronized void showDataTable(boolean show) {
    if(show) {
      if(tableFrame==null) {
        tableFrame = new DataTableFrame(getTitle()+" Data", dataTable);
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      }
      dataTable.refreshTable();
      dataTable.sort(0);
      tableFrame.setVisible(true);
    } else {
      tableFrame.setVisible(false);
      tableFrame.dispose();
      tableFrame = null;
    }
  }

  public static XML.ObjectLoader getLoader() {
    return new HistogramFrameLoader();
  }

  static protected class HistogramFrameLoader extends DrawingFrame.DrawingFrameLoader {

    /**
    * Creates a PlotFame.
    *
    * @param control XMLControl
    * @return Object
    */
    public Object createObject(XMLControl control) {
      HistogramFrame frame = new HistogramFrame("x", "y", "Histogram Frame");
      return frame;
    }

    /**
     * Loads the object with data from the control.
     *
     * @param control XMLControl
     * @param obj Object
     * @return Object
     */
    public Object loadObject(XMLControl control, Object obj) {
      super.loadObject(control, obj);
      HistogramFrame frame = ((HistogramFrame) obj);
      ArrayList list = frame.getObjectOfClass(Histogram.class);
      if(list.size()>0) { // assume the first Histogram belongs to this frame
        frame.histogram = (Histogram) list.get(0);
        frame.histogram.clear();
        frame.dataTable.add(frame.histogram);
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
