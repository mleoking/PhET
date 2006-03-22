/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.Potential;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 13, 2006
 * Time: 11:21:47 PM
 * Copyright (c) Feb 13, 2006 by Sam Reid
 */

public abstract class AtomPotential implements Potential {
    private Point center;
    private int radius;
    private double potentialValue;

    public AtomPotential( Point center, int radius, double potentialValue ) {
        this.center = center;
        this.radius = radius;
        this.potentialValue = potentialValue;
    }

    public double getPotential( int x, int y, int timestep ) {
        Point testPoint = new Point( x, y );
        if( inRange( testPoint ) ) {
            return getPotentialValue();
        }
        else {
            return 0.0;
        }
    }

    protected abstract boolean inRange( Point testPoint );

    private double getPotentialValue() {
        return potentialValue;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}
