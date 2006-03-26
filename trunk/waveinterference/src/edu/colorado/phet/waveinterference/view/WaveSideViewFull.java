/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Lattice2D;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 10:05:29 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class WaveSideViewFull extends WaveSideView {
    public WaveSideViewFull( Lattice2D lattice2D, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( lattice2D, latticeScreenCoordinates );
        setStroke( new BasicStroke( 3 ) );
    }

    public void update() {
        GeneralPath generalpath = getWavePath();
        double x0 = getX( 0 );
        double y0 = getY( 0, getYValue() );
        double xF = getX( getLattice2D().getWidth() - 1 );
        double yF = getY( getLattice2D().getWidth() - 1, getYValue() );
        generalpath.lineTo( (float)xF, (float)( 200 ) );
        generalpath.lineTo( (float)x0, (float)( 200 ) );
        generalpath.lineTo( (float)x0, (float)y0 );
        getPath().setPaint( Color.blue );
        getPath().setPathTo( generalpath );
    }
}
