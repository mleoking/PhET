/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*        Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid.skin;

import com.birosoft.liquid.LiquidLookAndFeel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;


/**
 * This class is for a skin that can be used to "skin" a component.
 * It's most important feature is to draw an image that can be scaled
 * to fit onto the component. All in all there are nine regions that
 * are treated differently. These regions can be grouped in three cases:
 * 1. The corners are not stretched at all, but take the space as described by
 * the members ulX,ulY,lrX and lrY.
 * They denote the distance from the corner of the image to the rectangle that
 * will be scaled to fit the component. This means that the rectangle (0,0)-(ulX,ulY)
 * will not be scaled at all, the rectangle (ulX,0)-(sizeX-lrX-1,ulY) is scaled only
 * horizontally and (sizeX-lrX,ulY) will not be scaled again.
 * For the upper left corner this means that
 * the Rectangle (0,0) - (ulX,ulY) is painted with the same rectangle from the skin
 * image.
 * 2. The edges of the component. These are the rectangles that fit in between the corners.
 * They will be stretched but only either horizontally or vertically.
 * 3. The center. This is the remaining space of the skin. It will be scaled both
 * horizontally and vertically.
 *
 * Note that if ulX,ulY,lrX and lrY are set to zero there will be a optimization to
 * improve speed for components that have a fixed size (The skin should be as big as
 * the component in this case).
 *
 * Each skin file consists of several images that are place next to each other.
 * The constructor for Skin takes the number of images as an argument. When the
 * skin is painted there's an argument for the index of the subimage to be used.
 * The index starts with 0.
 *
 * @see com.stefankrause.xplookandfeel.skin.SkinSimpleButtonIndexModel
 * @see com.stefankrause.xplookandfeel.skin.SkinToggleButtonIndexModel
 * */
public class Skin extends SkinElement {
    /** the number of subimages in the skin*/
    private int nrImages;

    /** the horizontal size of each subimage */
    private int hsize;

    /** the vertical size of each subimage */
    private int vsize;

    /** the distance from the left edge to the scaling region of the skin */
    private int ulX;

    /** the distance from the top edge to the scaling region of the skin */
    private int ulY;

    /** the distance from the right edge to the scaling region of the skin */
    private int lrX;

    /** the distance from the bottom edge to the scaling region of the skin */
    private int lrY;

    /** true if roundedSize==0 => optimization */
    private boolean noBorder = false;

    /**
     * Creates a new skin from the image file with fileName fileName and the number of
     * images passed in <code>nrImages</code>. The scaling region of the image is given
     * by ulX,ulY,lrX,lrY
     * @param fileName the filename of the image file
     * @param nrImages the number of subimages in the image file
     * @param ulX the distance from the left edge to the scaling region of the skin
     * @param ulY the distance from the top edge to the scaling region of the skin
     * @param lrX the distance from the right edge to the scaling region of the skin
     * @param lrY the distance from the bottom edge to the scaling region of the skin
     */
    public Skin(String fileName, int nrImages, int ulX, int ulY, int lrX,
        int lrY) {
        super(fileName, true);
        this.nrImages = nrImages;
        this.ulX = ulX;
        this.ulY = ulY;
        this.lrX = lrX;
        this.lrY = lrY;
        calculateSizes();
    }

    /**
     * Creates a new skin from the image file with fileName fileName and the number of
     * images passed in <code>nrImages</code>. The size of the corners is given
     * by roundedSize.
     * @param fileName the filename of the image file
     * @param nrImages the number of subimages in the image file
     * @param rounded the distance from the each edge to the scaling region of the skin
     */
    public Skin(String fileName, int nrImages, int roundedSize) {
        this(fileName, nrImages, roundedSize, roundedSize, roundedSize,
            roundedSize);

        if (roundedSize == 0) {
            noBorder = true;
        }
    }

