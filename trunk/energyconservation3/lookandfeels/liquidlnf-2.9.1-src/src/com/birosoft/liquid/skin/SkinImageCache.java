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


/*
 * Due to rising concerns regaring the usage of Microsoft pictures in the
 * xp look and feel I decided to obfuscate the pictures and make them available
 * via the class SecretLoad which is not supplied as source code. If you
 * want to extend xp look and feel, please use
 * Image img = java.awt.Toolkit.getDefaultToolkit().createImage(url);
 * to load your images.s
 *
 */
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.util.HashMap;


/**
 * A cache for the skin images. It is used as a singleton.
 *
 */
public class SkinImageCache {
    private static SkinImageCache instance = new SkinImageCache();
    static GraphicsConfiguration conf;

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        conf = ge.getDefaultScreenDevice().getDefaultConfiguration();
    }

    private HashMap map;
    private HashMap iconMap;
    private HashMap bufferedMap;

    protected SkinImageCache() {
        map = new HashMap();
        iconMap = new HashMap();
        bufferedMap = new HashMap();
    }

    /**
     * Loads the image file with fileName <code>fileName</code> as an automatic image.
     * For images with bitmask transparency or no transparency the image should be
     * hardware accelerated.
     * @param fileName the file name of the image file to load
     * @return Image
     */
    public Image getAutomaticImage(String fileName) {
        Image ret = (Image) map.get(fileName);

        if (ret == null) {
            Image img = SecretLoader.loadImage(fileName);
            map.put(fileName, img);

            return img;
        }

        return ret;
    }

    /** Loads the image file with fileName <code>fileName</code>.
     * @param fileName the file name of the image file to load
     * @return Image
     */
    public Image getImage(String fileName) {
        return getAutomaticImage(fileName);
    }

    /**
     * Loads the image file with fileName <code>fileName</code> as an buffered image.
     * This is basically not hardware accelerated.
     * @param fileName the file name of the image file to load
     * @return Image
     */
    public BufferedImage getBufferedImage(String fileName) {
        BufferedImage b = (BufferedImage) bufferedMap.get(fileName);

        if (b != null) {
            return b;
        }

        Image img = getImage(fileName);

        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        int w = img.getWidth(null);
        int h = img.getHeight(null);

        BufferedImage img2 = conf.createCompatibleImage(w, h);
        Graphics g = img2.getGraphics();
        g.drawImage(img, 0, 0, w, h, 0, 0, w, h, null);
        bufferedMap.put(fileName, img2);

        return img2;
    }

    /**
     * Returns the only instance of the image cache
     * @return SkinImageCache
     */
    public static SkinImageCache getInstance() {
        return instance;
    }
}
