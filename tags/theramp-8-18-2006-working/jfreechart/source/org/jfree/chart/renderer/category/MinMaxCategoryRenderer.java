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
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------------
 * MinMaxCategoryRenderer.java
 * ---------------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  Tomer Peretz;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Christian W. Zuckschwerdt;
 *                   Nicolas Brodu (for Astrium and EADS Corporate Research 
 *                   Center);
 *
 * $Id$
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (TP);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and 
 *               CategoryToolTipGenerator interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem() 
 *               method (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 08-Sep-2003 : Implemented Serializable (NB);
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.Icon;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.io.SerialUtilities;

/**
 * Renderer for drawing min max plot. This renderer draws all the series under 
 * the same category in the same x position using <code>objectIcon</code> and 
 * a line from the maximum value to the minimum value.
 * <p>
 * For use with the {@link org.jfree.chart.plot.CategoryPlot} class.
 *
 * @author Tomer Peretz
 */
public class MinMaxCategoryRenderer extends AbstractCategoryItemRenderer {

    /** For serialization. */
    private static final long serialVersionUID = 2935615937671064911L;
    
    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean plotLines = false;

    /** 
     * The paint of the line between the minimum value and the maximum value.
     */
    private transient Paint groupPaint = Color.black;

    /** 
     * The stroke of the line between the minimum value and the maximum value.
     */
    private transient Stroke groupStroke = new BasicStroke(1.0f);

    /** The icon used to indicate the minimum value.*/
    private transient Icon minIcon = getIcon(
        new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, Color.black
    );

    /** The icon used to indicate the maximum value.*/
    private transient Icon maxIcon = getIcon(
        new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, Color.black
    );

    /** The icon used to indicate the values.*/
    private transient Icon objectIcon = getIcon(
        new Line2D.Double(-4, 0, 4, 0), false, true
    );

    /** The last category. */
    private int lastCategory = -1;

    /** The minimum. */
    private double min;

    /** The maximum. */
    private double max;

    /** The minimum number. */
    private Number minValue;

    /** The maximum number. */
    private Number maxValue;

    /**
     * Default constructor.
     */
    public MinMaxCategoryRenderer () {
        super();
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem (Graphics2D g2,
                          CategoryItemRendererState state,
                          Rectangle2D dataArea,
                          CategoryPlot plot,
                          CategoryAxis domainAxis,
                          ValueAxis rangeAxis,
                          CategoryDataset dataset,
                          int row,
                          int column,
                          int pass) {

        // first check the number we are plotting...
        Number value = dataset.getValue(row, column);
        if (value != null) {
            // current data point...
            double x1 = domainAxis.getCategoryMiddle(
                column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
            );
            double y1 = rangeAxis.valueToJava2D(
                value.doubleValue(), dataArea, plot.getRangeAxisEdge()
            );
            g2.setPaint(getItemPaint(row, column));
            g2.setStroke(getItemStroke(row, column));
            Shape shape = null;
            shape = new Rectangle2D.Double(x1 - 4, y1 - 4, 8.0, 8.0);
            this.objectIcon.paintIcon(null, g2, (int) x1, (int) y1);
            if (this.lastCategory == column) {
                if (this.minValue.doubleValue() > value.doubleValue()) {
                    this.min = y1;
                    this.minValue = value;
                }
                if (this.maxValue.doubleValue() < value.doubleValue()) {
                    this.max = y1;
                    this.maxValue = value;
                }
                if (dataset.getRowCount() - 1 == row) {
                    g2.setPaint(this.groupPaint);
                    g2.setStroke(this.groupStroke);
                    g2.draw(new Line2D.Double(x1, this.min, x1, this.max));
                    this.minIcon.paintIcon(null, g2, (int) x1, (int) this.min);
                    this.maxIcon.paintIcon(null, g2, (int) x1, (int) this.max);
//                    if (isItemLabelVisible(row, column)) {
//                        NumberFormat formatter = getValueLabelFormatter();
//                        Font labelFont = getValueLabelFont();
//                        g2.setFont(labelFont);
//                        Paint paint = getValueLabelPaint();
//                        g2.setPaint(paint);
//                        boolean rotate = getVerticalValueLabels();
//                        drawLabel(
//                            g2, formatter.format(minValue), x1, min,
//                            labelFont, rotate, LineAndShapeRenderer.BOTTOM
//                        );
//                        drawLabel(g2, formatter.format(maxValue), x1, max,
//                                labelFont, rotate, LineAndShapeRenderer.TOP);
//                    }
                }
            }
            else {
                this.lastCategory = column;
                this.min = y1;
                this.max = y1;
                this.minValue = value;
                this.maxValue = value;
            }
            // connect to the previous point
            if (this.plotLines) {
                if (column != 0) {
                    Number previousValue = dataset.getValue(row, column - 1);
                    if (previousValue != null) {
                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = domainAxis.getCategoryStart(
                            column - 1, getColumnCount(), dataArea,
                            plot.getDomainAxisEdge()
                        );
                        double y0 = rangeAxis.valueToJava2D(
                            previous, dataArea, plot.getRangeAxisEdge()
                        );
                        g2.setPaint(getItemPaint(row, column));
                        g2.setStroke(getItemStroke(row, column));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }
                }
            }

            // collect entity and tool tip information...
            if (state.getInfo() != null) {
                EntityCollection entities 
                    = state.getInfo().getOwner().getEntityCollection();
                if (entities != null && shape != null) {
                    String tip = null;
                    CategoryToolTipGenerator tipster 
                        = getToolTipGenerator(row, column);
                    if (tipster != null) {
                        tip = tipster.generateToolTip(dataset, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(
                        shape, tip, null, dataset, row, 
                        dataset.getColumnKey(column), column);
                    entities.add(entity);
                }
            }
        }
    }

    /**
     * Sets whether or not lines are drawn between category points.
     *
     * @param drawLines  if true, then line will be drawn between sequenced 
     *                   categories.
     */
    public void setDrawLines (boolean drawLines) {
        this.plotLines = drawLines;
    }

    /**
     * Gets whether or not lines are drawn between category points.
     *
     * @return boolean true if line will be drawn between sequenced categories,
     *         otherwise false.
     */
    public boolean isDrawLines () {
        return this.plotLines;
    }

    /**
     * Sets the paint of the line between the minimum value and the maximum 
     * value.
     *
     * @param groupPaint  the new paint.
     */
    public void setGroupPaint (Paint groupPaint) {
        this.groupPaint = groupPaint;
    }

    /**
     * Gets the paint of the line between the minimum value and the maximum 
     * value.
     *
     * @return The paint.
     */
    public Paint getGroupPaint () {
        return this.groupPaint;
    }

    /**
     * Sets the stroke of the line between the minimum value and the maximum 
     * value.
     *
     * @param groupStroke The new stroke
     */
    public void setGroupStroke (Stroke groupStroke) {
        this.groupStroke = groupStroke;
    }

    /**
     * Gets the stroke of the line between the minimum value and the maximum 
     * value.
     *
     * @return Stroke The current stroke.
     */
    public Stroke getGroupStroke () {
        return this.groupStroke;
    }

    /**
     * Sets the icon used to indicate the values.
     *
     * @param objectIcon  the icon.
     */
    public void setObjectIcon (Icon objectIcon) {
        this.objectIcon = objectIcon;
    }

    /**
     * Gets the icon used to indicate the values.
     *
     * @return The icon.
     */
    public Icon getObjectIcon () {
        return this.objectIcon;
    }

    /**
     * Sets the icon used to indicate the maximum value.
     *
     * @param maxIcon  the max icon.
     */
    public void setMaxIcon (Icon maxIcon) {
        this.maxIcon = maxIcon;
    }

    /**
     * Gets the icon used to indicate the maximum value.
     *
     * @return The icon
     */
    public Icon getMaxIcone () {
        return this.maxIcon;
    }

    /**
     * Sets the icon used to indicate the minimum value.
     *
     * @param minIcon  the min icon.
     */
    public void setMinIcon (Icon minIcon) {
        this.minIcon = minIcon;
    }

    /**
     * Gets the icon used to indicate the minimum value.
     *
     * @return Icon
     */
    public Icon getMinIcon () {
        return this.minIcon;
    }

    /**
     * Returns an icon.
     *
     * @param shape  the shape.
     * @param fillPaint  the fill paint.
     * @param outlinePaint  the outline paint.
     *
     * @return The icon.
     */
    private Icon getIcon(Shape shape, final Paint fillPaint, 
                        final Paint outlinePaint) {

      final int width = shape.getBounds().width;
      final int height = shape.getBounds().height;
      final GeneralPath path = new GeneralPath(shape);
      return new Icon() {
          public void paintIcon(Component c, Graphics g, int x, int y) {
              Graphics2D g2 = (Graphics2D) g;
              path.transform(AffineTransform.getTranslateInstance(x, y));
              if (fillPaint != null) {
                  g2.setPaint(fillPaint);
                  g2.fill(path);
              }
              if (outlinePaint != null) {
                  g2.setPaint(outlinePaint);
                  g2.draw(path);
              }
              path.transform(AffineTransform.getTranslateInstance(-x, -y));
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }

      };
    }

    /**
     * Returns an icon.
     *
     * @param shape  the shape.
     * @param fill  the fill flag.
     * @param outline  the outline flag.
     *
     * @return The icon.
     */
    private Icon getIcon(Shape shape, final boolean fill, 
                         final boolean outline) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fill) {
                    g2.fill(path);
                }
                if (outline) {
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }

            public int getIconWidth() {
                return width;
            }

            public int getIconHeight() {
                return height;
            }
        };
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.groupStroke, stream);
        SerialUtilities.writePaint(this.groupPaint, stream);
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.groupStroke = SerialUtilities.readStroke(stream);
        this.groupPaint = SerialUtilities.readPaint(stream);
          
        this.minIcon = getIcon(
            new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, 
            Color.black
        );
        this.maxIcon = getIcon(
            new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, 
            Color.black
        );
        this.objectIcon = getIcon(new Line2D.Double(-4, 0, 4, 0), false, true);
    }
    
}
