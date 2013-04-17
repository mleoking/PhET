// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:30:26 PM
 */

public class CircularPotential extends AtomPotential {

    public CircularPotential( Point center, int radius, double potentialValue ) {
        super( center, radius, potentialValue );
    }

    protected boolean inRange( Point testPoint ) {
        double dist = testPoint.distance( super.getCenter() );
        return dist <= ( getDiameter() ) / 2.0;
    }

}
