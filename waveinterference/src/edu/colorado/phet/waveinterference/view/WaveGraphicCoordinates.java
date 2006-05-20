/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:48:21 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class WaveGraphicCoordinates extends LatticeScreenCoordinates {
    private PNode waveModelGraphic;
    private WaveModel waveModel;

    public WaveGraphicCoordinates( WaveModel waveModel, PNode waveModelGraphic ) {
        this.waveModel = waveModel;
        this.waveModelGraphic = waveModelGraphic;
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
        Point2D location = new Point2D.Double( x, y );
        waveModelGraphic.globalToLocal( location );
        double fracDistX = location.getX() / waveModelGraphic.getFullBounds().getWidth();
        double fracDistY = location.getY() / waveModelGraphic.getFullBounds().getHeight();

        Point2D fracLoc = new Point2D.Double( fracDistX, fracDistY );
        return new Point2D.Double( ( fracLoc.getX() * waveModel.getWidth() ),
                                   ( fracLoc.getY() * waveModel.getHeight() ) );
    }

    public Point toLatticeCoordinates( double x, double y ) {
        Point2D fp = toLatticeCoordinatesFP( x, y );
        return new Point( (int)fp.getX(), (int)fp.getY() );
    }

    protected Dimension getGridSize() {
        return new Dimension( waveModel.getWidth(), waveModel.getHeight() );
    }
}
