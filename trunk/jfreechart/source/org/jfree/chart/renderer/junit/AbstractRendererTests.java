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
 * --------------------------
 * AbstractRendererTests.java
 * --------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 23-Oct-2003 : Version 1 (DG);
 * 01-Mar-2004 : Added serialization test (DG);
 *
 */

package org.jfree.chart.renderer.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.Shape;
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

import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.ui.TextAnchor;

/**
 * Tests for the {@link AbstractRenderer} class.
 */
public class AbstractRendererTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(AbstractRendererTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public AbstractRendererTests(String name) {
        super(name);
    }
    
    /**
     * Test that the equals() method distinguishes all fields.
     */
    public void testEquals() {
        // have to use a concrete subclass...
        BarRenderer r1 = new BarRenderer();
        BarRenderer r2 = new BarRenderer();
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));
        
        // seriesVisible
        r1.setSeriesVisible(Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisible(Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        // seriesVisibleList
        r1.setSeriesVisible(2, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisible(2, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        // baseSeriesVisible
        r1.setBaseSeriesVisible(false);
        assertFalse(r1.equals(r2));
        r2.setBaseSeriesVisible(false);
        assertTrue(r1.equals(r2));
        
        // seriesVisibleInLegend
        r1.setSeriesVisibleInLegend(Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisibleInLegend(Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        // seriesVisibleInLegendList
        r1.setSeriesVisibleInLegend(1, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisibleInLegend(1, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        // baseSeriesVisibleInLegend
        r1.setBaseSeriesVisibleInLegend(false);
        assertFalse(r1.equals(r2));
        r2.setBaseSeriesVisibleInLegend(false);
        assertTrue(r1.equals(r2));
        
        // paint
        r1.setPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        // paintList
        r1.setSeriesPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.white));
        assertFalse(r1.equals(r2));
        r2.setSeriesPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.white));
        assertTrue(r1.equals(r2));
        
        // basePaint
        r1.setBasePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setBasePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        // fillPaint
        r1.setFillPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setFillPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        // fillPaintList
        r1.setSeriesFillPaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setSeriesFillPaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        // baseFillPaint
        r1.setBaseFillPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setBaseFillPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));

        // outlinePaint
        r1.setOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        // outlinePaintList
        r1.setSeriesOutlinePaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setSeriesOutlinePaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        // baseOutlinePaint
        r1.setBaseOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setBaseOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        // stroke
        Stroke s = new BasicStroke(3.21f);
        r1.setStroke(s);
        assertFalse(r1.equals(r2));
        r2.setStroke(s);
        assertTrue(r1.equals(r2));
        
        // strokeList
        r1.setSeriesStroke(1, s);
        assertFalse(r1.equals(r2));
        r2.setSeriesStroke(1, s);
        assertTrue(r1.equals(r2));

        // baseStroke
        r1.setBaseStroke(s);
        assertFalse(r1.equals(r2));
        r2.setBaseStroke(s);
        assertTrue(r1.equals(r2));

        // outlineStroke
        r1.setOutlineStroke(s);
        assertFalse(r1.equals(r2));
        r2.setOutlineStroke(s);
        assertTrue(r1.equals(r2));
        
        // outlineStrokeList
        r1.setSeriesOutlineStroke(0, s);
        assertFalse(r1.equals(r2));
        r2.setSeriesOutlineStroke(0, s);
        assertTrue(r1.equals(r2));

        // baseOutlineStroke
        r1.setBaseOutlineStroke(s);
        assertFalse(r1.equals(r2));
        r2.setBaseOutlineStroke(s);
        assertTrue(r1.equals(r2));
        
        // shape
        r1.setShape(new Rectangle(1, 2, 3, 4));
        assertFalse(r1.equals(r2));
        r2.setShape(new Rectangle(1, 2, 3, 4));
        assertTrue(r1.equals(r2));
        
        // shapeList
        r1.setSeriesShape(1, new Rectangle(1, 2, 3, 4));
        assertFalse(r1.equals(r2));
        r2.setSeriesShape(1, new Rectangle(1, 2, 3, 4));
        assertTrue(r1.equals(r2));

        // baseShape
        r1.setBaseShape(new Rectangle(1, 2, 3, 4));
        assertFalse(r1.equals(r2));
        r2.setBaseShape(new Rectangle(1, 2, 3, 4));
        assertTrue(r1.equals(r2));

        // itemLabelsVisible
        r1.setItemLabelsVisible(true);
        assertFalse(r1.equals(r2));
        r2.setItemLabelsVisible(true);
        assertTrue(r1.equals(r2));
        
        // itemLabelsVisibleList
        r1.setSeriesItemLabelsVisible(1, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelsVisible(1, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        // baseItemLabelsVisible
        r1.setBaseItemLabelsVisible(true);
        assertFalse(r1.equals(r2));
        r2.setBaseItemLabelsVisible(true);
        assertTrue(r1.equals(r2));

        // itemLabelFont
        r1.setItemLabelFont(new Font("Serif", Font.PLAIN, 10));
        assertFalse(r1.equals(r2));
        r2.setItemLabelFont(new Font("Serif", Font.PLAIN, 10));
        assertTrue(r1.equals(r2));
        
        // itemLabelFontList
        r1.setSeriesItemLabelFont(1, new Font("Serif", Font.BOLD, 9));
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelFont(1, new Font("Serif", Font.BOLD, 9));
        assertTrue(r1.equals(r2));
        
        // baseItemLabelFont
        r1.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 10));
        assertFalse(r1.equals(r2));
        r2.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 10));
        assertTrue(r1.equals(r2));
        
        // itemLabelPaint
        r1.setItemLabelPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertFalse(r1.equals(r2));
        r2.setItemLabelPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertTrue(r1.equals(r2));
        
        // itemLabelPaintList
        r1.setSeriesItemLabelPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertTrue(r1.equals(r2));

        // baseItemLabelPaint
        r1.setBaseItemLabelPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertFalse(r1.equals(r2));
        r2.setBaseItemLabelPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertTrue(r1.equals(r2));
        
        // positiveItemLabelPosition;
        r1.setPositiveItemLabelPosition(new ItemLabelPosition());
        assertFalse(r1.equals(r2));
        r2.setPositiveItemLabelPosition(new ItemLabelPosition());
        assertTrue(r1.equals(r2));
        
        // positiveItemLabelPositionList;
        r1.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition());
        assertFalse(r1.equals(r2));
        r2.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition());
        assertTrue(r1.equals(r2));

        // basePositiveItemLabelPosition;
        r1.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertFalse(r1.equals(r2));
        r2.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertTrue(r1.equals(r2));
        
        // negativeItemLabelPosition;
        r1.setNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertFalse(r1.equals(r2));
        r2.setNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertTrue(r1.equals(r2));
        
        // negativeItemLabelPositionList;
        r1.setSeriesNegativeItemLabelPosition(1, new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertFalse(r1.equals(r2));
        r2.setSeriesNegativeItemLabelPosition(1, new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertTrue(r1.equals(r2));

        // baseNegativeItemLabelPosition;
        r1.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertFalse(r1.equals(r2));
        r2.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertTrue(r1.equals(r2));

        // itemLabelAnchorOffset
        r1.setItemLabelAnchorOffset(3.0);
        assertFalse(r1.equals(r2));
        r2.setItemLabelAnchorOffset(3.0);
        assertTrue(r1.equals(r2));

        // createEntities;
        r1.setCreateEntities(Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setCreateEntities(Boolean.TRUE);
        assertTrue(r1.equals(r2));

        // createEntitiesList;
        r1.setSeriesCreateEntities(0, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesCreateEntities(0, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        // baseCreateEntities;
        r1.setBaseCreateEntities(false);
        assertFalse(r1.equals(r2));
        r2.setBaseCreateEntities(false);
        assertTrue(r1.equals(r2));

    }
    
    /**
     * Test that setting the series visibility for ALL series does in fact work.
     */
    public void testSetSeriesVisible() {
        BarRenderer r = new BarRenderer();
        r.setSeriesVisible(Boolean.TRUE);
        assertTrue(r.getItemVisible(0, 0));
    }

    /**
     * Test that setting the paint for ALL series does in fact work.
     */
    public void testSetPaint() {
        BarRenderer r = new BarRenderer();
        r.setPaint(Color.orange);
        assertEquals(Color.orange, r.getItemPaint(0, 0));
    }

    /**
     * Test that setting the outline paint for ALL series does in fact work.
     */
    public void testSetOutlinePaint() {
        BarRenderer r = new BarRenderer();
        r.setOutlinePaint(Color.orange);
        assertEquals(Color.orange, r.getItemOutlinePaint(0, 0));
    }

    /**
     * Test that setting the stroke for ALL series does in fact work.
     */
    public void testSetStroke() {
        BarRenderer r = new BarRenderer();
        Stroke s = new BasicStroke(10.0f);
        r.setStroke(s);
        assertEquals(s, r.getItemStroke(0, 0));
    }

    /**
     * Test that setting the outline stroke for ALL series does in fact work.
     */
    public void testSetOutlineStroke() {
        BarRenderer r = new BarRenderer();
        Stroke s = new BasicStroke(10.0f);
        r.setOutlineStroke(s);
        assertEquals(s, r.getItemOutlineStroke(0, 0));
    }

    /**
     * Test that setting the shape for ALL series does in fact work.
     */
    public void testSetShape() {
        BarRenderer r = new BarRenderer();
        Shape s = new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0);
        r.setShape(s);
        assertEquals(s, r.getItemShape(0, 0));
    }

    /**
     * Test that setting the item label visibility for ALL series does in fact 
     * work.
     */
    public void testSetItemLabelsVisible() {
        BarRenderer r = new BarRenderer();
        r.setItemLabelsVisible(true);
        assertTrue(r.isItemLabelVisible(0, 0));
    }

    /**
     * Test that setting the font for ALL series does in fact work (it was 
     * broken at one point).
     */
    public void testSetItemLabelFont() {
        BarRenderer r = new BarRenderer();
        r.setItemLabelFont(new Font("SansSerif", Font.PLAIN, 33));
        assertEquals(
            new Font("SansSerif", Font.PLAIN, 33), r.getItemLabelFont(0, 0)
        );
    }

    /**
     * Test that setting the paint for ALL series does in fact work (it was 
     * broken at one point).
     */
    public void testSetItemLabelPaint() {
        BarRenderer r = new BarRenderer();
        r.setItemLabelPaint(Color.green);
        assertEquals(Color.green, r.getItemLabelPaint(0, 0));
    }

    /**
     * Test that setting the positive item label position for ALL series does 
     * in fact work.
     */
    public void testSetPositiveItemLabelPosition() {
        BarRenderer r = new BarRenderer();
        r.setPositiveItemLabelPosition(
            new ItemLabelPosition(
                ItemLabelAnchor.INSIDE1, TextAnchor.BASELINE_LEFT
            )
        );
        assertEquals(
            new ItemLabelPosition(
                ItemLabelAnchor.INSIDE1, TextAnchor.BASELINE_LEFT
            ), 
            r.getPositiveItemLabelPosition(0, 0)
        );
    }

    /**
     * Test that setting the negative item label position for ALL series does 
     * in fact work.
     */
    public void testSetNegativeItemLabelPosition() {
        BarRenderer r = new BarRenderer();
        r.setNegativeItemLabelPosition(
            new ItemLabelPosition(
                ItemLabelAnchor.INSIDE1, TextAnchor.BASELINE_LEFT
            )
        );
        assertEquals(
            new ItemLabelPosition(
                ItemLabelAnchor.INSIDE1, TextAnchor.BASELINE_LEFT
            ), 
            r.getNegativeItemLabelPosition(0, 0)
        );
    }

    /**
     * Tests each setter method to ensure that it sends an event notification.
     */
    public void testEventNotification() {
        
        RendererChangeDetector detector = new RendererChangeDetector();
        BarRenderer r1 = new BarRenderer();  // have to use a subclass of 
                                             // AbstractRenderer
        r1.addChangeListener(detector);
        
        // PAINT
        detector.setNotified(false);
        r1.setPaint(Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesPaint(0, Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBasePaint(Color.red);
        assertTrue(detector.getNotified());

        // OUTLINE PAINT
        detector.setNotified(false);
        r1.setOutlinePaint(Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesOutlinePaint(0, Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseOutlinePaint(Color.red);
        assertTrue(detector.getNotified());
        
        // STROKE
        detector.setNotified(false);
        r1.setStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesStroke(0, new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        // OUTLINE STROKE
        detector.setNotified(false);
        r1.setOutlineStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesOutlineStroke(0, new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseOutlineStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        // SHAPE
        detector.setNotified(false);
        r1.setShape(new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesShape(0, new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseShape(new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        // ITEM_LABELS_VISIBLE
        detector.setNotified(false);
        r1.setItemLabelsVisible(Boolean.TRUE);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelsVisible(Boolean.TRUE);
        assertTrue(detector.getNotified());
        
        // ITEM_LABEL_FONT
        detector.setNotified(false);
        r1.setItemLabelFont(new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesItemLabelFont(0, new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());
        
        // ITEM_LABEL_PAINT
        detector.setNotified(false);
        r1.setItemLabelPaint(Color.blue);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesItemLabelPaint(0, Color.blue);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelPaint(Color.blue);
        assertTrue(detector.getNotified());
        
        // POSITIVE ITEM LABEL POSITION
        detector.setNotified(false);
        r1.setPositiveItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesPositiveItemLabelPosition(
            0, new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBasePositiveItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
        assertTrue(detector.getNotified());

        // NEGATIVE ITEM LABEL ANCHOR
        detector.setNotified(false);
        r1.setNegativeItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setSeriesNegativeItemLabelPosition(
            0, new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseNegativeItemLabelPosition(
            new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER)
        );
        assertTrue(detector.getNotified());

    }

    /**
     * Serialize an instance, restore it, and check for equality.  In addition,
     * test for a bug that was reported where the listener list is 'null' after 
     * deserialization.
     */
    public void testSerialization() {

        BarRenderer r1 = new BarRenderer();  
        BarRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (BarRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);
        try {
            r2.notifyListeners(new RendererChangeEvent(r2));
        }
        catch (NullPointerException e) {
            assertTrue(false);  // failed   
        }

    }

}
