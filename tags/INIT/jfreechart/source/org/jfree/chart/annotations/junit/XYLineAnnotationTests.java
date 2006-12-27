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
 * --------------------------
 * XYLineAnnotationTests.java
 * --------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 19-Aug-2003 : Version 1 (DG);
 * 07-Jan-2005 : Added hashCode() test (DG);
 *
 */

package org.jfree.chart.annotations.junit;

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

import org.jfree.chart.annotations.XYLineAnnotation;

/**
 * Tests for the {@link XYLineAnnotation} class.
 */
public class XYLineAnnotationTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(XYLineAnnotationTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public XYLineAnnotationTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() { 
        Stroke stroke = new BasicStroke(2.0f);
        XYLineAnnotation a1 = new XYLineAnnotation(
            10.0, 20.0, 100.0, 200.0, stroke, Color.blue
        );
        XYLineAnnotation a2 = new XYLineAnnotation(
            10.0, 20.0, 100.0, 200.0, stroke, Color.blue
        );
        assertTrue(a1.equals(a2));
    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashCode() {
        Stroke stroke = new BasicStroke(2.0f);
        XYLineAnnotation a1 = new XYLineAnnotation(
            10.0, 20.0, 100.0, 200.0, stroke, Color.blue
        );
        XYLineAnnotation a2 = new XYLineAnnotation(
            10.0, 20.0, 100.0, 200.0, stroke, Color.blue
        );
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        Stroke stroke = new BasicStroke(2.0f);
        XYLineAnnotation a1 = new XYLineAnnotation(
            10.0, 20.0, 100.0, 200.0, stroke, Color.blue
        );
        XYLineAnnotation a2 = null;
        try {
            a2 = (XYLineAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Stroke stroke = new BasicStroke(2.0f);
        XYLineAnnotation a1 = new XYLineAnnotation(
            10.0, 20.0, 100.0, 200.0, stroke, Color.blue
        );
        XYLineAnnotation a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (XYLineAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