    /**
     * Use the image with index index to paint the component with size sizeX,
     * sizeY
     * @param g
     * @param index index of the image in the skin file
     * @param sizeX horizontal size of the component
     * @param sizeY vertical size of the component
     */
    public void draw(Graphics g, int index, int sizeX, int sizeY) {
        Graphics2D g2 = (Graphics2D) g;
        int offset = index * getHsize();

        if (!noBorder) {
            // lo
            g2.drawImage(getImage(), 0, 0, ulX, ulY, offset + 0, 0,
                offset + ulX, ulY, null);

            // mo
            g2.drawImage(getImage(), ulX, 0, sizeX - lrX, ulY, offset + ulX, 0,
                (offset + hsize) - lrX, ulY, null);

            // ro
            g2.drawImage(getImage(), sizeX - lrX, 0, sizeX, ulY,
                (offset + hsize) - lrX, 0, offset + hsize, ulY, null);

            // lm
            g2.drawImage(getImage(), 0, ulY, ulX, sizeY - lrY, offset + 0, ulY,
                offset + ulX, vsize - lrY, null);

            // rm
            g2.drawImage(getImage(), sizeX - lrX, ulY, sizeX, sizeY - lrY,
                (offset + hsize) - lrX, ulY, offset + hsize, vsize - lrY, null);

            // lu
            g2.drawImage(getImage(), 0, sizeY - lrY, ulX, sizeY, offset + 0,
                vsize - lrY, offset + ulX, vsize, null);

            // mu
            g2.drawImage(getImage(), ulX, sizeY - lrY, sizeX - lrX, sizeY,
                offset + ulX, vsize - lrY, (offset + hsize) - lrX, vsize, null);

            // ru
            g2.drawImage(getImage(), sizeX - lrX, sizeY - lrY, sizeX, sizeY,
                (offset + hsize) - lrX, vsize - lrY, offset + hsize, vsize, null);

            g2.drawImage(getImage(), ulX, ulY, sizeX - lrX, sizeY - lrY,
                offset + ulX, ulY, (offset + hsize) - lrX, vsize - lrY, null);
        } else {
            g.drawImage(getImage(), 0, 0, sizeX, sizeY, offset, 0,
                offset + hsize, vsize, null);
        }

        //		System.out.println("TIME :" + (summedTime/timesCalled) );
    }

    /**
     * Use the image with index index to paint the component at point x,
     * y with dimension sizeX, sizeY
     * @param g
     * @param index index of the image in the skin file
     * @param x x coordiante of the point where the skin is painted
     * @param y y coordiante of the point where the skin is painted
     * @param sizeX horizontal size of the component
     * @param sizeY vertical size of the component
     */
    public void draw(Graphics g, int index, int x, int y, int sizeX, int sizeY) {
        int offset = index * getHsize();

        if (!noBorder) {
            // lo
            g.drawImage(getImage(), x + 0, y + 0, x + ulX, y + ulY, offset + 0,
                0, offset + ulX, ulY, null);

            // mo
            g.drawImage(getImage(), x + ulX, y + 0, (x + sizeX) - lrX, y + ulY,
                offset + ulX, 0, (offset + hsize) - lrX, ulY, null);

            // ro
            g.drawImage(getImage(), (x + sizeX) - lrX, y + 0, x + sizeX,
                y + ulY, (offset + hsize) - lrX, 0, offset + hsize, ulY, null);

            // lm
            g.drawImage(getImage(), x + 0, y + ulY, x + ulX, (y + sizeY) - lrY,
                offset + 0, ulY, offset + ulX, vsize - lrY, null);

            // rm
            g.drawImage(getImage(), (x + sizeX) - lrX, y + ulY, x + sizeX,
                (y + sizeY) - lrY, (offset + hsize) - lrX, ulY, offset + hsize,
                vsize - lrY, null);

            // lu
            g.drawImage(getImage(), x + 0, (y + sizeY) - lrY, x + ulX,
                y + sizeY, offset + 0, vsize - lrY, offset + ulX, vsize, null);

            // mu
            g.drawImage(getImage(), x + ulX, (y + sizeY) - lrY,
                (x + sizeX) - lrX, y + sizeY, offset + ulX, vsize - lrY,
                (offset + hsize) - lrX, vsize, null);

            // ru
            g.drawImage(getImage(), (x + sizeX) - lrX, (y + sizeY) - lrY,
                x + sizeX, y + sizeY, (offset + hsize) - lrX, vsize - lrY,
                offset + hsize, vsize, null);
            g.drawImage(getImage(), x + ulX, y + ulY, (x + sizeX) - lrX,
                (y + sizeY) - lrY, offset + ulX, ulY, (offset + hsize) - lrX,
                vsize - lrY, null);
        } else {
            g.drawImage(getImage(), x, y, x + sizeX, y + sizeY, offset, 0,
                offset + hsize, vsize, null);
        }
    }

    /**
     * Use the image with index index to paint the component with its
     * natural size centred in a rectangle with dimension sizeX, sizeY
     * @param g
     * @param index index of the image in the skin file
     * @param sizeX horizontal size of the component
     * @param sizeY vertical size of the component
     */
    public void drawCentered(Graphics g, int index, int sizeX, int sizeY) {
        int offset = index * getHsize();

        int w = getHsize();
        int h = getVsize();

        int sx = (sizeX - w) / 2;
        int sy = (sizeY - h) / 2;

        g.drawImage(getImage(), sx, sy, sx + w, sy + h, offset, 0, offset + w,
            h, null);
    }

    /**
     * Use the image with index index to paint the component with its
     * natural size centred in a rectangle with dimension sizeX, sizeY
     * @param g
     * @param index index of the image in the skin file
     * @param x x coordiante of the point where the skin is painted
     * @param y y coordiante of the point where the skin is painted
     *  @param    sizeX horizontal size of the component
     * @param sizeY vertical size of the component
     */
    public void drawCentered(Graphics g, int index, int x, int y, int sizeX,
        int sizeY) {
        int offset = index * getHsize();

        int w = getHsize();
        int h = getVsize();

        int sx = (sizeX - w) / 2;
        int sy = (sizeY - h) / 2;

        g.drawImage(getImage(), x + sx, y + sy, x + sx + w, y + sy + h, offset,
            0, offset + w, h, null);
    }

