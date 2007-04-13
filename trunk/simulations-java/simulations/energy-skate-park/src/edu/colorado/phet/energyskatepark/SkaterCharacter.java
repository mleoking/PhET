package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 1:38:23 PM
 */
public class SkaterCharacter {
    private String imageURL;
    private String name;
    private double mass;
    private double modelHeight;
    private double heightDivisor;

    public SkaterCharacter( String imageURL, String name, double mass, double modelHeight ) {
        this( imageURL, name, mass, modelHeight, 1.0 );
    }

    public SkaterCharacter( String imageURL, String name, double mass, double modelHeight, double heightDivisor ) {
        this.imageURL = imageURL;
        this.name = name;
        this.mass = mass;
        this.modelHeight = modelHeight;
        this.heightDivisor = heightDivisor;
    }

    public double getModelWidth() {
        return ( modelHeight / getImage().getHeight() ) * getImage().getWidth();
    }

    public double getModelHeight() {
        return modelHeight;
    }

    public String getName() {
        return name;
    }

    public double getMass() {
        return mass;
    }

    public String getImageURL() {
        return imageURL;
    }

    public BufferedImage getImage() {
        try {
            return ImageLoader.loadBufferedImage( getImageURL() );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public double getHeightDivisor() {
        return heightDivisor;
    }
}
