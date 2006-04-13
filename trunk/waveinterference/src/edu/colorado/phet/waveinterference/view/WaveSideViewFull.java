/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 10:05:29 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */

public class WaveSideViewFull extends WaveSideView {
    public WaveSideViewFull( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( waveModel, latticeScreenCoordinates );
        setStroke( new BasicStroke( 3 ) );
    }

    public void update() {
        super.update();
        GeneralPath generalpath = getWavePath();
        Point2D[] pt = readValues();
        double x0 = pt[0].getX();
        double y0 = pt[0].getY();
        double xF = pt[pt.length - 1].getX();
        generalpath.lineTo( (float)xF, (float)( 200 ) );
        generalpath.lineTo( (float)x0, (float)( 200 ) );
        generalpath.lineTo( (float)x0, (float)y0 );
        getPath().setPathTo( generalpath );
    }

    public void setBodyColor( Color color ) {
        getPath().setPaint( color );
    }
}
