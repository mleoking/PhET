/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.ImageSuite;
import edu.colorado.phet.cck.util.CommonImageLoader;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.util.graphics.HashedImageLoader;
import edu.colorado.phet.common.CommonImageLoader2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 9:38:59 PM
 * Copyright (c) Dec 6, 2003 by Sam Reid
 * Testing cvs.
 */
public class CCK2ImageSuite {
    ImageSuite schematicImageSuite;
    ImageSuite lifelikeImageSuite;
    private BufferedImage bulbImage;
    private BufferedImage closedImage;
    private BufferedImage openImage;
    private BufferedImage imageHandle;

    private String batteryImageLocation = "images/batteries/AA-battery-100.gif";
    private String schematicResistorImage = "images/schematic/resistor-transparent.gif";
    private String switchImage = "images/schematic/switch-closed.gif";
    private String schematicBatteryImage = "images/schematic/battery-transparent.gif";
    String resistorImageLocation = "images/lifelike/resistor3.gif";
    private BufferedImage resistorImage;
    private BufferedImage particleImage;
    private BufferedImage ammeterImage;
    HashedImageLoader imageLoader=new HashedImageLoader();

    public BufferedImage getParticleImage() throws IOException {
        return imageLoader.loadImage("images/spheres/particle-blue-sml.gif");
    }

    public int getParticleImageWidth() throws IOException {
        return getParticleImage().getWidth();
    }

    public CCK2ImageSuite() throws IOException {
        loadImages();
    }

    private void loadImages() throws IOException {
        resistorImage = imageLoader.loadBufferedImage(resistorImageLocation);
        BufferedImage batteryImage = imageLoader.loadBufferedImage(batteryImageLocation);
        lifelikeImageSuite = new ImageSuite(resistorImage, batteryImage, closedImage);
        BufferedImage schr = imageLoader.loadBufferedImage(schematicResistorImage);
        BufferedImage schswit = imageLoader.loadBufferedImage(switchImage);
//        BufferedImage schbatt = ImageIO.read(getClass().getClassLoader().getResource(schematicBatteryImage));
        BufferedImage schbatt=imageLoader.loadImage(schematicBatteryImage);
        schematicImageSuite = new ImageSuite(schr, schbatt, schswit);
    }

    public ImageSuite getLifelikeSuite() {
        return lifelikeImageSuite;
    }

    public ImageSuite getSchematicSuite() {
        return schematicImageSuite;
    }

    public BufferedImage getClosedSwitchImage() throws IOException {
        return imageLoader.loadImage("images/switches/knifeBoard.gif");
    }

    public BufferedImage getBaseSwitchImage() throws IOException {
        return imageLoader.loadImage("images/switches/knifeBoard.gif");
    }

    public BufferedImage getBulbImage() throws IOException {
            return imageLoader.loadImage("images/schematic/bulb-knob.gif");//Loads smooth alpha, not bitchannel.
    }

    public BufferedImage getImageHandle() throws IOException {
        return imageLoader.loadImage("images/switches/handle4.gif");
    }

    public BufferedImage getResistorImage() {
        return resistorImage;
    }

    public HashedImageLoader getImageLoader() {
        return imageLoader;
    }
//    public edu.colorado.phet.cck.util.ImageLoader getImageLoader() {
//        return imageLoader;
//    }

    public BufferedImage getAmmeterImage() throws IOException {
        return imageLoader.loadImage("images/ammeterbranch.gif");
    }
}
