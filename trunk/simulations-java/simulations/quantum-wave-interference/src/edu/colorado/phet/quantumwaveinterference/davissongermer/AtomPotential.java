// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.model.Potential;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 13, 2006
 * Time: 11:21:47 PM
 */

public abstract class AtomPotential implements Potential {
    private Point center;
    private double potentialValue;
    private int diameter;

    public AtomPotential( Point center, int diameter, double potentialValue ) {
        this.center = center;
        this.diameter = diameter;
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

    public int getDiameter() {
        return diameter;
    }

    public String toString() {
        return getClass().getName() + ", center=" + center + ", diameter=" + diameter;
    }
}
