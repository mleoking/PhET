/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.common.view.util.HashedImageLoader;
import edu.colorado.phet.common.view.util.ImageLoader;

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
    HashedImageLoader imageLoader = new HashedImageLoader();
    private BufferedImage bulb;

    public CCK2ImageSuite() throws IOException {
        loadImages();
    }

    public BufferedImage getParticleImage() throws IOException {
//        return imageLoader.loadImage( "images/spheres/particle-blue-sml.gif" );
//        return imageLoader.loadImage( "images/spheres/myelectron4.gif" );
//        return imageLoader.loadImage( "images/spheres/myelectron6.gif" );
//        return imageLoader.loadImage( "images/spheres/electron5.gif" );
        return imageLoader.loadImage( "images/spheres/electron9.gif" );
    }

    public int getParticleImageWidth() throws IOException {
        return getParticleImage().getWidth();
    }

    private void loadImages() throws IOException {
        resistorImage = ImageLoader.loadBufferedImage( resistorImageLocation );
        BufferedImage batteryImage = ImageLoader.loadBufferedImage( batteryImageLocation );
        lifelikeImageSuite = new ImageSuite( resistorImage, batteryImage, closedImage );
        BufferedImage schr = ImageLoader.loadBufferedImage( schematicResistorImage );
        BufferedImage schswit = ImageLoader.loadBufferedImage( switchImage );
        BufferedImage schbatt = imageLoader.loadImage( schematicBatteryImage );
        schematicImageSuite = new ImageSuite( schr, schbatt, schswit );
    }

    public ImageSuite getLifelikeSuite() {
        return lifelikeImageSuite;
    }

    public ImageSuite getSchematicSuite() {
        return schematicImageSuite;
    }

    public BufferedImage getKnifeHandleImage() {
        try {
            return imageLoader.loadImage( "images/switches/handle8.gif" );
        }
        catch( IOException e ) {
//            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getKnifeBoardImage() {
        try {
            return imageLoader.loadImage( "images/switches/knifeBoard.gif" );
        }
        catch( IOException e ) {
//            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getImageHandle() throws IOException {
        return imageLoader.loadImage( "images/switches/handle4.gif" );
    }

    public BufferedImage getResistorImage() {
        return resistorImage;
    }

    public HashedImageLoader getImageLoader() {
        return imageLoader;
    }

    public BufferedImage getAmmeterImage() throws IOException {
        return imageLoader.loadImage( "images/ammeterbranch.gif" );
    }
}
