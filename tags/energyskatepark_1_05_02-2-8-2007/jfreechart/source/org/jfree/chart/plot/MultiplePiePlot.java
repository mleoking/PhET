/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * MultiplePiePlot.java
 * --------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 29-Jan-2004 : Version 1 (DG);
 * 31-Mar-2004 : Added setPieIndex() call during drawing (DG);
 * 20-Apr-2005 : Small change for update to LegendItem constructors (DG);
 * 05-May-2005 : Updated draw() method parameters (DG);
 * 16-Jun-2005 : Added get/setDataset() and equals() methods (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.CategoryToPieDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.TableOrder;

/**
 * A plot that displays multiple pie plots using data from a 
 * {@link CategoryDataset}.
 */
public class MultiplePiePlot extends Plot implements Cloneable, Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = -355377800470807389L;
    
    /** The chart object that draws the individual pie charts. */
    private JFreeChart pieChart;
    
    /** The dataset. */
    private CategoryDataset dataset;
    
    /** The data extract order (by row or by column). */
    private TableOrder dataExtractOrder;
    
    /** The pie section limit percentage. */
    private double limit = 0.0;
    
    /**
     * Creates a new plot with no data.
     */
    public MultiplePiePlot() {
        this(null);
    }
    
    /**
     * Creates a new plot.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public MultiplePiePlot(CategoryDataset dataset) {
        super();
        this.dataset = dataset;
        PiePlot piePlot = new PiePlot(null);
        this.pieChart = new JFreeChart(piePlot);
        this.pieChart.removeLegend();
        this.dataExtractOrder = TableOrder.BY_COLUMN;
        this.pieChart.setBackgroundPaint(null);
        TextTitle seriesTitle = new TextTitle(
            "Series Title", new Font("SansSerif", Font.BOLD, 12)
        );
        seriesTitle.setPosition(RectangleEdge.BOTTOM);
        this.pieChart.setTitle(seriesTitle);
    }
    
    /**
     * Returns the dataset used by the plot.
     * 
     * @return The dataset (possibly <code>null</code>).
     */
    public CategoryDataset getDataset() {
        return this.dataset;   
    }
    
    /**
     * Sets the dataset used by the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(CategoryDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of 
        // change listeners...
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }

        // send a dataset change event to self to trigger plot change event
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }
    
    /**
     * Returns the pie chart that is used to draw the individual pie plots.
     * 
     * @return The pie chart.
     */
    public JFreeChart getPieChart() {
        return this.pieChart;
    }
    
    /**
     * Sets the chart that is used to draw the individual pie plots.
     * 
     * @param pieChart  the pie chart.
     */
    public void setPieChart(JFreeChart pieChart) {
        this.pieChart = pieChart;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the data extract order (by row or by column).
     * 
     * @return The data extract order (never <code>null</code>).
     */
    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }
    
    /**
     * Sets the data extract order (by row or by column) and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param order  the order (<code>null</code> not permitted).
     */
    public void setDataExtractOrder(TableOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument");
        }
        this.dataExtractOrder = order;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the limit (as a percentage) below which small pie sections are 
     * aggregated.
     * 
     * @return The limit percentage.
     */
    public double getLimit() {
        return this.limit;
    }
    
    /**
     * Sets the limit below which pie sections are aggregated.  
     * Set this to 0.0 if you don't want any aggregation to occur.
     * 
     * @param limit  the limit percent.
     */
    public void setLimit(double limit) {
        this.limit = limit;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns a short string describing the type of plot.
     *
     * @return The plot type.
     */
    public String getPlotType() {
        return "Multiple Pie Plot";  
         // TODO: need to fetch this from localised resources
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects info about the drawing.
     */
    public void draw(Graphics2D g2, 
                     Rectangle2D area,
                     Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {
        
       
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        drawBackground(g2, area);
        drawOutline(g2, area);
        
        // check that there is some data to display...
        if (DatasetUtilities.isEmptyOrNull(this.dataset)) {
            drawNoDataMessage(g2, area);
            return;
        }

        int pieCount = 0;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            pieCount = this.dataset.getRowCount();
        }
        else {
            pieCount = this.dataset.getColumnCount();
        }

        // the columns variable is always >= rows
        int displayCols = (int) Math.ceil(Math.sqrt(pieCount));
        int displayRows 
            = (int) Math.ceil((double) pieCount / (double) displayCols);

        // swap rows and columns to match plotArea shape
        if (displayCols > displayRows && area.getWidth() < area.getHeight()) {
            int temp = displayCols;
            displayCols = displayRows;
            displayRows = temp;
        }

        // int fontHeight 
        //     = g2.getFontMetrics(this.plotLabelFont).getHeight() * 2;
        int x = (int) area.getX();
        int y = (int) area.getY();
        int width = ((int) area.getWidth()) / displayCols;
        int height = ((int) area.getHeight()) / displayRows;
        int row = 0;
        int column = 0;
        int diff = (displayRows * displayCols) - pieCount;
        int xoffset = 0;
        Rectangle rect = new Rectangle();

        for (int pieIndex = 0; pieIndex < pieCount; pieIndex++) {
            rect.setBounds(
                x + xoffset + (width * column), y + (height * row), 
                width, height
            );

            String title = null;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                title = this.dataset.getRowKey(pieIndex).toString();
            }
            else {
                title = this.dataset.getColumnKey(pieIndex).toString();
            }
            this.pieChart.setTitle(title);
            
            PieDataset piedataset = null;
            PieDataset dd = new CategoryToPieDataset(
                this.dataset, this.dataExtractOrder, pieIndex
            );
            if (this.limit > 0.0) {
                piedataset = DatasetUtilities.createConsolidatedPieDataset(
                    dd, "Other", this.limit
                );
            }
            else {
                piedataset = dd;
            }
            PiePlot piePlot = (PiePlot) this.pieChart.getPlot();
            piePlot.setDataset(piedataset);
            piePlot.setPieIndex(pieIndex);
            ChartRenderingInfo subinfo = null;
            if (info != null) {
                subinfo = new ChartRenderingInfo();
            }
            this.pieChart.draw(g2, rect, subinfo);
            if (info != null) {
                info.getOwner().getEntityCollection().addAll(
                    subinfo.getEntityCollection()
                );
                info.addSubplotInfo(subinfo.getPlotInfo());
            }
            
            ++column;
            if (column == displayCols) {
                column = 0;
                ++row;

                if (row == displayRows - 1 && diff != 0) {
                    xoffset = (diff * width) / 2;
                }
            }
        }

    }
    
    /**
     * Returns a collection of legend items for the pie chart.
     *
     * @return The legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        if (this.dataset != null) {
            List keys = null;
      
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                keys = this.dataset.getColumnKeys();
            }
            else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
                keys = this.dataset.getRowKeys();
            }

            if (keys != null) {
                int section = 0;
                Iterator iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String label = iterator.next().toString();
                    String description = label;
                    PiePlot plot = (PiePlot) this.pieChart.getPlot();
                    Paint paint = plot.getSectionPaint(section);
                    Paint outlinePaint = plot.getSectionOutlinePaint(section);
                    Stroke outlineStroke 
                        = plot.getSectionOutlineStroke(section);
                    LegendItem item = new LegendItem(label, description, 
                            null, null, Plot.DEFAULT_LEGEND_ITEM_CIRCLE, 
                            paint, outlineStroke, outlinePaint);

                    result.add(item);
                    section++;
                }
            }
        }
        return result;
    }
    
    /**
     * Tests this plot for equality with an arbitrary object.  Note that the 
     * plot's dataset is not considered in the equality test.
     * 
     * @param obj  the object (<code>null</code> permitted).
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof MultiplePiePlot)) {
            return false;   
        }
        MultiplePiePlot that = (MultiplePiePlot) obj;
        if (this.dataExtractOrder != that.dataExtractOrder) {
            return false;   
        }
        if (this.limit != that.limit) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.pieChart, that.pieChart)) {
            return false;   
        }
        if (!super.equals(obj)) {
            return false;   
        }
        return true;
    }
    
}
