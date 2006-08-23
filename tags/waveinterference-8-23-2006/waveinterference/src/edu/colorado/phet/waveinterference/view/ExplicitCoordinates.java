/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 3:16:40 AM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class ExplicitCoordinates extends LatticeScreenCoordinates {
    private int width;
    private int height;

    public ExplicitCoordinates() {
        this( 50, 50 );//todo remove this dummy default
    }

    public ExplicitCoordinates( int width, int height ) {
        this.width = width;
        this.height = height;
    }

    public Point2D toScreenCoordinates( int i, int j ) {
        Point screenPt1 = new Point();
        Point screenPt2 = new Point( 100, 100 );
        //maps lattice to screen
        ModelViewTransform2D modelViewTransform2D =
                new ModelViewTransform2D( toLatticeCoordinatesFP( screenPt1 ), toLatticeCoordinatesFP( screenPt2 ), screenPt1, screenPt2 );
        return modelViewTransform2D.modelToViewDouble( i, j );
    }

    public Point2D toLatticeCoordinatesFP( Point2D pt ) {
        return toLatticeCoordinatesFP( pt.getX(), pt.getY() );
    }

    private Point2D toLatticeCoordinatesFP( double x, double y ) {
        return new Point2D.Double( x / 5, y / 5 + 10 );
    }

    public Point toLatticeCoordinates( double x, double y ) {
        Point2D fp = toLatticeCoordinatesFP( x, y );
        return new Point( (int)fp.getX(), (int)fp.getY() );
    }

    protected Dimension getGridSize() {
        return new Dimension( width, height );
    }
}
