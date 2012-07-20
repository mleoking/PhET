// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Mar 31, 2006
 * Time: 7:47:46 PM
 */

public class BFieldGraphic extends AbstractVectorViewGraphic {

    public BFieldGraphic( WaveModel waveModel, LatticeScreenCoordinates latticeScreenCoordinates, int distBetweenSamples ) {
        super( waveModel, latticeScreenCoordinates, distBetweenSamples );
    }

    protected void addArrow( float x, float y ) {
        MutableVector2D vector = new MutableVector2D( new Point2D.Double( x, 0 ), new Point2D.Double( x, y ) );
        vector.rotate( Math.PI / 4 );
        Point2D dst = vector.getDestination( new Point2D.Double( x, 0 ) );
        Arrow arrow = new Arrow( new Point2D.Double( x, 0 ), new Point2D.Double( dst.getX(), dst.getY() ), 8, 8, 4, 0.5, true );
        PPath arrowPath = new PPath( arrow.getShape() );
        addChild( arrowPath );
        if ( y > 0 ) {
            arrowPath.setPaint( Color.white );
        }
    }
}
