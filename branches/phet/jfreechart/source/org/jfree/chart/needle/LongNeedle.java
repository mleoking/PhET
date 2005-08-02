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
 * ---------------
 * LongNeedle.java
 * ---------------
 * (C) Copyright 2002-2005, by the Australian Antarctic Division and 
 *                          Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes:
 * --------
 * 25-Sep-2002 : Version 1, contributed by Bryan Scott (DG);
 * 27-Mar-2003 : Implemented Serializable (DG);
 * 09-Sep-2003 : Added equals() method (DG);
 * 16-Mar-2004 : Implemented Rotation
 */

package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.io.Serializable;

/**
 * A needle that is represented by a long line.
 *
 * @author Bryan Scott
 */
public class LongNeedle extends MeterNeedle implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -4319985779783688159L;
    
    /**
     * Default constructor.
     */
    public LongNeedle() {
        super();
        setRotateY(0.8);
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, 
                              Point2D rotate, double angle) {

        GeneralPath shape1 = new GeneralPath();
        GeneralPath shape2 = new GeneralPath();
        GeneralPath shape3 = new GeneralPath();

        float minX = (float) plotArea.getMinX();
        float minY = (float) plotArea.getMinY();
        float maxX = (float) plotArea.getMaxX();
        float maxY = (float) plotArea.getMaxY();
        //float midX = (float) (minX + (plotArea.getWidth() * getRotateX()));
        //float midY = (float) (minY + (plotArea.getHeight() * getRotateY()));
        float midX = (float) (minX + (plotArea.getWidth() * 0.5));
        float midY = (float) (minY + (plotArea.getHeight() * 0.8));
        float y = maxY - (2 * (maxY - midY));
        if (y < minY) {
            y = minY;
        }
        shape1.moveTo(minX, midY);
        shape1.lineTo(midX, minY);
        shape1.lineTo(midX, y);
        shape1.closePath();

        shape2.moveTo(maxX, midY);
        shape2.lineTo(midX, minY);
        shape2.lineTo(midX, y);
        shape2.closePath();

        shape3.moveTo(minX, midY);
        shape3.lineTo(midX, maxY);
        shape3.lineTo(maxX, midY);
        shape3.lineTo(midX, y);
        shape3.closePath();

        Shape s1 = shape1;
        Shape s2 = shape2;
        Shape s3 = shape3;

        if ((rotate != null) && (angle != 0)) {
            /// we have rotation huston, please spin me
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            s1 = shape1.createTransformedShape(transform);
            s2 = shape2.createTransformedShape(transform);
            s3 = shape3.createTransformedShape(transform);
        }


        if (getHighlightPaint() != null) {
            g2.setPaint(getHighlightPaint());
            g2.fill(s3);
        }

        if (getFillPaint() != null) {
            g2.setPaint(getFillPaint());
            g2.fill(s1);
            g2.fill(s2);
        }


        if (getOutlinePaint() != null) {
            g2.setStroke(getOutlineStroke());
            g2.setPaint(getOutlinePaint());
            g2.draw(s1);
            g2.draw(s2);
            g2.draw(s3);
        }
    }

    /**
     * Tests another object for equality with this object.
     *
     * @param object  the object to test.
     *
     * @return A boolean.
     */
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (super.equals(object) && object instanceof LongNeedle) {
            return true;
        }
        return false;
    }

}
