/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.DynamicBranch;
import edu.colorado.phet.common_cck.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 12:50:58 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class Inductor extends Resistor implements DynamicBranch {

    private double MIN_TIME = 0;
    private double time = 1;
//    double cap = 1;
    private double inductance;

    public Inductor( Point2D.Double aDouble, AbstractVector2D dir, double componentWidth, double initialHeight, CircuitChangeListener kirkhoffListener ) {
        super( aDouble, dir, componentWidth, initialHeight, kirkhoffListener );
    }

    public double getResistance() {
        double res = time * inductance;
//        System.out.println( "res = " + res );
        return res;
    }

    public void stepInTime( double dt ) {
        if( Math.abs( getCurrent() ) > 0.001 ) {
            time += dt;
        }
    }

    public void resetDynamics() {
        time = MIN_TIME;
    }

    public void setTime( double s ) {
        this.time = s + MIN_TIME;
    }

    public void setInductance( double inductance ) {
        this.inductance = inductance;
    }
}
