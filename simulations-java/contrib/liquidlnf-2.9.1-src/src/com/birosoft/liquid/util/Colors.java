/*
 * Colors.java
 *
 * Created on Среда, 2004, Мај 12, 17.10
 */
package com.birosoft.liquid.util;

import com.birosoft.liquid.skin.*;

import java.awt.*;
import java.awt.image.*;

import javax.swing.JComponent;


/**
 *
 * @author  mikeai
 */
public class Colors {
    /** Creates a new instance of Colors */
    static Image image;

    /** Creates a new instance of Colors */
    static Image newImage;
    static BufferedImage clearFill;
    static Color buttonBg = new Color(215, 231, 249);
    static Color bg = new Color(246, 245, 244);

    public Colors() {
    }

    static public Image getImage() {
        return newImage;
    }

    static public BufferedImage getClearFill() {
        return clearFill;
    }

    static public void drawStipples(Graphics g, JComponent c, Color bg) {
        g.setColor(Colors.dark(bg, 103));

        int i = 0;
        int height = c.getHeight();

        while (i < height) {
            g.drawLine(0, i, c.getWidth() - 1, i);
            i++;
            g.drawLine(0, i, c.getWidth() - 1, i);
            i += 3;
        }
    }

    static Color handlesinglepixel(int x, int y, int pixel, Color c) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return Colors.liquidAlpha(c, new Color(red, green, blue, alpha));
    }

    public static void getPixels() {
        SkinImageCache sic = SkinImageCache.getInstance();
        image = sic.getAutomaticImage("button.png");
        clearFill = sic.getBufferedImage("clear_fill.png");

        int x = 0;
        int y = 0;
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        newImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);

        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(image, x, y, w, h, pixels, 0, w);

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
                if ((i == 0) || (i == 117)) {
                    colourWith = bg;
                } else if ((i == 39) || (i == 156)) {
                    colourWith = buttonBg;
                } else if (i == 78) {
                    colourWith = dark(buttonBg, 115);
                }

                g.setColor(handlesinglepixel(x + i, y + j, pixels[(j * w) + i],
                        colourWith));
                g.drawLine(x + i, y + j, x + i, y + j);
            }
        }
    }

    public static Color dark(Color c, int factor) {
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

    public static Color light(Color c, int factor) {
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

    public static Color liquidAlpha(Color c, Color bg) {
        boolean blend = false;
        float srcPercent;
        float destPercent;
        int delta;
        int destR;
        int destG;
        int destB;
        int alpha;
        int srcR = c.getRed();
        int srcG = c.getGreen();
        int srcB = c.getBlue();

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

    /*
     * This code serves for internal testing purposes
     *
    static public void main(String[] args) {
        /*
        Color buttonBg = new Color(215, 231, 249);
        Color bitmapBg = new Color(225, 224, 223);
        Color alpha = Colors.liquidAlpha(buttonBg, bitmapBg);
        System.out.println(alpha);
        bitmapBg = Color.white;
        alpha = Colors.liquidAlpha(buttonBg, bitmapBg);
        System.out.println(alpha);
         */

        //Color c = new Color(72, 88, 106);
        //System.out.println(Colors.dark(c, 135));
    /*
        Colors.getPixels();

        javax.swing.JFrame f = new javax.swing.JFrame();
        f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        Panel p = new Panel(Colors.getImage());
        //Panel p = new Panel(img);
        f.setSize(300, 205);
        f.getContentPane().add(p);
        f.show();
    }*/
}
