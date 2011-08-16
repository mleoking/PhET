// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;

/**
 * Base class for models in this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MPModel implements Resettable {

    public final EField eField = new EField();

    public void reset() {
        eField.reset();
    }

    // converts an angle to range [0,2*PI) radians
    protected static double normalizeAngle( double angle ) {
        double normalizedAngle = angle % ( 2 * Math.PI );
        if ( normalizedAngle < 0 ) {
            normalizedAngle = ( 2 * Math.PI ) + angle;
        }
        assert ( normalizedAngle >= 0 && normalizedAngle <= 2 * Math.PI );
        return normalizedAngle;
    }
}
