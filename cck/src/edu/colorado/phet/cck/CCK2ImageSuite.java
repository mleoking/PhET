/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.ImageSuite;
import edu.colorado.phet.cck.util.CommonImageLoader;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 9:38:59 PM
 * Copyright (c) Dec 6, 2003 by Sam Reid
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
    private edu.colorado.phet.cck.util.ImageLoader imageLoader;
    private BufferedImage ammeterImage;

    public BufferedImage getParticleImage() {
        if (particleImage == null) {
            particleImage = new ImageLoader().loadBufferedImage("images/spheres/particle-blue-sml.gif");
        }
        return particleImage;
    }

    public int getParticleImageWidth() {
        getParticleImage();
        return particleImage.getWidth();
    }

    public CCK2ImageSuite() throws IOException {
        this.imageLoader = new CommonImageLoader2();
        loadImages();
    }

    private void loadImages() throws IOException {
        resistorImage = imageLoader.loadBufferedImage(resistorImageLocation);
        BufferedImage batteryImage = imageLoader.loadBufferedImage(batteryImageLocation);
        lifelikeImageSuite = new ImageSuite(resistorImage, batteryImage, closedImage);
        BufferedImage schr = imageLoader.loadBufferedImage(schematicResistorImage);
        BufferedImage schswit = imageLoader.loadBufferedImage(switchImage);
        BufferedImage schbatt = ImageIO.read(getClass().getClassLoader().getResource(schematicBatteryImage));
        schematicImageSuite = new ImageSuite(schr, schbatt, schswit);
    }

    public ImageSuite getLifelikeSuite() {
        return lifelikeImageSuite;
    }

    public ImageSuite getSchematicSuite() {
        return schematicImageSuite;
    }

    public BufferedImage getClosedSwitchImage() {
        if (closedImage == null) {
            closedImage = new ImageLoader().loadBufferedImage("images/switches/knifeBoard.gif");
        }
        return closedImage;
    }

    public BufferedImage getBaseSwitchImage() {
        if (openImage == null) {
            openImage = new ImageLoader().loadBufferedImage("images/switches/knifeBoard.gif");
        }
        return openImage;
    }

    public BufferedImage getBulbImage() throws IOException {
        if (bulbImage == null) {
//            bulbImage=imageLoader.loadBufferedImage("images/schematic/bulb-knob.gif");//Loads smooth alpha, not bitchannel.
            bulbImage = new CommonImageLoader().loadBufferedImage("images/schematic/bulb-knob.gif");//Loads smooth alpha, not bitchannel.
            //DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000
            //versus:
            //DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=10000.....
        }
        return bulbImage;
    }

    public BufferedImage getImageHandle() {
        if (imageHandle == null) {
//        ImageLoader loader = new ImageLoader();
            imageHandle = new ImageLoader().loadBufferedImage("images/switches/handle4.gif");
        }
        return imageHandle;
    }

    public BufferedImage getResistorImage() {
        return resistorImage;
    }

    public edu.colorado.phet.cck.util.ImageLoader getImageLoader() {
        return imageLoader;
    }

    public BufferedImage getAmmeterImage() {
        if (ammeterImage != null)
            return ammeterImage;
        ammeterImage = new ImageLoader().loadBufferedImage("images/ammeterbranch.gif");
        return ammeterImage;
    }
}
