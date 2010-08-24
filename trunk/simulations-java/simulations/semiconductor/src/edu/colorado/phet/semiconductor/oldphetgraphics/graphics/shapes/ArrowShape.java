/*, 2003.*/
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics.shapes;

import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;


/**
 * User: Sam Reid
 * Date: Jan 3, 2004
 * Time: 4:49:08 PM
 */
public class ArrowShape {
    GeneralPath arrowPath;
    AbstractVector2DInterface tipLocation;
    private AbstractVector2DInterface direction;
    private AbstractVector2DInterface norm;

    public ArrowShape( AbstractVector2DInterface tailLocation, AbstractVector2DInterface tipLocation, double headHeight, double headWidth, double tailWidth ) {
        direction = tipLocation.getSubtractedInstance( tailLocation ).getNormalizedInstance();
        double dist = tipLocation.getSubtractedInstance( tailLocation ).getMagnitude();
        if ( dist < headHeight ) {
            throw new RuntimeException( "Head too big." );
        }
        norm = direction.getNormalVector();
        this.tipLocation = tipLocation;
        AbstractVector2DInterface rightFlap = getPoint( -1 * headHeight, -headWidth / 2 );
        AbstractVector2DInterface leftFlap = getPoint( -1 * headHeight, headWidth / 2 );
        AbstractVector2DInterface rightPin = getPoint( -1 * headHeight, -tailWidth / 2 );
        AbstractVector2DInterface leftPin = getPoint( -1 * headHeight, tailWidth / 2 );
        AbstractVector2DInterface rightTail = getPoint( -1 * dist, -tailWidth / 2 );
        AbstractVector2DInterface leftTail = getPoint( -1 * dist, tailWidth / 2 );
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
    private AbstractVector2DInterface getPoint( double parallel, double normal ) {
        AbstractVector2DInterface dv = direction.getScaledInstance( parallel ).
                getAddedInstance( norm.getScaledInstance( normal ) );
        AbstractVector2DInterface abs = tipLocation.getAddedInstance( dv );
        return abs;
    }

    public GeneralPath getArrowShape() {
        return arrowPath;
    }

}
