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

import java.awt.Image;


/**
 * The base class for <code>Skin</code> and <code>SkinMenuItem</code>. It's
 * responsibility is to handle the loading of the image from the cache.
 */
public class SkinElement {
    private String filename;
    private Image image;

    public SkinElement(String filename, boolean useAutomaticBitmap) {
        this.filename = filename;

        if (useAutomaticBitmap) {
            image = SkinImageCache.getInstance().getAutomaticImage(filename);
        } else {
            image = SkinImageCache.getInstance().getImage(filename);
        }
    }

    /**
     * returns the filename for the skin file.
     * @return String
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename for the skin file
     * @param filename the filename for the skin file
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * returns the image for the skin
     * @return Image
     */
    public Image getImage() {
        return image;
    }

    protected void setImage(Image image) {
        this.image = image;
    }
}
