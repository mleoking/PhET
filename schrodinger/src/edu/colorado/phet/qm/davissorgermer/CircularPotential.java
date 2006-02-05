/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissorgermer;

import edu.colorado.phet.qm.model.Potential;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:30:26 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class CircularPotential implements Potential {
    private Point center;
    private int radius;
    private double potentialValue;

    public CircularPotential( Point center, int radius, double potentialValue ) {
        this.center = center;
        this.radius = radius;
        this.potentialValue = potentialValue;
    }

    public double getPotential( int x, int y, int timestep ) {
        Point testPoint = new Point( x, y );
        double dist = testPoint.distance( center );
        if( dist <= radius ) {
            return getPotentialValue();
        }
        else {
            return 0.0;
        }
    }

    private double getPotentialValue() {
        return potentialValue;
    }

}
