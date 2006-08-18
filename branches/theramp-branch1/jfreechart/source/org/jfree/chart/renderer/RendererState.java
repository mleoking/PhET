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
 * ------------------
 * RendererState.java
 * ------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 07-Oct-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer;

import org.jfree.chart.plot.PlotRenderingInfo;

/**
 * Represents the current state of a renderer.
 */
public class RendererState {
    
    /** The plot rendering info. */
    private PlotRenderingInfo info;
    
    /**
     * Creates a new state object.
     * 
     * @param info  the plot rendering info.
     */
    public RendererState(PlotRenderingInfo info) {
        this.info = info;
    }
    
    /**
     * Returns the plot rendering info.
     * 
     * @return The info.
     */
    public PlotRenderingInfo getInfo() {
        return this.info;
    }
    
}
