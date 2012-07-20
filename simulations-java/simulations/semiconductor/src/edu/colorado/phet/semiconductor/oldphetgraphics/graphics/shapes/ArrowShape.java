// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics.shapes;

import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;


/**
 * User: Sam Reid
 * Date: Jan 3, 2004
 * Time: 4:49:08 PM
 */
public class ArrowShape {
    GeneralPath arrowPath;
    AbstractVector2D tipLocation;
    private Vector2D direction;
    private Vector2D norm;

    public ArrowShape( AbstractVector2D tailLocation, AbstractVector2D tipLocation, double headHeight, double headWidth, double tailWidth ) {
        direction = tipLocation.getSubtractedInstance( tailLocation ).getNormalizedInstance();
        double dist = tipLocation.getSubtractedInstance( tailLocation ).getMagnitude();
        if ( dist < headHeight ) {
            throw new RuntimeException( "Head too big." );
        }
        norm = direction.getNormalVector();
        this.tipLocation = tipLocation;
        Vector2D rightFlap = getPoint( -1 * headHeight, -headWidth / 2 );
        Vector2D leftFlap = getPoint( -1 * headHeight, headWidth / 2 );
        Vector2D rightPin = getPoint( -1 * headHeight, -tailWidth / 2 );
        Vector2D leftPin = getPoint( -1 * headHeight, tailWidth / 2 );
        Vector2D rightTail = getPoint( -1 * dist, -tailWidth / 2 );
        Vector2D leftTail = getPoint( -1 * dist, tailWidth / 2 );
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
    private Vector2D getPoint( double parallel, double normal ) {
        Vector2D dv = direction.getScaledInstance( parallel ).
                plus( norm.getScaledInstance( normal ) );
        Vector2D abs = tipLocation.plus( dv );
        return abs;
    }

    public GeneralPath getArrowShape() {
        return arrowPath;
    }

}
