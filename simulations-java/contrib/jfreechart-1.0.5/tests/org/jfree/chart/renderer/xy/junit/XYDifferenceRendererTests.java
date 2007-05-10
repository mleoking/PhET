/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
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
 * XYDifferenceRendererTests.java
 * ------------------------------
 * (C) Copyright 2003-2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYDifferenceRendererTests.java,v 1.1.2.2 2007/01/24 11:02:25 mungady Exp $
 *
 * Changes
 * -------
 * 22-Oct-2003 : Version 1 (DG);
 * 04-May-2005 : Improved equals() test (DG);
 * 24-Jan-2007 : Added 'roundXCoordinates' to testEquals(), and improved
 *               testClone() (DG);
 *
 */

package org.jfree.chart.renderer.xy.junit;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.xy.XYDifferenceRenderer;

/**
 * Tests for the {@link XYDifferenceRenderer} class.
 */
public class XYDifferenceRendererTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(XYDifferenceRendererTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public XYDifferenceRendererTests(String name) {
        super(name);
    }

    /**
     * Check that the equals() method distinguishes all fields.
     */
    public void testEquals() {
        XYDifferenceRenderer r1 = new XYDifferenceRenderer(
                Color.red, Color.blue, false);
        XYDifferenceRenderer r2 = new XYDifferenceRenderer(
                Color.red, Color.blue, false);
        assertEquals(r1, r2);
        
        // positive paint
        r1.setPositivePaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.blue));
        assertFalse(r1.equals(r2));
        r2.setPositivePaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.blue));
        assertTrue(r1.equals(r2));
        
        // negative paint
        r1.setNegativePaint(new GradientPaint(1.0f, 2.0f, Color.yellow, 
                3.0f, 4.0f, Color.blue));
        assertFalse(r1.equals(r2));
        r2.setNegativePaint(new GradientPaint(1.0f, 2.0f, Color.yellow, 
                3.0f, 4.0f, Color.blue));
        assertTrue(r1.equals(r2));
        
        // shapesVisible
        r1 = new XYDifferenceRenderer(Color.green, Color.yellow, true);
        assertFalse(r1.equals(r2));
        r2 = new XYDifferenceRenderer(Color.green, Color.yellow, true);
        assertTrue(r1.equals(r2));
        
        // legendLine
        r1.setLegendLine(new Line2D.Double(1.0, 2.0, 3.0, 4.0));
        assertFalse(r1.equals(r2));
        r2.setLegendLine(new Line2D.Double(1.0, 2.0, 3.0, 4.0));
        assertTrue(r1.equals(r2));
        
        // roundXCoordinates
        r1.setRoundXCoordinates(true);
        assertFalse(r1.equals(r2));
        r2.setRoundXCoordinates(true);
        assertTrue(r1.equals(r2));
        
        assertFalse(r1.equals(null));
    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        XYDifferenceRenderer r1 
            = new XYDifferenceRenderer(Color.red, Color.blue, false);
        XYDifferenceRenderer r2 
            = new XYDifferenceRenderer(Color.red, Color.blue, false);
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        XYDifferenceRenderer r1 = new XYDifferenceRenderer(Color.red, 
                Color.blue, false);
        XYDifferenceRenderer r2 = null;
        try {
            r2 = (XYDifferenceRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        // check independence
        Shape s = r1.getLegendLine();
        if (s instanceof Line2D) {
            Line2D l = (Line2D) s;
            l.setLine(1.0, 2.0, 3.0, 4.0);
            assertFalse(r1.equals(r2));
        }
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        XYDifferenceRenderer r1 = new XYDifferenceRenderer(Color.red, 
                Color.blue, false);
        XYDifferenceRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (XYDifferenceRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

}
