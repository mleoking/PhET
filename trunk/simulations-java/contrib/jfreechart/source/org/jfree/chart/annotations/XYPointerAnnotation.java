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
 * ------------------------
 * XYPointerAnnotation.java
 * ------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 21-May-2003 : Version 1 (DG);
 * 10-Jun-2003 : Changed BoundsAnchor to TextAnchor (DG);
 * 02-Jul-2003 : Added accessor methods and simplified constructor (DG);
 * 19-Aug-2003 : Implemented Cloneable (DG);
 * 13-Oct-2003 : Fixed bug where arrow paint is not set correctly (DG);
 * 21-Jan-2004 : Update for renamed method in ValueAxis (DG);
 * 29-Sep-2004 : Changes to draw() method signature (DG);
 *
 */

package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * An arrow and label that can be placed on an 
 * {@link org.jfree.chart.plot.XYPlot}.  The arrow is drawn at a user-definable 
 * angle so that it points towards the (x, y) location for the annotation.  
 * <p>
 * The arrow length (and its offset from the (x, y) location) is controlled by 
 * the tip radius and the base radius attributes.  Imagine two circles around 
 * the (x, y) coordinate: the inner circle defined by the tip radius, and the 
 * outer circle defined by the base radius.  Now, draw the arrow starting at 
 * some point on the outer circle (the point is determined by the angle), with 
 * the arrow tip being drawn at a corresponding point on the inner circle.
 * <p>
 * See the <code>MarkerDemo1.java</code> source file in the JFreeChart 
 * distribution for an example.
 *
 */

