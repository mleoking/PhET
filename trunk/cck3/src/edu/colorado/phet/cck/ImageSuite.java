/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck;

import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:48:07 PM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public class ImageSuite {
    BufferedImage resistorImage;
    BufferedImage batteryImage;
    BufferedImage switchImage;

    public ImageSuite( BufferedImage resistorImage, BufferedImage batteryImage, BufferedImage switchImage ) {
        this.resistorImage = resistorImage;
        this.batteryImage = batteryImage;
        this.switchImage = switchImage;
    }

    public BufferedImage getResistorImage() {
        return resistorImage;
    }

    public BufferedImage getBatteryImage() {
        return batteryImage;
    }

    public double getBatteryImageAspectRatio() {
        double w = batteryImage.getWidth();
        double h = batteryImage.getHeight();
        return w / h;
    }

    public BufferedImage getSwitchImage() {
        return switchImage;
    }
}
