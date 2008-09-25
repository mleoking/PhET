package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;


/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:01:18 PM
 */
public class LinearBranch {
    Vector2D.Double start;
    Vector2D.Double end;
    private AbstractVector2D dv;

    public LinearBranch( Vector2D.Double start, Vector2D.Double end ) {
        this.start = start;
        this.end = end;
        this.dv = end.getSubtractedInstance( start );
    }

    public double getLength() {
        return dv.getMagnitude();
    }

    /**
     * Get a location a scalar distance along the branch.
     */
    public AbstractVector2D getLocation( double dist ) {
        return start.getAddedInstance( dv.getInstanceOfMagnitude( dist ));
    }

    public Vector2D.Double getStartPosition() {
        return start;
    }

    public Vector2D.Double getEndPosition() {
        return end;
    }
}
