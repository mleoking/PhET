// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:17:59 AM
 */

public class FaucetData {
    private String faucetImageName;
    private double fractionalDistToOpeningX;
    private double fractionalDistToOpeningY;

    public FaucetData( String faucetImageName, double fractionalDistToOpeningX, double fractionalDistToOpeningY ) {
        this.faucetImageName = faucetImageName;
        this.fractionalDistToOpeningX = fractionalDistToOpeningX;
        this.fractionalDistToOpeningY = fractionalDistToOpeningY;
    }

    public String getFaucetImageName() {
        return faucetImageName;
    }

    public double getFractionalDistToOpeningX() {
        return fractionalDistToOpeningX;
    }

    public double getFractionalDistToOpeningY() {
        return fractionalDistToOpeningY;
    }

    public double getDistToOpeningX( double imageWidth ) {
        return getFractionalDistToOpeningX() * imageWidth;
    }

    public double getDistToOpeningY( double imageHeight ) {
        return getFractionalDistToOpeningY() * imageHeight;
    }

    public double getDistToOpeningX( Image image ) {
        return getDistToOpeningX( image.getWidth( null ) );
    }

    public double getDistToOpeningY( Image image ) {
        return getDistToOpeningY( image.getHeight( null ) );
    }
}
