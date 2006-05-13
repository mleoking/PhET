/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.Wavefunction;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 18, 2006
 * Time: 1:04:57 PM
 * Copyright (c) Feb 18, 2006 by Sam Reid
 */

public class RadialIntensityReader implements DGIntensityReader {
    private DGModel dgModel;

    public RadialIntensityReader( DGModel dgModel ) {
        this.dgModel = dgModel;
    }

    public double getIntensity( double angle ) {
        Point center = dgModel.getCenterAtomPoint();
        int radius = getRadius();
        double dy = radius * Math.cos( Math.toRadians( angle ) );
        double dx = radius * Math.sin( Math.toRadians( angle ) );
        Point pt = new Point( (int)( center.x + dx ), (int)( center.y + dy ) );
        if( getWavefunction().containsLocation( pt.x, pt.y ) ) {
            return getWavefunction().valueAt( pt.x, pt.y ).abs();
        }
        else {
            return 0.0;
        }
    }

    private int getRadius() {
        return getWavefunction().getWidth() / 2 - 5;
    }

    private Wavefunction getWavefunction() {
        return dgModel.getWavefunction();
    }
}
