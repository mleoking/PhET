/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:30:26 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
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
