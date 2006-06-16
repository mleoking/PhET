/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.DynamicBranch;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 12:50:58 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class Capacitor extends Resistor implements DynamicBranch {
    public Capacitor( Point2D.Double aDouble, AbstractVector2D dir, double componentWidth, double initialHeight, CircuitChangeListener kirkhoffListener ) {
        super( aDouble, dir, componentWidth, initialHeight, kirkhoffListener );
    }

    private double time = 0.001;


    double cap = 1;

    public double getResistance() {
//        double r = 1.0 / ( time * cap );
        double r = ( time * cap );
//        System.out.println( "r = " + r );
        return r;
    }

    public void stepInTime( double dt ) {
        if( Math.abs( getCurrent() ) > 0.001 ) {
            time += dt;
        }

//        System.out.println( getCurrent() );
    }

    public void resetDynamics() {
        time = 0.001;
    }
}
