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
 * -------------------------
 * FastScatterPlotTests.java
 * -------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 18-Mar-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.FastScatterPlot;

/**
 * Tests for the {@link FastScatterPlot} class.
 */
public class FastScatterPlotTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(FastScatterPlotTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public FastScatterPlotTests(String name) {
        super(name);
    }

    /**
     * Test the equals() method.
     */
    public void testEquals() {
        
        FastScatterPlot plot1 = new FastScatterPlot();
        FastScatterPlot plot2 = new FastScatterPlot();
        assertTrue(plot1.equals(plot2));    
        assertTrue(plot2.equals(plot1));
        
        plot1.setPaint(Color.yellow);
        assertFalse(plot1.equals(plot2));
        plot2.setPaint(Color.yellow);
        assertTrue(plot1.equals(plot2));
        
        plot1.setDomainGridlinesVisible(false);
        assertFalse(plot1.equals(plot2));
        plot2.setDomainGridlinesVisible(false);
        assertTrue(plot1.equals(plot2));
        
        plot1.setDomainGridlinePaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setDomainGridlinePaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        Stroke s = new BasicStroke(1.5f);
        plot1.setDomainGridlineStroke(s);
        assertFalse(plot1.equals(plot2));
        plot2.setDomainGridlineStroke(s);
        assertTrue(plot1.equals(plot2));
        
        plot1.setRangeGridlinesVisible(false);
        assertFalse(plot1.equals(plot2));
        plot2.setRangeGridlinesVisible(false);
        assertTrue(plot1.equals(plot2));
        
        plot1.setRangeGridlinePaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setRangeGridlinePaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        Stroke s2 = new BasicStroke(1.5f);
        plot1.setRangeGridlineStroke(s2);
        assertFalse(plot1.equals(plot2));
        plot2.setRangeGridlineStroke(s2);
        assertTrue(plot1.equals(plot2));
        
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        FastScatterPlot p1 = new FastScatterPlot();
        FastScatterPlot p2 = null;
        try {
            p2 = (FastScatterPlot) p1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.err.println("Failed to clone.");
        }
        assertTrue(p1 != p2);
        assertTrue(p1.getClass() == p2.getClass());
        assertTrue(p1.equals(p2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        float[][] data = createData();

        ValueAxis domainAxis = new NumberAxis("X");
        ValueAxis rangeAxis = new NumberAxis("Y");
        FastScatterPlot p1 = new FastScatterPlot(data, domainAxis, rangeAxis);
        FastScatterPlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            p2 = (FastScatterPlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2);

    }

    /**
     * Populates the data array with random values.
     *
     * @return Random data.
     */
    private float[][] createData() {

        float[][] result = new float[2][1000];
        for (int i = 0; i < result[0].length; i++) {

            float x = (float) i + 100;
            result[0][i] = x;
            result[1][i] = 100 + (float) Math.random() * 1000;
        }
        return result;

    }

}