    /**
     * Returns the horizontal size of the skin, this is the width of each subimage
     * @return int
     */
    public int getHsize() {
        return hsize;
    }

    /**
     * Returns the vertical size of the skin, this is the height of each subimage
     * @return int
     */
    public int getVsize() {
        return vsize;
    }

    /**
     * Returns the size of the skin, this is the height of each subimage
     * @return Dimension
     */
    public Dimension getSize() {
        return new Dimension(hsize, vsize);
    }

    /**
     * Calculates the size for each subimage
     */
    protected void calculateSizes() {
        hsize = (getImage().getWidth(null)) / nrImages;
        vsize = getImage().getHeight(null);
    }

    Color dark(Color c, int factor) {
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

        if ((factor <= 0) || (c.getAlpha() < 255)) {
            return c;
        } else if (factor < 100) {
            return light(c, 10000 / factor);
        }

        int vi = (int) (hsv[2] * 255);

        vi = (100 * vi) / factor;

        float v = (float) vi / 255;

        return Color.getHSBColor(hsv[0], hsv[1], v);
    }

    Color light(Color c, int factor) {
        if (factor <= 0) {
            return c;
        } else if (factor < 100) {
            return dark(c, 10000 / factor);
        }

        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

        float s = hsv[1];
        float v = hsv[2];

        System.out.print("LIGHT V : " + v);

        v = (factor * v) / 100;

        if (v > 1) {
            v = 1;
        }

        if (v > 255) {
            s -= (v - 255);

            if (s < 0) {
                s = 0;
            }

            v = 255;
        }

        return Color.getHSBColor(hsv[0], hsv[1], v);
    }

    Color colour(Color src, Color bg) {
        boolean blend = false;
        float srcPercent;
        float destPercent;
        int delta;
        int destR;
        int destG;
        int destB;
        int alpha;
        int srcR = src.getRed();
        int srcG = src.getGreen();
        int srcB = src.getBlue();

        /*
        srcR += (((bg.getRed() == bg.getGreen()) &&
        (bg.getRed() == bg.getBlue())) ? 19 : 9);
        srcG += (((bg.getRed() == bg.getGreen()) &&
        (bg.getRed() == bg.getBlue())) ? 19 : 9);
        srcB += (((bg.getRed() == bg.getGreen()) &&
        (bg.getRed() == bg.getBlue())) ? 19 : 9);
         */
        srcR += 20;
        srcG += 20;
        srcB += 20;
        alpha = bg.getAlpha();
        delta = 255 - bg.getRed();
        destR = srcR - delta;
        destG = srcG - delta;
        destB = srcB - delta;

        if (destR < 0) {
            destR = 0;
        }

        if (destG < 0) {
            destG = 0;
        }

        if (destB < 0) {
            destB = 0;
        }

        if (destR > 255) {
            destR = 255;
        }

        if (destG > 255) {
            destG = 255;
        }

        if (destB > 255) {
            destB = 255;
        }

        if (blend && (alpha != 255) && (alpha != 0)) {
            srcPercent = ((float) alpha) / 255.0f;
            destPercent = 1.0f - srcPercent;
            destR = (int) ((srcPercent * destR) + (destPercent * bg.getRed()));
            destG = (int) ((srcPercent * destG) +
                (destPercent * bg.getGreen()));
            destB = (int) ((srcPercent * destB) + (destPercent * bg.getBlue()));
            alpha = 255;
        }

        return new Color(destR, destG, destB, alpha);
    }

    private Color colourSinglePixel(int x, int y, int pixel, Color c) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return colour(c, new Color(red, green, blue, alpha));
    }

    public void colourImage() {
        int x = 0;
        int y = 0;
        int w = hsize * nrImages;
        int h = vsize;
        BufferedImage newImage = new BufferedImage(w, h,
                BufferedImage.TYPE_4BYTE_ABGR);

        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(getImage(), x, y, w, h, pixels, 0, w);

        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");

            return;
        }

        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");

            return;
        }

        Graphics g = newImage.getGraphics();
        Color colourWith = null;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if (i == 0 || i == 117) {
                    colourWith = LiquidLookAndFeel.getBackgroundColor();
                } else if (i == 39 || i == 156) {
                    colourWith = LiquidLookAndFeel.getButtonBackground();
                } else if (i == 78) {
                    colourWith = dark(LiquidLookAndFeel.getButtonBackground(),
                            115);
                }

                g.setColor(colourSinglePixel(x + i, y + j, pixels[(j * w) + i],
                        colourWith));
                g.drawLine(x + i, y + j, x + i, y + j);
            }

        }

        setImage(newImage);
    }
}
