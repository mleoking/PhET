/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 1:01:19 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */

public class EdgeIntensityReader implements DGIntensityReader {
    private int inset = 5;
    private DGModel dgModel;

    public EdgeIntensityReader( DGModel dgModel ) {
        this.dgModel = dgModel;
    }

    public double getIntensity( double angle ) {
        Point gridLocation = toGridLocation( angle );
//        System.out.println( "angle = " + angle +", gridlocation="+gridLocation);
        Complex value = getWavefunction().valueAt( gridLocation.x, gridLocation.y );
        return value.abs();
    }

    private Wavefunction getWavefunction() {
        return dgModel.getWavefunction();
    }

    private Point toGridLocation( double degrees ) {
        double threshold = getThreshold();
//        System.out.println( "threshold = " + threshold );
        if( degrees < threshold ) {
            return toGridLocationBottom( degrees );
        }
        else {
            return toGridLocationSide( degrees );
        }
    }

    private double getThreshold() {
        return Math.toDegrees( Math.atan2( getWavefunction().getWidth() / 2, getDistanceFromSouthToCenterAtom() ) );
    }

    private int getDistanceFromSouthToCenterAtom() {
        return getWavefunction().getHeight() - dgModel.getCenterAtomPoint().y;
    }

    private Point toGridLocationSide( double degrees ) {
        int yoffset = (int)( ( getWavefunction().getWidth() / 2 - inset ) * Math.tan( Math.toRadians( 90 - degrees ) ) );
        return new Point( getWavefunction().getWidth() - inset, dgModel.getCenterAtomPoint().y + yoffset );
    }

    private Point toGridLocationBottom( double degrees ) {
        int xoffset = (int)( ( getDistanceFromSouthToCenterAtom() - inset ) * Math.tan( Math.toRadians( degrees ) ) );
        return new Point( xoffset + getWavefunction().getWidth() / 2, getWavefunction().getHeight() - inset );
    }
}
