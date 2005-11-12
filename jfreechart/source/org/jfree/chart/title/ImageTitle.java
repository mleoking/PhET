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
 * ---------------
 * ImageTitle.java
 * ---------------
 * (C) Copyright 2000-2005, by David Berry and Contributors;
 *
 * Original Author:  David Berry;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header (DG);
 * 07-Nov-2001 : Separated the JCommon Class Library classes, JFreeChart now 
 *               requires jcommon.jar (DG);
 * 09-Jan-2002 : Updated Javadoc comments (DG);
 * 07-Feb-2002 : Changed blank space around title from Insets --> Spacer, to 
 *               allow for relative or absolute spacing (DG);
 * 25-Jun-2002 : Updated import statements (DG);
 * 23-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Nov-2002 : Added method for drawing images at left or right (DG);
 * 22-Sep-2003 : Added checks that the Image can never be null (TM).
 * 11-Jan-2005 : Removed deprecated code in preparation for the 1.0.0 
 *               release (DG);    
 * 02-Feb-2005 : Changed padding mechanism for all titles (DG);
 * 20-Apr-2005 : Added new draw() method (DG);   
 * 
 */

package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;

/**
 * A chart title that displays an image.  This is useful, for example, if you
 * have an image of your corporate logo and want to use as a footnote or part
 * of a title in a chart you create.
 * <P>
 * ImageTitle needs an image passed to it in the constructor.  For ImageTitle
 * to work, you must have already loaded this image from its source (disk or
 * URL).  It is recomended you use something like
 * Toolkit.getDefaultToolkit().getImage() to get the image.  Then, use
 * MediaTracker or some other message to make sure the image is fully loaded
 * from disk.
 *
 * @author David Berry
 */
public class ImageTitle extends Title {

    /** The title image. */
    private Image image;

    /** The height used to draw the image (may involve scaling). */
    private int height;

    /** The width used to draw the image (may involve scaling). */
    private int width;

    /**
     * Creates a new image title.
     *
     * @param image  the image.
     */
    public ImageTitle(Image image) {

        this(
            image,
            image.getHeight(null),
            image.getWidth(null),
            Title.DEFAULT_POSITION,
            Title.DEFAULT_HORIZONTAL_ALIGNMENT,
            Title.DEFAULT_VERTICAL_ALIGNMENT,
            Title.DEFAULT_PADDING
        );

    }

    /**
     * Creates a new image title.
     *
     * @param image  the image.
     * @param position  the title position.
     * @param horizontalAlignment  the horizontal alignment.
     * @param verticalAlignment  the vertical alignment.
     */
    public ImageTitle(Image image, RectangleEdge position, 
                      HorizontalAlignment horizontalAlignment, 
                      VerticalAlignment verticalAlignment) {

        this(
            image,
            image.getHeight(null),
            image.getWidth(null),
            position,
            horizontalAlignment,
            verticalAlignment,
            Title.DEFAULT_PADDING
        );

    }

    /**
     * Creates a new image title with the given image scaled to the given
     * width and height in the given location.
     *
     * @param image  the image (not null).
     * @param height  the height used to draw the image.
     * @param width  the width used to draw the image.
     * @param position  the title position.
     * @param horizontalAlignment  the horizontal alignment.
     * @param verticalAlignment  the vertical alignment.
     * @param padding  the amount of space to leave around the outside of the 
     *                 title.
     */
    public ImageTitle(Image image, int height, int width, 
                      RectangleEdge position,
                      HorizontalAlignment horizontalAlignment, 
                      VerticalAlignment verticalAlignment,
                      RectangleInsets padding) {

        super(position, horizontalAlignment, verticalAlignment, padding);
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        this.height = height;
        this.width = width;

    }

