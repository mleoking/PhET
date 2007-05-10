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
 * ------------------------------
 * LineAndShapeRendererTests.java
 * ------------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LineAndShapeRendererTests.java,v 1.1.2.1 2006/10/03 15:41:33 mungady Exp $
 *
 * Changes
 * -------
 * 22-Sep-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.renderer.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.category.LineAndShapeRenderer;

/**
 * Tests for the {@link LineAndShapeRenderer} class.
 */
public class LineAndShapeRendererTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(LineAndShapeRendererTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public LineAndShapeRendererTests(String name) {
        super(name);
    }

    /**
     * Test that the equals() method distinguishes all fields.
     */
    public void testEquals() {
        
        LineAndShapeRenderer r1 = new LineAndShapeRenderer();
        LineAndShapeRenderer r2 = new LineAndShapeRenderer();
        assertEquals(r1, r2);
        
        r1.setBaseLinesVisible(!r1.getBaseLinesVisible());
        assertFalse(r1.equals(r2));
        r2.setBaseLinesVisible(r1.getBaseLinesVisible());
        assertTrue(r1.equals(r2));
        
        r1.setSeriesLinesVisible(1, true);
        assertFalse(r1.equals(r2));
        r2.setSeriesLinesVisible(1, true);
        assertTrue(r1.equals(r2));
        
        r1.setLinesVisible(false);
        assertFalse(r1.equals(r2));
        r2.setLinesVisible(false);
        assertTrue(r1.equals(r2));
        
        r1.setBaseShapesVisible(!r1.getBaseShapesVisible());
        assertFalse(r1.equals(r2));
        r2.setBaseShapesVisible(r1.getBaseShapesVisible());
        assertTrue(r1.equals(r2));
        
        r1.setSeriesShapesVisible(1, true);
        assertFalse(r1.equals(r2));
        r2.setSeriesShapesVisible(1, true);
        assertTrue(r1.equals(r2));
        
        r1.setShapesVisible(false);
        assertFalse(r1.equals(r2));
        r2.setShapesVisible(false);
        assertTrue(r1.equals(r2));

        r1.setShapesFilled(false);
        assertFalse(r1.equals(r2));
        r2.setShapesFilled(false);
        assertTrue(r1.equals(r2));
        
        r1.setSeriesShapesFilled(1, true);
        assertFalse(r1.equals(r2));
        r2.setSeriesShapesFilled(1, true);
        assertTrue(r1.equals(r2));
        
        r1.setBaseShapesFilled(false);
        assertFalse(r1.equals(r2));
        r2.setBaseShapesFilled(false);
        assertTrue(r1.equals(r2));
        
        r1.setUseOutlinePaint(true);
        assertFalse(r1.equals(r2));
        r2.setUseOutlinePaint(true);
        assertTrue(r1.equals(r2));
        
    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        LineAndShapeRenderer r1 = new LineAndShapeRenderer();
        LineAndShapeRenderer r2 = new LineAndShapeRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        LineAndShapeRenderer r1 = new LineAndShapeRenderer();
        LineAndShapeRenderer r2 = null;
        try {
            r2 = (LineAndShapeRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        assertTrue(checkIndependence(r1, r2));
        
    }

    /**
     * Checks that the two renderers are equal but independent of one another.
     * 
     * @param r1  renderer 1.
     * @param r2  renderer 2.
     * 
     * @return A boolean.
     */
    private boolean checkIndependence(LineAndShapeRenderer r1, 
                                      LineAndShapeRenderer r2) {

        // should be equal...
        if (!r1.equals(r2)) {
            return false;
        }
        
        // and independent...        
        r1.setBaseLinesVisible(!r1.getBaseLinesVisible());
        if (r1.equals(r2)) {
            return false;
        }
        r2.setBaseLinesVisible(r1.getBaseLinesVisible());
        if (!r1.equals(r2)) {
            return false;
        }

        r1.setSeriesLinesVisible(1, true);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setSeriesLinesVisible(1, true);
        if (!r1.equals(r2)) {
            return false;
        }
            
        r1.setLinesVisible(false);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setLinesVisible(false);
        if (!r1.equals(r2)) {
            return false;
        }
        
        r1.setBaseShapesVisible(!r1.getBaseShapesVisible());
        if (r1.equals(r2)) {
            return false;
        }
        r2.setBaseShapesVisible(r1.getBaseShapesVisible());
        if (!r1.equals(r2)) {
            return false;
        }

        r1.setSeriesShapesVisible(1, true);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setSeriesShapesVisible(1, true);
        if (!r1.equals(r2)) {
            return false;
        }
            
        r1.setShapesVisible(false);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setShapesVisible(false);
        if (!r1.equals(r2)) {
            return false;
        }
        
        boolean flag = true;
        Boolean existing = r1.getShapesFilled();
        if (existing != null) {
            flag = !existing.booleanValue();
        }
        r1.setShapesFilled(flag);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setShapesFilled(flag);
        if (!r1.equals(r2)) {
            return false;
        }

        r1.setShapesFilled(false);
        r2.setShapesFilled(false);
        r1.setSeriesShapesFilled(0, false);
        r2.setSeriesShapesFilled(0, true);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setSeriesShapesFilled(0, false);
        if (!r1.equals(r2)) {
            return false;
        }
        
        r1.setBaseShapesFilled(false);
        r2.setBaseShapesFilled(true);
        if (r1.equals(r2)) {
            return false;
        }
        r2.setBaseShapesFilled(false);
        if (!r1.equals(r2)) {
            return false;
        }
        return true;
    
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        LineAndShapeRenderer r1 = new LineAndShapeRenderer();
        LineAndShapeRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (LineAndShapeRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }

}
