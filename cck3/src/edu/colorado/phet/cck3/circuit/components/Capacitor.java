/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.CompositeCircuitChangeListener;
import edu.colorado.phet.cck3.circuit.DynamicBranch;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 12:50:58 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class Capacitor extends Resistor implements DynamicBranch {

    private double MIN_TIME = 0;
    private double time = 0.001;
    double cap = 10;

    public Capacitor( Point2D.Double aDouble, AbstractVector2D dir, double componentWidth, double initialHeight, CircuitChangeListener kirkhoffListener ) {
        super( aDouble, dir, componentWidth, initialHeight, kirkhoffListener );
    }

    public Capacitor( double capacitance ) {
        this( new Point2D.Double( 0, 0 ), new Vector2D.Double( 1, 0 ), 5, 5, new CompositeCircuitChangeListener() );
        setCapacitance( capacitance );
    }

    public double getResistance() {
//        double res = 1.0 / ( time * cap );
        double res = ( time * cap );//inverted since there are 2 reciprocals in the MNA
//        System.out.println( "res = " + res );
        return res;
    }

    public void stepInTime( double dt ) {
//        System.out.println( "Math.abs( getCurrent( )) = " + Math.abs( getCurrent() ) );
//        System.out.println( "getVoltageDrop() = " + getVoltageDrop() );
//        System.out.println( getVoltageDrop() );
        if( Math.abs( getCurrent() ) > 0.001 ) {
            time += dt;
        }
    }

    public void resetDynamics() {
        time = MIN_TIME;
    }

    public void setCapacitance( double cap ) {
        this.cap = cap;
    }

    public void setTime( double s ) {
        this.time = s + MIN_TIME;
    }

    public double getCapacitance() {
        return cap;
    }
}
