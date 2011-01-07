// Copyright 2002-2011, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics.shapes;

import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;


/**
 * User: Sam Reid
 * Date: Jan 3, 2004
 * Time: 4:49:08 PM
 */
public class ArrowShape {
    GeneralPath arrowPath;
    ImmutableVector2D tipLocation;
    private ImmutableVector2D direction;
    private ImmutableVector2D norm;

    public ArrowShape( ImmutableVector2D tailLocation, ImmutableVector2D tipLocation, double headHeight, double headWidth, double tailWidth ) {
        direction = tipLocation.getSubtractedInstance( tailLocation ).getNormalizedInstance();
        double dist = tipLocation.getSubtractedInstance( tailLocation ).getMagnitude();
        if ( dist < headHeight ) {
            throw new RuntimeException( "Head too big." );
        }
        norm = direction.getNormalVector();
        this.tipLocation = tipLocation;
        ImmutableVector2D rightFlap = getPoint( -1 * headHeight, -headWidth / 2 );
        ImmutableVector2D leftFlap = getPoint( -1 * headHeight, headWidth / 2 );
        ImmutableVector2D rightPin = getPoint( -1 * headHeight, -tailWidth / 2 );
        ImmutableVector2D leftPin = getPoint( -1 * headHeight, tailWidth / 2 );
        ImmutableVector2D rightTail = getPoint( -1 * dist, -tailWidth / 2 );
        ImmutableVector2D leftTail = getPoint( -1 * dist, tailWidth / 2 );
        DoubleGeneralPath path = new DoubleGeneralPath( tipLocation.getX(), tipLocation.getY() );
        path.lineTo( rightFlap );
        path.lineTo( rightPin );
        path.lineTo( rightTail );
        path.lineTo( leftTail );
        path.lineTo( leftPin );
        path.lineTo( leftFlap );
        path.lineTo( tipLocation );
        this.arrowPath = path.getGeneralPath();
    }

    //parallel and normal are from the tip
    private ImmutableVector2D getPoint( double parallel, double normal ) {
        ImmutableVector2D dv = direction.getScaledInstance( parallel ).
                getAddedInstance( norm.getScaledInstance( normal ) );
        ImmutableVector2D abs = tipLocation.getAddedInstance( dv );
        return abs;
    }

    public GeneralPath getArrowShape() {
        return arrowPath;
    }

}
