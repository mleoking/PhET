package edu.colorado.phet.bernoulli.zoom;

import edu.colorado.phet.coreadditions.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 3:48:00 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class MoveTo2D {
    PhetVector target;
    double speed;

    public MoveTo2D( PhetVector target, double speed ) {
        this.target = target;
        this.speed = speed;
    }

    public PhetVector moveCloser( PhetVector currentLocation ) {
        PhetVector diff = target.getSubtractedInstance( currentLocation );

        if( diff.getMagnitude() < speed || diff.getMagnitude() == 0 ) {
            return target;//you made it!
        }
        else {
            PhetVector speed = diff.getNormalizedInstance().getScaledInstance( this.speed );
            return currentLocation.getAddedInstance( speed );
        }
    }
}
