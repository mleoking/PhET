/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.DynamicBranch;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 15, 2006
 * Time: 11:01:58 PM
 * Copyright (c) Jun 15, 2006 by Sam Reid
 */

public class ACVoltageSource extends Battery implements DynamicBranch {
    private double time = 0;

    public ACVoltageSource( double voltage, double internalResistance ) {
        super( voltage, internalResistance );
    }

    public ACVoltageSource( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl, boolean internalResistanceOn ) {
        super( start, dir, length, height, kl, internalResistanceOn );
    }

    public ACVoltageSource( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl, double internalResistance, boolean internalResistanceOn ) {
        super( start, dir, length, height, kl, internalResistance, internalResistanceOn );
    }

    public ACVoltageSource( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height, double internalResistance, boolean internalResistanceOn ) {
        super( kl, startJunction, endjJunction, length, height, internalResistance, internalResistanceOn );
    }

    double maxvolts = 10;
    double frequency = 1.0 / 100.0;//hz

    public double getVoltageDrop() {
        double scale = Math.sin( time * frequency * Math.PI * 2 );
        double v = maxvolts * scale;
        return v;
    }

    public void stepInTime( double dt ) {
        this.time += dt;
    }
}
