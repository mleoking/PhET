/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.view.graphics.shapes;

import java.awt.geom.GeneralPath;

import edu.colorado.phet.semiconductor.util.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.util.DoubleGeneralPath;

/**
 * User: Sam Reid
 * Date: Jan 3, 2004
 * Time: 4:49:08 PM
 */
public class ArrowShape {
    GeneralPath arrowPath;
    PhetVector tipLocation;
    private PhetVector direction;
    private PhetVector norm;

    public ArrowShape( PhetVector tailLocation, PhetVector tipLocation, double headHeight, double headWidth, double tailWidth ) {
        direction = tipLocation.getSubtractedInstance( tailLocation ).getNormalizedInstance();
        double dist = tipLocation.getSubtractedInstance( tailLocation ).getMagnitude();
        if ( dist < headHeight ) {
            throw new RuntimeException( "Head too big." );
        }
        norm = direction.getNormalVector();
        this.tipLocation = tipLocation;
        PhetVector rightFlap = getPoint( -1 * headHeight, -headWidth / 2 );
        PhetVector leftFlap = getPoint( -1 * headHeight, headWidth / 2 );
        PhetVector rightPin = getPoint( -1 * headHeight, -tailWidth / 2 );
        PhetVector leftPin = getPoint( -1 * headHeight, tailWidth / 2 );
        PhetVector rightTail = getPoint( -1 * dist, -tailWidth / 2 );
        PhetVector leftTail = getPoint( -1 * dist, tailWidth / 2 );
        DoubleGeneralPath path = new DoubleGeneralPath( tipLocation );
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
    private PhetVector getPoint( double parallel, double normal ) {
        PhetVector dv = direction.getScaledInstance( parallel ).
                getAddedInstance( norm.getScaledInstance( normal ) );
        PhetVector abs = tipLocation.getAddedInstance( dv );
        return abs;
    }

    public GeneralPath getArrowShape() {
        return arrowPath;
    }

}