public class XYPointerAnnotation extends XYTextAnnotation 
                                 implements Cloneable, PublicCloneable, 
                                            Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -4031161445009858551L;
    
    /** The default tip radius (in Java2D units). */
    public static final double DEFAULT_TIP_RADIUS = 10.0;
    
    /** The default base radius (in Java2D units). */
    public static final double DEFAULT_BASE_RADIUS = 30.0;
    
    /** The default label offset (in Java2D units). */
    public static final double DEFAULT_LABEL_OFFSET = 3.0;
    
    /** The default arrow length (in Java2D units). */
    public static final double DEFAULT_ARROW_LENGTH = 5.0;

    /** The default arrow width (in Java2D units). */
    public static final double DEFAULT_ARROW_WIDTH = 3.0;
    
    /** The angle of the arrow's line (in radians). */
    private double angle;

    /** 
     * The radius from the (x, y) point to the tip of the arrow (in Java2D 
     * units). 
     */
    private double tipRadius;

    /** 
     * The radius from the (x, y) point to the start of the arrow line (in 
     * Java2D units). 
     */
    private double baseRadius;

    /** The length of the arrow head (in Java2D units). */
    private double arrowLength;

    /** The arrow width (in Java2D units, per side). */
    private double arrowWidth;
    
    /** The arrow stroke. */
    private transient Stroke arrowStroke;

    /** The arrow paint. */
    private transient Paint arrowPaint;
    
    /** The radius from the base point to the anchor point for the label. */
    private double labelOffset;

    /**
     * Creates a new label and arrow annotation.
     *
     * @param label  the label (<code>null</code> permitted).
     * @param x  the x-coordinate (measured against the chart's domain axis).
     * @param y  the y-coordinate (measured against the chart's range axis).
     * @param angle  the angle of the arrow's line (in radians).
     */
    public XYPointerAnnotation(String label, double x, double y, double angle) {

        super(label, x, y);
        this.angle = angle;
        this.tipRadius = DEFAULT_TIP_RADIUS;
        this.baseRadius = DEFAULT_BASE_RADIUS;
        this.arrowLength = DEFAULT_ARROW_LENGTH;
        this.arrowWidth = DEFAULT_ARROW_WIDTH;
        this.labelOffset = DEFAULT_LABEL_OFFSET;
        this.arrowStroke = new BasicStroke(1.0f);
        this.arrowPaint = Color.black;

    }
    
    /**
     * Returns the angle of the arrow.
     * 
     * @return The angle (in radians).
     */
    public double getAngle() {
        return this.angle;
    }
    
    /**
     * Sets the angle of the arrow.
     * 
     * @param angle  the angle (in radians).
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }
    
    /**
     * Returns the tip radius.
     * 
     * @return The tip radius (in Java2D units).
     */
    public double getTipRadius() {
        return this.tipRadius;
    }
    
    /**
     * Sets the tip radius.
     * 
     * @param radius  the radius (in Java2D units).
     */
    public void setTipRadius(double radius) {
        this.tipRadius = radius;
    }
    
    /**
     * Sets the base radius.
     * 
     * @return The base radius (in Java2D units).
     */
    public double getBaseRadius() {
        return this.baseRadius;
    }
    
    /**
     * Sets the base radius.
     * 
     * @param radius  the radius (in Java2D units).
     */
    public void setBaseRadius(double radius) {
        this.baseRadius = radius;
    }

    /**
     * Sets the label offset.
     * 
     * @return The label offset (in Java2D units).
     */
    public double getLabelOffset() {
        return this.labelOffset;
    }
    
    /**
     * Sets the label offset (from the arrow base, continuing in a straight 
     * line, in Java2D units).
     * 
     * @param offset  the offset (in Java2D units).
     */
    public void setLabelOffset(double offset) {
        this.labelOffset = offset;
    }
    
    /**
     * Returns the arrow length.
     * 
     * @return The arrow length.
     */
    public double getArrowLength() {
        return this.arrowLength;
    }
    
    /**
     * Sets the arrow length.
     * 
     * @param length  the length.
     */
    public void setArrowLength(double length) {
        this.arrowLength = length;
    }

    /**
     * Returns the arrow width.
     * 
     * @return The arrow width (in Java2D units).
     */
    public double getArrowWidth() {
        return this.arrowWidth;
    }
    
    /**
     * Sets the arrow width.
     * 
     * @param width  the width (in Java2D units).
     */
    public void setArrowWidth(double width) {
        this.arrowWidth = width;
    }
    
    /** 
     * Returns the stroke used to draw the arrow line.
     * 
     * @return The arrow stroke (never <code>null</code>).
     */
    public Stroke getArrowStroke() {
        return this.arrowStroke;
    }

    /** 
     * Sets the stroke used to draw the arrow line.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setArrowStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' not permitted.");
        }
        this.arrowStroke = stroke;
    }
    
    /**
     * Sets the paint used for the arrow.
     * 
     * @return The arrow paint (never <code>null</code>).
     */
    public Paint getArrowPaint() {
        return this.arrowPaint;
    }
    
    /**
     * Sets the paint used for the arrow.
     * 
     * @param paint  the arrow paint (<code>null</code> not permitted).
     */
    public void setArrowPaint(Paint paint) {
        this.arrowPaint = paint;
    }
    
    /**
     * Draws the annotation.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param rendererIndex  the renderer index.
     * @param info  the plot rendering info.
     */
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis, 
                     int rendererIndex,
                     PlotRenderingInfo info) {

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
            plot.getDomainAxisLocation(), orientation
        );
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
            plot.getRangeAxisLocation(), orientation
        );
        double j2DX = domainAxis.valueToJava2D(getX(), dataArea, domainEdge);
        double j2DY = rangeAxis.valueToJava2D(getY(), dataArea, rangeEdge);

        double startX = j2DX + Math.cos(this.angle) * this.baseRadius;
        double startY = j2DY + Math.sin(this.angle) * this.baseRadius;

        double endX = j2DX + Math.cos(this.angle) * this.tipRadius;
        double endY = j2DY + Math.sin(this.angle) * this.tipRadius;

        double arrowBaseX = endX + Math.cos(this.angle) * this.arrowLength;
        double arrowBaseY = endY + Math.sin(this.angle) * this.arrowLength;

        double arrowLeftX = arrowBaseX 
            + Math.cos(this.angle + Math.PI / 2.0) * this.arrowWidth;
        double arrowLeftY = arrowBaseY 
            + Math.sin(this.angle + Math.PI / 2.0) * this.arrowWidth;

        double arrowRightX = arrowBaseX 
            - Math.cos(this.angle + Math.PI / 2.0) * this.arrowWidth;
        double arrowRightY = arrowBaseY 
            - Math.sin(this.angle + Math.PI / 2.0) * this.arrowWidth;

        GeneralPath arrow = new GeneralPath();
        arrow.moveTo((float) endX, (float) endY);
        arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
        arrow.lineTo((float) arrowRightX, (float) arrowRightY);
        arrow.closePath();

        g2.setStroke(this.arrowStroke);
        g2.setPaint(this.arrowPaint);
        Line2D line = new Line2D.Double(startX, startY, endX, endY);
        g2.draw(line);
        g2.fill(arrow);

        // draw the label
        g2.setFont(getFont());
        g2.setPaint(getPaint());
        double labelX = j2DX 
            + Math.cos(this.angle) * (this.baseRadius + this.labelOffset);
        double labelY = j2DY 
            + Math.sin(this.angle) * (this.baseRadius + this.labelOffset);
        Rectangle2D hotspot = TextUtilities.drawAlignedString(
            getText(), 
            g2, 
            (float) labelX, 
            (float) labelY,
            getTextAnchor()
        );

        String toolTip = getToolTipText();
        String url = getURL();
        if (toolTip != null || url != null) {
            addEntity(info, hotspot, rendererIndex, toolTip, url);
        }
        
    }
    
    /**
     * Tests this annotation for equality with an object.
     * 
     * @param object  the object to test against.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object object) {
        
        if (object == null) {
            return false;
        }
        
        if (object == this) {
            return true;
        }
        
        if (object instanceof XYPointerAnnotation) {
        
            XYPointerAnnotation a = (XYPointerAnnotation) object;
            boolean b0 = (this.angle == a.angle);
            boolean b1 = (this.tipRadius == a.tipRadius);
            boolean b2 = (this.baseRadius == a.baseRadius);
            boolean b3 = (this.arrowLength == a.arrowLength);
            boolean b4 = (this.arrowWidth == a.arrowWidth);
            boolean b5 = (this.arrowPaint.equals(a.arrowPaint));
            boolean b6 = (this.arrowStroke.equals(a.arrowStroke));
            boolean b7 = (this.labelOffset == a.labelOffset);
            return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7;
        }
       
        return false;
        
    }
    
    /**
     * Returns a clone of the annotation.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the annotation can't be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.arrowPaint, stream);
        SerialUtilities.writeStroke(this.arrowStroke, stream);
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
        this.arrowPaint = SerialUtilities.readPaint(stream);
        this.arrowStroke = SerialUtilities.readStroke(stream);
    }

}
