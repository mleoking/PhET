// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view;

import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 12:48:07 PM
 */
public class ImageSuite {
    BufferedImage resistorImage;
    BufferedImage batteryImage;
    BufferedImage switchImage;

    public ImageSuite(BufferedImage resistorImage, BufferedImage batteryImage, BufferedImage switchImage) {
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
        return ((double) batteryImage.getWidth()) / batteryImage.getHeight();
    }

}
