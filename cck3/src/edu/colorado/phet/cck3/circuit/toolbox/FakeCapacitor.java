/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.toolbox;

import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.DynamicBranch;
import edu.colorado.phet.cck3.circuit.components.Bulb;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 15, 2006
 * Time: 11:39:21 PM
 * Copyright (c) Jun 15, 2006 by Sam Reid
 */

public class FakeCapacitor extends Bulb implements DynamicBranch {
    private double time = 0.001;

    public FakeCapacitor( Point2D start, AbstractVector2D dir, double distBetweenJunctions, double magnitude,
                          double height, CircuitChangeListener circuitChangeListener ) {
        super( start, dir, distBetweenJunctions, magnitude, height, circuitChangeListener );
    }

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
