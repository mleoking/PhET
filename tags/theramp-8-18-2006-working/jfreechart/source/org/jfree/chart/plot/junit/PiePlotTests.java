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
 * -----------------
 * PiePlotTests.java
 * -----------------
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
 * 10-May-2005 : Strengthened equals() test (DG);
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.StandardPieItemLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.util.Rotation;

/**
 * Tests for the {@link PiePlot} class.
 */
public class PiePlotTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(PiePlotTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public PiePlotTests(String name) {
        super(name);
    }

    /**
     * Test the equals() method.
     */
    public void testEquals() {
        
        PiePlot plot1 = new PiePlot();
        PiePlot plot2 = new PiePlot();
        assertTrue(plot1.equals(plot2));
        assertTrue(plot2.equals(plot1));
                
        // pieIndex...
        plot1.setPieIndex(99);
        assertFalse(plot1.equals(plot2));
        plot2.setPieIndex(99);
        assertTrue(plot1.equals(plot2));
        
        // interiorGap...
        plot1.setInteriorGap(0.15);
        assertFalse(plot1.equals(plot2));
        plot2.setInteriorGap(0.15);
        assertTrue(plot1.equals(plot2));

        // circular
        plot1.setCircular(!plot1.isCircular());
        assertFalse(plot1.equals(plot2));
        plot2.setCircular(false);
        assertTrue(plot1.equals(plot2));
        
        // startAngle
        plot1.setStartAngle(Math.PI);
        assertFalse(plot1.equals(plot2));
        plot2.setStartAngle(Math.PI);
        assertTrue(plot1.equals(plot2));
        
        // direction
        plot1.setDirection(Rotation.ANTICLOCKWISE);
        assertFalse(plot1.equals(plot2));
        plot2.setDirection(Rotation.ANTICLOCKWISE);
        assertTrue(plot1.equals(plot2));
        
        // ignoreZeroValues
        plot1.setIgnoreZeroValues(true);
        assertFalse(plot1.equals(plot2));
        plot2.setIgnoreZeroValues(true);
        assertTrue(plot1.equals(plot2));
        
        // ignoreNullValues
        plot1.setIgnoreNullValues(true);
        assertFalse(plot1.equals(plot2));
        plot2.setIgnoreNullValues(true);
        assertTrue(plot1.equals(plot2));
        
        // sectionPaint
        plot1.setSectionPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setSectionPaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // sectionPaintList
        plot1.setSectionPaint(2, Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setSectionPaint(2, Color.red);
        assertTrue(plot1.equals(plot2));
        
        // baseSectionPaint
        plot1.setBaseSectionPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setBaseSectionPaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // sectionOutlinePaint
        plot1.setSectionOutlinePaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlinePaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // sectionOutlinePaintList
        plot1.setSectionOutlinePaint(2, Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlinePaint(2, Color.red);
        assertTrue(plot1.equals(plot2));
        
        // baseSectionOutlinePaint
        plot1.setBaseSectionOutlinePaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setBaseSectionOutlinePaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // sectionOutlineStroke
        plot1.setSectionOutlineStroke(new BasicStroke(1.0f));
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlineStroke(new BasicStroke(1.0f));
        assertTrue(plot1.equals(plot2));
        
        // sectionOutlineStrokeList
        plot1.setSectionOutlineStroke(2, new BasicStroke(1.0f));
        assertFalse(plot1.equals(plot2));
        plot2.setSectionOutlineStroke(2, new BasicStroke(1.0f));
        assertTrue(plot1.equals(plot2));
        
        // baseSectionOutlineStroke
        plot1.setBaseSectionOutlineStroke(new BasicStroke(1.0f));
        assertFalse(plot1.equals(plot2));
        plot2.setBaseSectionOutlineStroke(new BasicStroke(1.0f));
        assertTrue(plot1.equals(plot2));
        
        // shadowPaint
        plot1.setShadowPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setShadowPaint(Color.red);
        assertTrue(plot1.equals(plot2));

        // shadowXOffset
        plot1.setShadowXOffset(4.4);
        assertFalse(plot1.equals(plot2));
        plot2.setShadowXOffset(4.4);
        assertTrue(plot1.equals(plot2));

        // shadowYOffset
        plot1.setShadowYOffset(4.4);
        assertFalse(plot1.equals(plot2));
        plot2.setShadowYOffset(4.4);
        assertTrue(plot1.equals(plot2));

        // labelFont
        plot1.setLabelFont(new Font("Serif", Font.PLAIN, 18));
        assertFalse(plot1.equals(plot2));
        plot2.setLabelFont(new Font("Serif", Font.PLAIN, 18));
        assertTrue(plot1.equals(plot2));
       
        // labelPaint
        plot1.setLabelPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelPaint(Color.red);
        assertTrue(plot1.equals(plot2));
       
        // labelBackgroundPaint
        plot1.setLabelBackgroundPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelBackgroundPaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // labelOutlinePaint
        plot1.setLabelOutlinePaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelOutlinePaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // labelOutlineStroke
        Stroke s = new BasicStroke(1.1f);
        plot1.setLabelOutlineStroke(s);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelOutlineStroke(s);
        assertTrue(plot1.equals(plot2));
        
        // labelShadowPaint
        plot1.setLabelShadowPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelShadowPaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // explodePercentages
        plot1.setExplodePercent(3, 0.33);
        assertFalse(plot1.equals(plot2));
        plot2.setExplodePercent(3, 0.33);
        assertTrue(plot1.equals(plot2));
        
        // labelGenerator
        plot1.setLabelGenerator(new StandardPieItemLabelGenerator("{2}{1}{0}"));
        assertFalse(plot1.equals(plot2));
        plot2.setLabelGenerator(new StandardPieItemLabelGenerator("{2}{1}{0}"));
        assertTrue(plot1.equals(plot2));
       
        // labelFont
        Font f = new Font("SansSerif", Font.PLAIN, 20);
        plot1.setLabelFont(f);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelFont(f);
        assertTrue(plot1.equals(plot2));
        
        // labelPaint
        plot1.setLabelPaint(Color.blue);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelPaint(Color.blue);
        assertTrue(plot1.equals(plot2));
        
        // maximumLabelWidth
        plot1.setMaximumLabelWidth(0.33);
        assertFalse(plot1.equals(plot2));
        plot2.setMaximumLabelWidth(0.33);
        assertTrue(plot1.equals(plot2));
        
        // labelGap
        plot1.setLabelGap(0.11);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelGap(0.11);
        assertTrue(plot1.equals(plot2));
        
        // links visible
        plot1.setLabelLinksVisible(false);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelLinksVisible(false);
        assertTrue(plot1.equals(plot2));
        
        // linkMargin
        plot1.setLabelLinkMargin(0.11);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelLinkMargin(0.11);
        assertTrue(plot1.equals(plot2));

        // labelLinkPaint
        plot1.setLabelLinkPaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setLabelLinkPaint(Color.red);
        assertTrue(plot1.equals(plot2));
       
        // labelLinkStroke
        plot1.setLabelLinkStroke(new BasicStroke(1.0f));
        assertFalse(plot1.equals(plot2));
        plot2.setLabelLinkStroke(new BasicStroke(1.0f));
        assertTrue(plot1.equals(plot2));
       
        // toolTipGenerator
        plot1.setToolTipGenerator(
            new StandardPieItemLabelGenerator("{2}{1}{0}")
        );
        assertFalse(plot1.equals(plot2));
        plot2.setToolTipGenerator(
            new StandardPieItemLabelGenerator("{2}{1}{0}")
        );
        assertTrue(plot1.equals(plot2));
        
        // urlGenerator
        plot1.setURLGenerator(new StandardPieURLGenerator("xx"));
        assertFalse(plot1.equals(plot2));
        plot2.setURLGenerator(new StandardPieURLGenerator("xx"));
        assertTrue(plot1.equals(plot2));
        
        // minimumArcAngleToDraw
        plot1.setMinimumArcAngleToDraw(1.0);
        assertFalse(plot1.equals(plot2));
        plot2.setMinimumArcAngleToDraw(1.0);  
        assertTrue(plot1.equals(plot2));
        
        // legendItemShape
        plot1.setLegendItemShape(new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));
        assertFalse(plot1.equals(plot2));
        plot2.setLegendItemShape(new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));
        assertTrue(plot1.equals(plot2));
        
    }

    /**
     * Some basic checks for the clone() method.
     */
    public void testCloning() {
        PiePlot p1 = new PiePlot();
        PiePlot p2 = null;
        try {
            p2 = (PiePlot) p1.clone();
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

        PiePlot p1 = new PiePlot(null);
        PiePlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            p2 = (PiePlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);

    }

}
