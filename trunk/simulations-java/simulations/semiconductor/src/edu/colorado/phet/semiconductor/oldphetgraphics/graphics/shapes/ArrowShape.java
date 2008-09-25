/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.view.graphics.shapes;

import java.awt.geom.GeneralPath;


import edu.colorado.phet.semiconductor.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Jan 3, 2004
 * Time: 4:49:08 PM
 */
public class ArrowShape {
    GeneralPath arrowPath;
    AbstractVector2D tipLocation;
    private AbstractVector2D direction;
    private AbstractVector2D norm;

    public ArrowShape( AbstractVector2D tailLocation, AbstractVector2D tipLocation, double headHeight, double headWidth, double tailWidth ) {
        direction = tipLocation.getSubtractedInstance( tailLocation ).getNormalizedInstance();
        double dist = tipLocation.getSubtractedInstance( tailLocation ).getMagnitude();
        if ( dist < headHeight ) {
            throw new RuntimeException( "Head too big." );
        }
        norm = direction.getNormalVector();
        this.tipLocation = tipLocation;
        AbstractVector2D rightFlap = getPoint( -1 * headHeight, -headWidth / 2 );
        AbstractVector2D leftFlap = getPoint( -1 * headHeight, headWidth / 2 );
        AbstractVector2D rightPin = getPoint( -1 * headHeight, -tailWidth / 2 );
        AbstractVector2D leftPin = getPoint( -1 * headHeight, tailWidth / 2 );
        AbstractVector2D rightTail = getPoint( -1 * dist, -tailWidth / 2 );
        AbstractVector2D leftTail = getPoint( -1 * dist, tailWidth / 2 );
        DoubleGeneralPath path = new DoubleGeneralPath( tipLocation.getX(), tipLocation.getY());
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
    private AbstractVector2D getPoint( double parallel, double normal ) {
        AbstractVector2D dv = direction.getScaledInstance( parallel ).
                getAddedInstance( norm.getScaledInstance( normal ) );
        AbstractVector2D abs = tipLocation.getAddedInstance( dv );
        return abs;
    }

    public GeneralPath getArrowShape() {
        return arrowPath;
    }

}
