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
 * TextAnnotation.java
 * -------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 28-Aug-2002 : Version 1 (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle, added accessor 
 *               methods (DG);
 * 13-Jan-2003 : Reviewed Javadocs (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 02-Jun-2003 : Added anchor and rotation settings (DG);
 * 19-Aug-2003 : Added equals() method and implemented Cloneable (DG);
 * 29-Sep-2004 : Updated equals() method (DG);
 * 06-Jun-2005 : Fixed equals() method to work with GradientPaint (DG);
 * 
 */

package org.jfree.chart.annotations;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.io.SerialUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

/**
 * A base class for text annotations.  This class records the content but not 
 * the location of the annotation.
 */
public class TextAnnotation implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 7008912287533127432L;
    
    /** The default font. */
    public static final Font DEFAULT_FONT 
        = new Font("SansSerif", Font.PLAIN, 10);

    /** The default paint. */
    public static final Paint DEFAULT_PAINT = Color.black;
    
    /** The default text anchor. */
    public static final TextAnchor DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;

    /** The default rotation anchor. */    
    public static final TextAnchor DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;
    
    /** The default rotation angle. */
    public static final double DEFAULT_ROTATION_ANGLE = 0.0;

    /** The text. */
    private String text;

    /** The font. */
    private Font font;

    /** The paint. */
    private transient Paint paint;
    
    /** The text anchor. */
    private TextAnchor textAnchor;
    
    /** The rotation anchor. */
    private TextAnchor rotationAnchor;
    
    /** The rotation angle. */
    private double rotationAngle;

    /**
     * Creates a text annotation with default settings.
     *
     * @param text  the text (<code>null</code> not permitted).
     */
    protected TextAnnotation(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.text = text;
        this.font = DEFAULT_FONT;
        this.paint = DEFAULT_PAINT;
        this.textAnchor = DEFAULT_TEXT_ANCHOR;
        this.rotationAnchor = DEFAULT_ROTATION_ANCHOR;
        this.rotationAngle = DEFAULT_ROTATION_ANGLE;
    }

    /**
     * Returns the text for the annotation.
     *
     * @return The text (never <code>null</code>).
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets the text for the annotation.
     * 
     * @param text  the text (<code>null</code> not permitted).
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Returns the font for the annotation.
     *
     * @return The font.
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Sets the font for the annotation.
     * 
     * @param font  the font.
     */
    public void setFont(Font font) {
        this.font = font;
    }
    
    /**
     * Returns the paint for the annotation.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }
    
    /**
     * Sets the paint for the annotation.
     * 
     * @param paint  the paint.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Returns the text anchor.
     * 
     * @return The text anchor.
     */
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }
    
    /**
     * Sets the text anchor (the point on the text bounding rectangle that is 
     * aligned to the (x, y) coordinate of the annotation).
     * 
     * @param anchor  the anchor point.
     */
    public void setTextAnchor(TextAnchor anchor) {
        this.textAnchor = anchor;
    }
    
    /**
     * Returns the rotation anchor.
     * 
     * @return The rotation anchor point.
     */
    public TextAnchor getRotationAnchor() {
        return this.rotationAnchor;
    }
    
    /**
     * Sets the rotation anchor point.
     * 
     * @param anchor  the anchor.
     */
    public void setRotationAnchor(TextAnchor anchor) {
        this.rotationAnchor = anchor;    
    }
    
    /**
     * Returns the rotation angle.
     * 
     * @return The rotation angle.
     */
    public double getRotationAngle() {
        return this.rotationAngle; 
    }
    
    /**
     * Sets the rotation angle.
     * <p>
     * The angle is measured clockwise in radians.
     * 
     * @param angle  the angle (in radians).
     */
    public void setRotationAngle(double angle) {
        this.rotationAngle = angle;    
    }
    
    /**
     * Tests this object for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        // now try to reject equality...
        if (!(obj instanceof TextAnnotation)) {
            return false;
        }
        TextAnnotation that = (TextAnnotation) obj;
        if (!ObjectUtilities.equal(this.text, that.getText())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.font, that.getFont())) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.getPaint())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.textAnchor, that.getTextAnchor())) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.rotationAnchor, that.getRotationAnchor()
        )) {
            return false;
        }
        if (this.rotationAngle != that.getRotationAngle()) {
            return false;
        }
        
        // seem to be the same...
        return true;
            
    }
    
    /**
     * Returns a hash code for this instance.
     * 
     * @return A hash code.
     */
    public int hashCode() {
        // TODO: this needs work
        return this.text.hashCode();   
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
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
        this.paint = SerialUtilities.readPaint(stream);
    }

}
