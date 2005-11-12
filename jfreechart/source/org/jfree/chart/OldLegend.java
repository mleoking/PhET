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
 * -----------
 * Legend.java
 * -----------
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Jim Moore;
 *                   Nicolas Brodu;
 *                   Barak Naveh;
 *
 * $Id$
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend 
 *               placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Tidied up Javadoc comments (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 20-Jun-2002 : Added outlineKeyBoxes attribute suggested by Jim Moore (DG);
 * 14-Oct-2002 : Changed listener storage structure (DG);
 * 14-Jan-2003 : Changed constructor to protected, moved outer-gap to 
 *               subclass (DG);
 * 27-Mar-2003 : Implemented Serializable (DG);
 * 05-Jun-2003 : Added ChartRenderingInfo parameter to draw() method (DG);
 * 11-Sep-2003 : Cloning support
 * 26-Mar-2004 : Added 8 more legend anchor points (BN);
 * 11-Jan-2005 : Removed deprecated code in preparation for the 1.0.0 
 *               release (DG);
 *
 */

package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

import org.jfree.chart.event.LegendChangeEvent;
import org.jfree.chart.event.LegendChangeListener;

/**
 * A chart legend shows the names and visual representations of the series that
 * are plotted in a chart.
 *
 * @see DefaultOldLegend
 */
public abstract class OldLegend implements Serializable, Cloneable {

    /** For serialization. */
    private static final long serialVersionUID = 7706174413964908242L;
    
    /** 
     * Internal value used as NORTHWEST diagonal component of the anchor 
     * value. 
     */
    private static final int NORTHWEST = 0xA0;

    /** 
     * Internal value used as NORTHEAST diagonal component of the anchor 
     * value. 
     */
    private static final int NORTHEAST = 0xB0;

    /** 
     * Internal value used as SOUTHEAST diagonal component of the anchor 
     * value. 
     */
    private static final int SOUTHEAST = 0xC0;

    /** 
     * Internal value used as SOUTHWEST diagonal component of the anchor 
     * value. 
     */
    private static final int SOUTHWEST = 0xD0;
    
    /** Constant anchor value for legend position WEST. */
    public static final int WEST = 0x00;

    /** Constant anchor value for legend position WEST_NORTHWEST. */
    public static final int WEST_NORTHWEST = WEST + NORTHWEST;

    /** Constant anchor value for legend position WEST_SOUTHWEST. */
    public static final int WEST_SOUTHWEST = WEST + SOUTHWEST;

    /** Constant anchor value for legend position NORTH. */
    public static final int NORTH = 0x01;

    /** Constant anchor value for legend position NORTH_NORTHWEST. */
    public static final int NORTH_NORTHWEST = NORTH + NORTHWEST;

    /** Constant anchor value for legend position NORTH_NORTHEAST. */
    public static final int NORTH_NORTHEAST = NORTH + NORTHEAST;

    /** Constant anchor value for legend position EAST. */
    public static final int EAST = 0x02;

    /** Constant anchor value for legend position EAST_NORTHEAST. */
    public static final int EAST_NORTHEAST = EAST + NORTHEAST;

    /** Constant anchor value for legend position EAST_SOUTHEAST. */
    public static final int EAST_SOUTHEAST = EAST + SOUTHEAST;

    /** Constant anchor value for legend position SOUTH. */
    public static final int SOUTH = 0x03;

    /** Constant anchor value for legend position SOUTH_SOUTHWEST. */
    public static final int SOUTH_SOUTHWEST = SOUTH + SOUTHWEST;

    /** Constant anchor value for legend position SOUTH_SOUTHEAST. */
    public static final int SOUTH_SOUTHEAST = SOUTH + SOUTHEAST;

    /** 
     * Internal value indicating the bit holding the value of interest in the 
     * anchor value. 
     */
    protected static final int INVERTED = 1 << 1;

    /** 
     * Internal value indicating the bit holding the value of interest in the 
     * anchor value. 
     */
    protected static final int HORIZONTAL = 1 << 0;

    /** The current location anchor of the legend. */
    private int anchor = SOUTH;

    /**
     * A reference to the chart that the legend belongs to
     * (used for access to the dataset).
     *  <!-- use registerChart() instead -->
     */
    private JFreeChart chart;

    /** Storage for registered change listeners. */
    private transient EventListenerList listenerList;

    /**
     * Static factory method that returns a concrete subclass of Legend.
     *
     * @param chart  the chart that the legend belongs to.
     *
     * @return A StandardLegend.
     */
    public static OldLegend createInstance(JFreeChart chart) {
        return new DefaultOldLegend();
    }

    /**
     * Default constructor.
     */
    public OldLegend() {
        this.listenerList = new EventListenerList();
    }

    /**
     * Returns the chart that this legend belongs to.
     *
     * @return The chart.
     */
    public JFreeChart getChart() {
        return this.chart;
    }

    /**
     * Internal maintenance method to update the reference to the central
     * JFreeChart object.
     *
     * @param chart the chart, may be null, if the legend gets removed from
     * the chart.
     */
    protected void registerChart(JFreeChart chart) {
        this.chart = chart;
    }

    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param available  the area within which the legend (and plot) should be
     *                   drawn.
     * @param info  a carrier for returning information about the entities in 
     *              the legend.
     *
     * @return The area remaining after the legend has drawn itself.
     */
    public abstract Rectangle2D draw(Graphics2D g2, Rectangle2D available, 
                                     ChartRenderingInfo info);

    /**
     * Registers an object for notification of changes to the legend.
     *
     * @param listener  the object that is being registered.
     */
    public void addChangeListener(LegendChangeListener listener) {
        this.listenerList.add(LegendChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the legend.
     *
     * @param listener  the object that is being deregistered.
     */
    public void removeChangeListener(LegendChangeListener listener) {
        this.listenerList.remove(LegendChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the chart legend has changed in 
     * some way.
     *
     * @param event  information about the change to the legend.
     */
    protected void notifyListeners(LegendChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == LegendChangeListener.class) {
                ((LegendChangeListener) listeners[i + 1]).legendChanged(event);
            }
        }

    }

    /**
     * Returns the current anchor of this legend.
     * <p>
     * The default anchor for this legend is <code>SOUTH</code>.
     *
     * @return The current anchor.
     */
    public int getAnchor() {
        return this.anchor;
    }

    /**
     * Sets the current anchor of this legend.
     * <P>
     * The anchor can be one of: <code>NORTH</code>, <code>SOUTH</code>, 
     * <code>EAST</code>, <code>WEST</code>.  If a valid anchor value is 
     * provided, the current anchor is set and an update event is triggered. 
     * Otherwise, no change is made.
     *
     * @param anchor  the new anchor value.
     */
    public void setAnchor(int anchor) {
        if (isValidAnchor(anchor)) {
            this.anchor = anchor;
            notifyListeners(new LegendChangeEvent(this));
        }
    }

    /**
     * Tests if the specified anchor is a valid anchor.
     * 
     * @param anchor a candidate anchor.
     * @return <code>true</code> if the anchor is valid; <code>false</code> 
     *         otherwise.
     */
    private boolean isValidAnchor(int anchor) {
        switch (anchor) {
            case NORTH:
            case NORTH_NORTHEAST:
            case NORTH_NORTHWEST:
            case SOUTH:
            case SOUTH_SOUTHEAST:
            case SOUTH_SOUTHWEST:
            case WEST:
            case WEST_NORTHWEST:
            case WEST_SOUTHWEST:
            case EAST:
            case EAST_NORTHEAST:
            case EAST_SOUTHEAST:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Returns <code>true</code> if and only if this legend is anchored to top.
     * 
     * @return <code>true</code> if and only if this legend is anchored to top.
     */
    protected boolean isAnchoredToTop() {
        switch (this.anchor) {
            case WEST_NORTHWEST:
            case NORTH_NORTHWEST:
            case NORTH:
            case NORTH_NORTHEAST:
            case EAST_NORTHEAST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns <code>true</code> if and only if this legend is anchored to 
     * middle.
     * 
     * @return <code>true</code> if and only if this legend is anchored to 
     *         middle.  
     */
    protected boolean isAnchoredToMiddle() {
        return this.anchor == EAST || this.anchor == WEST;
    }
    
    /**
     * Returns <code>true</code> if and only if this legend is anchored to 
     * bottom.
     * 
     * @return <code>true</code> if and only if this legend is anchored to 
     *         bottom.  
     */
    protected boolean isAnchoredToBottom() {
        switch (this.anchor) {
            case WEST_SOUTHWEST:
            case SOUTH_SOUTHWEST:
            case SOUTH:
            case SOUTH_SOUTHEAST:
            case EAST_SOUTHEAST:
                return true;
            default:
                return false;
        }                
    }

    /**
     * Returns <code>true</code> if and only if this legend is anchored to left.
     * 
     * @return <code>true</code> if and only if this legend is anchored to left.
     */
    protected boolean isAnchoredToLeft() {
        switch (this.anchor) {
            case NORTH_NORTHWEST: 
            case WEST_NORTHWEST:
            case WEST:
            case WEST_SOUTHWEST:
            case SOUTH_SOUTHWEST:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Returns <code>true</code> if and only if this legend is anchored to 
     * right.
     * 
     * @return <code>true</code> if and only if this legend is anchored to 
     *         right.  
     */
    protected boolean isAnchoredToRight() {
        switch (this.anchor) {
            case NORTH_NORTHEAST: 
            case EAST_NORTHEAST:
            case EAST:
            case EAST_SOUTHEAST:
            case SOUTH_SOUTHEAST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns <code>true</code> if and only if this legend is anchored to 
     * center.
     * 
     * @return <code>true</code> if and only if this legend is anchored to 
     *         center.  
     */
    protected boolean isAnchoredToCenter() {
        return this.anchor == NORTH || this.anchor == SOUTH;
    }
    
    /**
     * Tests this legend for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj instanceof OldLegend) {
            OldLegend l = (OldLegend) obj;
            return (this.anchor == l.anchor);
        }
        return false;

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
        this.listenerList = new EventListenerList();  
        // TODO: make sure this is populated.
    }

    /**
     * Clones the legend, and takes care of listeners.
     * Note: the cloned legend refer to the same chart as the original one.
     * JFreeChart clone() takes care of setting the references correctly.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    protected Object clone() throws CloneNotSupportedException {
        OldLegend ret = (OldLegend) super.clone();
        this.listenerList = new EventListenerList();
        return ret;
    }

}
