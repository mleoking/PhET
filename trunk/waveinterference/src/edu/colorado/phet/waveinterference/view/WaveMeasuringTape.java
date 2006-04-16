/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.piccolo.nodes.MeasuringTape;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Apr 15, 2006
 * Time: 10:50:37 PM
 * Copyright (c) Apr 15, 2006 by Sam Reid
 */

public class WaveMeasuringTape extends MeasuringTape {
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private Rectangle2D modelBounds = new Rectangle2D.Double( 0, 0, 30, 30 );

    public WaveMeasuringTape( LatticeScreenCoordinates latticeScreenCoordinates ) {
        super( new ModelViewTransform2D( new Rectangle( 50, 50 ), new Rectangle( 50, 50 ) ), new Point2D.Double() );
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateMapping();
            }
        } );
        updateMapping();
        setUnits( "cm" );
        setModelSrc( new Point( 15, 15 ) );
        setModelDst( new Point( 25, 15 ) );
    }

    public void setWaveAreaSize( double width, double height ) {
        this.modelBounds = new Rectangle2D.Double( 0, 0, width, height );
    }

    private void updateMapping() {
        ModelViewTransform2D tx = new ModelViewTransform2D( modelBounds, latticeScreenCoordinates.getScreenRect() );
        super.setModelViewTransform2D( tx );
    }

    public void setLocation( Point2D src, Point2D dst ) {
        setModelSrc( src );
        setModelDst( dst );
    }
}
