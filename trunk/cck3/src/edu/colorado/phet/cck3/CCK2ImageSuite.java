/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.common.view.util.CachingImageLoader;
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

    ImageSuite lifelikeImageSuite;
    private BufferedImage closedImage;
    private String batteryImageLocation = "images/AA-battery-100.gif";
    String resistorImageLocation = "images/resistor3.gif";
    private BufferedImage resistorImage;
    CachingImageLoader imageLoader = new CachingImageLoader();

    public CCK2ImageSuite() throws IOException {
        loadImages();
    }

    public BufferedImage getParticleImage() throws IOException {
        return imageLoader.loadImage( "images/electron9.gif" );
    }

    public int getParticleImageWidth() throws IOException {
        return getParticleImage().getWidth();
    }

    private void loadImages() throws IOException {
        resistorImage = ImageLoader.loadBufferedImage( resistorImageLocation );
        BufferedImage batteryImage = ImageLoader.loadBufferedImage( batteryImageLocation );
        lifelikeImageSuite = new ImageSuite( resistorImage, batteryImage, closedImage );
    }

    public ImageSuite getLifelikeSuite() {
        return lifelikeImageSuite;
    }

    public BufferedImage getKnifeHandleImage() {
        try {
            return imageLoader.loadImage( "images/handle8.gif" );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getKnifeBoardImage() {
        try {
            return imageLoader.loadImage( "images/knifeBoard.gif" );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public BufferedImage getResistorImage() {
        return resistorImage;
    }

    public CachingImageLoader getImageLoader() {
        return imageLoader;
    }

    public BufferedImage getAmmeterImage() throws IOException {
        return imageLoader.loadImage( "images/ammeterbranch.gif" );
    }
}
