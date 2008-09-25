package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.semiconductor.util.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 1:01:18 PM
 */
public class LinearBranch {
    PhetVector start;
    PhetVector end;
    private PhetVector dv;

    public LinearBranch( PhetVector start, PhetVector end ) {
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
    public PhetVector getLocation( double dist ) {
        return start.getAddedInstance( dv.getInstanceForMagnitude( dist ) );
    }

    public PhetVector getStartPosition() {
        return start;
    }

    public PhetVector getEndPosition() {
        return end;
    }
}
