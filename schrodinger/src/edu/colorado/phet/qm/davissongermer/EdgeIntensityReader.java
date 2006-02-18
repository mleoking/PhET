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
    private int inset = 3;
    private DGModel dgModel;

    public EdgeIntensityReader( DGModel dgModel ) {
        this.dgModel = dgModel;
    }

    public double getIntensity( double angle ) {
        Point gridLocation = toGridLocation( angle );
        Complex value = getWavefunction().valueAt( gridLocation.x, gridLocation.y );
        return value.abs();
    }

    private Wavefunction getWavefunction() {
        return dgModel.getWavefunction();
    }

    private Point toGridLocation( double degrees ) {
        if( degrees < 45 ) {
            return toGridLocationBottom( degrees );
        }
        else {
            return toGridLocationSide( degrees );
        }
    }

    private Point toGridLocationSide( double degrees ) {
        int yoffset = (int)( ( getWavefunction().getWidth() / 2 - inset ) * Math.tan( Math.toRadians( 90 - degrees ) ) );
        return new Point( getWavefunction().getWidth() - inset, getWavefunction().getHeight() / 2 + yoffset );
    }

    private Point toGridLocationBottom( double degrees ) {
        int xoffset = (int)( ( getWavefunction().getHeight() / 2 - inset ) * Math.tan( Math.toRadians( degrees ) ) );
        return new Point( xoffset + getWavefunction().getWidth() / 2, getWavefunction().getHeight() - inset );
    }
}
