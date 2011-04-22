// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.MeasuringTape;

/**
 * User: Sam Reid
 * Date: Apr 15, 2006
 * Time: 10:50:37 PM
 */

public class WaveMeasuringTape extends MeasuringTape {
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private Rectangle2D modelBounds = new Rectangle2D.Double( 0, 0, 30, 30 );

    public WaveMeasuringTape( LatticeScreenCoordinates latticeScreenCoordinates, double physicalWidth, double physicalHeight ) {
        super( new ModelViewTransform2D( new Rectangle( 50, 50 ), new Rectangle( 50, 50 ) ), new Point2D.Double(), "cm" );
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateMapping();
            }
        } );
        updateMapping();
        setWaveAreaSize( physicalWidth, physicalHeight );
        Point2D.Double src = new Point2D.Double( physicalWidth / 2, physicalHeight / 2 );
        Point2D.Double dst = new Point2D.Double( physicalWidth * 0.75, physicalHeight / 2 );
        setLocation( src, dst );

        this.initialSrc = new Point2D.Double( src.x, src.y );
        this.initialDst = new Point2D.Double( dst.x, dst.y );
    }

    public void setWaveAreaSize( double width, double height ) {
        this.modelBounds = new Rectangle2D.Double( 0, 0, width, height );
    }

    private void updateMapping() {
        ModelViewTransform2D tx = new ModelViewTransform2D( modelBounds, latticeScreenCoordinates.getScreenRect() );
        super.setModelViewTransform2D( tx );
    }
}