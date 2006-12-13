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
 * -------------------
 * CrosshairState.java
 * -------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 24-Jan-2002 : Version 1 (DG);
 * 05-Mar-2002 : Added Javadoc comments (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 19-Sep-2003 : Modified crosshair distance calculation (DG);
 * 04-Dec-2003 : Crosshair anchor point now stored outside chart since it is
 *               dependent on the display target (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo --> CrosshairState (DG);               
 *
 */

package org.jfree.chart.plot;

import java.awt.geom.Point2D;

/**
 * Maintains state information about crosshairs on a plot.
 *
 */
public class CrosshairState {

    /** 
     * A flag that controls whether the distance is calculated in data space 
     * or Java2D space. 
     */
    private boolean calculateDistanceInDataSpace = false;

    /** The x-value (in data space) for the anchor point. */
    private double anchorX;

    /** The y-value (in data space) for the anchor point. */
    private double anchorY;
    
    /** The anchor point in Java2D space - if null, don't update crosshair. */
    private Point2D anchor;
    
    /** The x-value for the crosshair point. */
    private double crosshairX;

    /** The y-value for the crosshair point. */
    private double crosshairY;

    /** 
     * The smallest distance so far between the anchor point and a data point. 
     */
    private double distance;

    /**
     * Default constructor.
     */
    public CrosshairState() {
        this(false);
    }

    /**
     * Creates a new info object.
     * 
     * @param calculateDistanceInDataSpace  a flag that controls whether the 
     *                                      distance is calculated in data 
     *                                      space or Java2D space.
     */
    public CrosshairState(boolean calculateDistanceInDataSpace) {
        this.calculateDistanceInDataSpace = calculateDistanceInDataSpace;
    }

    /**
     * Sets the distance between the anchor point and the current crosshair 
     * point.  As each data point is processed, its distance to the anchor 
     * point is compared with this value and, if it is closer, the data point 
     * becomes the new crosshair point.
     *
     * @param distance  the distance.
     */
    public void setCrosshairDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Evaluates a data point and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * To understand this method, you need to know the context in which it will
     * be called.  An instance of this class is passed to an 
     * {@link org.jfree.chart.renderer.xy.XYItemRenderer} as
     * each data point is plotted.  As the point is plotted, it is passed to
     * this method to see if it should be the new crosshair point.
     *
     * @param x  x coordinate (measured against the domain axis).
     * @param y  y coordinate (measured against the range axis).
     * @param transX  x translated into Java2D space.
     * @param transY  y translated into Java2D space.
     * @param orientation  the plot orientation.
     */
    public void updateCrosshairPoint(double x, double y, 
                                     double transX, double transY, 
                                     PlotOrientation orientation) {

        if (this.anchor != null) {
            double d = 0.0;
            if (this.calculateDistanceInDataSpace) {
                d = (x - this.anchorX) * (x - this.anchorX)
                  + (y - this.anchorY) * (y - this.anchorY);
            }
            else {
                double xx = this.anchor.getX();
                double yy = this.anchor.getY();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    double temp = yy;
                    yy = xx;
                    xx = temp;
                }
                d = (transX - xx) * (transX - xx) 
                    + (transY - yy) * (transY - yy);            
            }

            if (d < this.distance) {
                this.crosshairX = x;
                this.crosshairY = y;
                this.distance = d;
            }
        }

    }

    /**
     * Evaluates an x-value and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * Used in cases where only the x-axis is numerical.
     *
     * @param candidateX  x position of the candidate for the new crosshair 
     *                    point.
     */
    public void updateCrosshairX(double candidateX) {

        double d = Math.abs(candidateX - this.anchorX);
        if (d < this.distance) {
            this.crosshairX = candidateX;
            this.distance = d;
        }

    }

    /**
     * Evaluates a y-value and if it is the closest to the anchor point it
     * becomes the new crosshair point.
     * <P>
     * Used in cases where only the y-axis is numerical.
     *
     * @param candidateY  y position of the candidate for the new crosshair 
     *                    point.
     */
    public void updateCrosshairY(double candidateY) {

        double d = Math.abs(candidateY - this.anchorY);
        if (d < this.distance) {
            this.crosshairY = candidateY;
            this.distance = d;
        }

    }

    /** 
     * Sets the anchor point.  This is usually the mouse click point in a chart
     * panel, and the crosshair point will often be the data item that is 
     * closest to the anchor point.
     * 
     * @param anchor  the anchor point.
     */
    public void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }
    
    /**
     * Get the x-value for the crosshair point.
     *
     * @return The x position of the crosshair point.
     */
    public double getCrosshairX() {
        return this.crosshairX;
    }
    
    /**
     * Sets the x coordinate for the crosshair.  This is the coordinate in data
     * space measured against the domain axis.
     * 
     * @param x the coordinate.
     */
    public void setCrosshairX(double x) {
        this.crosshairX = x;
    }

    /**
     * Get the y-value for the crosshair point.  This is the coordinate in data
     * space measured against the range axis.
     *
     * @return The y position of the crosshair point.
     */
    public double getCrosshairY() {
        return this.crosshairY;
    }

    /**
     * Sets the y coordinate for the crosshair.
     * 
     * @param y  the y coordinate.
     */
    public void setCrosshairY(double y) {
        this.crosshairY = y;
    }

}
