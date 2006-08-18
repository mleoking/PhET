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
 * ------------------------
 * LineRenderer3DTests.java
 * ------------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 15-Oct-2004 : Version 1 (DG);
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

import org.jfree.chart.renderer.category.LineRenderer3D;

/**
 * Tests for the {@link LineRenderer3D} class.
 */
public class LineRenderer3DTests extends TestCase {

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
    public LineRenderer3DTests(String name) {
        super(name);
    }

    /**
     * Test that the equals() method distinguishes all fields.
     */
    public void testEquals() {
        LineRenderer3D r1 = new LineRenderer3D();
        LineRenderer3D r2 = new LineRenderer3D();
        assertEquals(r1, r2);
    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        LineRenderer3D r1 = new LineRenderer3D();
        LineRenderer3D r2 = new LineRenderer3D();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        LineRenderer3D r1 = new LineRenderer3D();
        LineRenderer3D r2 = null;
        try {
            r2 = (LineRenderer3D) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
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
    private boolean checkIndependence(LineRenderer3D r1, LineRenderer3D r2) {

        // should be equal...
        boolean b0 = r1.equals(r2);
        
        // and independent...
        r1.setShapesVisible(!r1.isShapesVisible());
        boolean b1 = !r1.equals(r2);
        r2.setShapesVisible(r1.isShapesVisible());
        boolean b2 = r1.equals(r2);
        
        r1.setLinesVisible(!r1.isLinesVisible());
        boolean b3 = !r1.equals(r2);
        r2.setLinesVisible(r1.isLinesVisible());
        boolean b4 = r1.equals(r2);
                
        boolean flag = true;
        Boolean existing = r1.getShapesFilled();
        if (existing != null) {
            flag = !existing.booleanValue();
        }
        r1.setShapesFilled(flag);
        boolean b5 = !r1.equals(r2);
        r2.setShapesFilled(flag);
        boolean b6 = r1.equals(r2);

        r1.setShapesFilled(false);
        r2.setShapesFilled(false);
        r1.setSeriesShapesFilled(0, false);
        r2.setSeriesShapesFilled(0, true);
        boolean b7 = !r1.equals(r2);
        r2.setSeriesShapesFilled(0, false);
        boolean b8 = (r1.equals(r2));
        
        r1.setDefaultShapesFilled(false);
        r2.setDefaultShapesFilled(true);
        boolean b9 = !r1.equals(r2);
        r2.setDefaultShapesFilled(false);
        boolean b10 = (r1.equals(r2));
        
        return b0 && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9 && b10;
    
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        LineRenderer3D r1 = new LineRenderer3D();
        LineRenderer3D r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (LineRenderer3D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }

}
