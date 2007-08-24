/*, 2003.*/
package edu.colorado.phet.distanceladder.common.view.graphics.shapes;

import edu.colorado.phet.distanceladder.common.math.PhetVector;
import edu.colorado.phet.distanceladder.common.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * User: Sam Reid
 * Date: Jan 3, 2004
 * Time: 4:49:08 PM
 */
public class ArrowShape {
    GeneralPath headPath;
    GeneralPath tailPath;
    GeneralPath arrowPath;
    PhetVector tailLocation;
    PhetVector tipLocation;
    double headHeight;
    double headWidth;
    double tailWidth;
    private PhetVector direction;
    private PhetVector norm;

    public ArrowShape( Point tailLocation, Point tipLocation, double headHeight, double headWidth, double tailWidth ) {
        this( new PhetVector( tailLocation ), new PhetVector( tipLocation ), headHeight, headWidth, tailWidth );
    }

    public ArrowShape( PhetVector tailLocation, PhetVector tipLocation, double headHeight, double headWidth, double tailWidth ) {
        direction = tipLocation.getSubtractedInstance( tailLocation ).getNormalizedInstance();
        double dist = tipLocation.getSubtractedInstance( tailLocation ).getMagnitude();
        if( dist < headHeight ) {
            throw new RuntimeException( "Head too big." );
        }
        norm = direction.getNormalVector();
        this.tailLocation = tailLocation;
        this.tipLocation = tipLocation;
        this.headHeight = headHeight;
        this.headWidth = headWidth;
        this.tailWidth = tailWidth;
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

    public GeneralPath getTailShape() {
        return tailPath;
    }

    public GeneralPath getArrowShape() {
        return arrowPath;
    }

    public Shape getHeadShape() {
        return arrowPath;
    }
}
