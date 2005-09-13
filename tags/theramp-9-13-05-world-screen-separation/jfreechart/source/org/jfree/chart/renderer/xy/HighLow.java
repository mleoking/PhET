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
 * ------------
 * HighLow.java
 * ------------
 * (C) Copyright 2000-2004, by Andrzej Porebski and Contributors.
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 17-Nov-2001 : Renamed HiLow --> HighLow (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package org.jfree.chart.renderer.xy;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Represents one point in the high/low/open/close plot.
 * <P>
 * All the coordinates in this class are in Java2D space.
 *
 * @author Andrzej Porebski
 */
public class HighLow {

    /** Useful constant for open/close value types. */
    public static final int OPEN = 0;

    /** Useful constant for open/close value types. */
    public static final int CLOSE = 1;

    /** The position of the line. */
    private Line2D line;

    /** The bounds. */
    private Rectangle2D bounds;

    /** The open value. */
    private double open;

    /** The close value. */
    private double close;

    /** The pen/brush used to draw the lines. */
    private Stroke stroke;

    /** The color used to draw the lines. */
    private Paint paint;

    /** The tick size. */
    private double tickSize = 2;

    /**
     * Constructs a high-low item, with default values for the open/close and
     * colors.
     *
     * @param x  the x value.
     * @param high  the high value.
     * @param low  the low value.
     */
    public HighLow(double x, double high, double low) {
        this(x, high, low, high, low, new BasicStroke(), Color.blue);
    }

    /**
     * Constructs a high-low item, with default values for the colors.
     *
     * @param x  the x value.
     * @param high  the high value.
     * @param low  the low value.
     * @param open  the open value.
     * @param close  the close value.
     */
    public HighLow(double x, double high, double low, double open, 
                   double close) {
        this(x, high, low, open, close, new BasicStroke(), Color.blue);
    }

    /**
     * Constructs a high-low item.
     *
     * @param x  the x value.
     * @param high  the high value.
     * @param low  the low value.
     * @param open  the open value.
     * @param close  the close value.
     * @param stroke  the stroke.
     * @param paint  the paint.
     */
    public HighLow(double x, double high, double low, double open, double close,
                   Stroke stroke, Paint paint) {

        this.line = new Line2D.Double(x, high, x, low);
        this.bounds =  new Rectangle2D.Double(x - this.tickSize, high,
                                              2 * this.tickSize, low - high);
        this.open = open;
        this.close = close;
        this.stroke = stroke;
        this.paint = paint;

    }

    /**
     * Sets the width of the open/close tick.
     *
     * @param newSize  the new tick size.
     */
    public void setTickSize(double newSize) {
        this.tickSize = newSize;
    }

    /**
     * Returns the width of the open/close tick.
     *
     * @return The width of the open/close tick.
     */
    public double getTickSize() {
        return this.tickSize;
    }

    /**
     * Returns the line.
     *
     * @return The line.
     */
    public Line2D getLine() {
        return this.line;
    }

    /**
     * Returns the bounds.
     *
     * @return The bounds.
     */
    public Rectangle2D getBounds() {
        return this.bounds;
    }

    /**
     * Returns either OPEN or CLOSE value depending on the valueType.
     *
     * @param valueType  which value <code>{OPEN|CLOSE}</code>.
     *
     * @return The open value for valueType <code>OPEN</code>, the close value
     *      otherwise.
     */
    public double getValue(int valueType) {
        if (valueType == OPEN) {
            return this.open;
        }
        else {
            return this.close;
        }
    }

    /**
     * Sets either OPEN or Close value depending on the valueType.
     *
     * @param type  the value type (OPEN or CLOSE).
     * @param value  the new value.
     */
    public void setValue(int type, double value) {
        if (type == OPEN) {
            this.open = value;
        }
        else {
            this.close = value;
        }
    }

    /**
     * Returns the line for open tick.
     *
     * @return The line for open tick.
     */
    public Line2D getOpenTickLine() {
        return getTickLine(
            getLine().getX1(), getValue(OPEN), (-1) * getTickSize()
        );
    }

    /**
     * Returns the line for close tick
     *
     * @return The line for close tick.
     */
    public Line2D getCloseTickLine() {
        return getTickLine(getLine().getX1(), getValue(CLOSE), getTickSize());
    }

    /**
     * Helper to get the tickLine for the OPEN/CLOSE value.
     *
     * @param x  the X coordinate of the start point of the tick line.
     * @param value  the OPEN or the CLOSE value.
     * @param width  the width of the tickLine.
     *
     * @return A tickLine for the OPEN or the CLOSE value.
     */
    private Line2D getTickLine(double x, double value, double width) {
        return new Line2D.Double(x, value, x + width, value);
    }

    /**
     * Returns the Stroke object used to draw the line.
     *
     * @return The Stroke object used to draw the line.
     */
    public Stroke getStroke() {
        return this.stroke;
    }

    /**
     * Returns the Paint object used to color the line.
     *
     * @return The Paint object used to color the line.
     */
    public Paint getPaint() {
        return this.paint;
    }

}