    /**
     * Returns the image for the title.
     *
     * @return The image for the title.
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * Sets the image for the title and notifies registered listeners that the
     * title has been modified.
     *
     * @param image  the new image (<code>null</code> not permitted).
     */
    public void setImage(Image image) {
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param titleArea  the area within which the title (and plot) should be 
     *                   drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D titleArea) {

        RectangleEdge position = getPosition();
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            drawHorizontal(g2, titleArea);
        }
        else if (position == RectangleEdge.LEFT 
                     || position == RectangleEdge.RIGHT) {
            drawVertical(g2, titleArea);
        }
        else {
            throw new RuntimeException("Invalid title position.");
        }
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param chartArea  the area within which the title (and plot) should be 
     *                   drawn.
     *
     * @return The size of the area used by the title.
     */
    protected Size2D drawHorizontal(Graphics2D g2, Rectangle2D chartArea) {

        double startY = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;

        RectangleInsets padding = getPadding();
        topSpace = padding.calculateTopOutset(this.height);
        bottomSpace = padding.calculateBottomOutset(this.height);
        leftSpace = padding.calculateLeftOutset(this.width);
        rightSpace = padding.calculateRightOutset(this.width);

        if (getPosition() == RectangleEdge.TOP) {
            startY = chartArea.getY() + topSpace;
        }
        else {
            startY = chartArea.getY() + chartArea.getHeight() - bottomSpace 
                     - this.height;
        }

        // what is our alignment?
        HorizontalAlignment horizontalAlignment = getHorizontalAlignment();
        double startX = 0.0;
        if (horizontalAlignment == HorizontalAlignment.CENTER) {
            startX = chartArea.getX() + leftSpace + chartArea.getWidth() / 2.0 
                     - this.width / 2.0;
        }
        else if (horizontalAlignment == HorizontalAlignment.LEFT) {
            startX = chartArea.getX() + leftSpace;
        }
        else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
            startX = chartArea.getX() + chartArea.getWidth() - rightSpace 
                     - this.width;
        }
        g2.drawImage(
            this.image, (int) startX, (int) startY, 
            this.width, this.height, null
        );

        return new Size2D(
            chartArea.getWidth() + leftSpace + rightSpace,
            this.height + topSpace + bottomSpace
        );

    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param chartArea  the area within which the title (and plot) should be 
     *                   drawn.
     *
     * @return The size of the area used by the title.
     */
    protected Size2D drawVertical(Graphics2D g2, Rectangle2D chartArea) {

        double startX = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;

        RectangleInsets padding = getPadding();
        if (padding != null) {
            topSpace = padding.calculateTopOutset(this.height);
            bottomSpace = padding.calculateBottomOutset(this.height);
            leftSpace = padding.calculateLeftOutset(this.width);
            rightSpace = padding.calculateRightOutset(this.width);
        }

        if (getPosition() == RectangleEdge.LEFT) {
            startX = chartArea.getX() + leftSpace;
        }
        else {
            startX = chartArea.getMaxX() - rightSpace - this.width;
        }

        // what is our alignment?
        VerticalAlignment alignment = getVerticalAlignment();
        double startY = 0.0;
        if (alignment == VerticalAlignment.CENTER) {
            startY = chartArea.getMinY() + topSpace 
                     + chartArea.getHeight() / 2.0
                     - this.height / 2.0;
        }
        else if (alignment == VerticalAlignment.TOP) {
            startY = chartArea.getMinY() + topSpace;
        }
        else if (alignment == VerticalAlignment.BOTTOM) {
            startY = chartArea.getMaxY() - bottomSpace - this.height;
        }

        g2.drawImage(
            this.image, (int) startX, (int) startY, 
            this.width, this.height, null
        );

        return new Size2D(
            chartArea.getWidth() + leftSpace + rightSpace,
            this.height + topSpace + bottomSpace
        );

    }
    
    /**
     * Draws the block within the specified area.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  ignored (<code>null</code> permitted).
     * 
     * @return Always <code>null</code>.
     */
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        draw(g2, area);
        return null;
    }

}
