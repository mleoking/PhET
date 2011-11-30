// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

/**
 * Author: Sam Reid
 * Mar 30, 2007, 1:38:23 PM
 */
public class SkaterCharacter implements Serializable {
    private final String imageResourceName;
    private final String name;
    private final double mass;
    private final double modelHeight;
    private final double heightDivisor;

    public SkaterCharacter( String imageURL, String name, double mass, double modelHeight ) {
        this( imageURL, name, mass, modelHeight, 1.0 );
    }

    public SkaterCharacter( String imageResourceName, String name, double mass, double modelHeight, double heightDivisor ) {
        this.imageResourceName = imageResourceName;
        this.name = name;
        this.mass = mass;
        this.modelHeight = modelHeight;
        this.heightDivisor = heightDivisor;
    }

    public boolean equals( Object obj ) {
        if ( obj instanceof SkaterCharacter ) {
            SkaterCharacter sc = (SkaterCharacter) obj;
            return sc.imageResourceName.equals( imageResourceName ) &&
                   sc.name.equals( name ) &&
                   sc.mass == mass &&
                   sc.modelHeight == modelHeight &&
                   sc.heightDivisor == heightDivisor;
        }
        else {
            return false;
        }
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

    public String getImageResourceName() {
        return imageResourceName;
    }

    public BufferedImage getImage() {
        return EnergySkateParkResources.getImage( getImageResourceName() );
    }

    public double getHeightDivisor() {
        return heightDivisor;
    }
}
