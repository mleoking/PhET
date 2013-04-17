// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.circuitconstructionkit.model.components;

import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.DynamicBranch;
import edu.colorado.phet.circuitconstructionkit.model.Junction;

/**
 * User: Sam Reid
 * Date: Jun 15, 2006
 * Time: 11:01:58 PM
 */

public class ACVoltageSource extends Battery implements DynamicBranch {
    private double time = 0;
    private double amplitude = 10;
    private double frequency = 0.5;//Hz

    public ACVoltageSource( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height, double internalResistance, boolean internalResistanceOn ) {
        super( kl, startJunction, endjJunction, length, height, internalResistance, internalResistanceOn );
        setUserComponentID( CCKSimSharing.UserComponents.acVoltageSource );
    }

    public double getVoltageDrop() {
        double scale = Math.sin( time * frequency * Math.PI * 2 );
        return amplitude * scale;
    }

    public void stepInTime( double dt ) {
        this.time += dt;
    }

    public void resetDynamics() {
        time = 0;
    }

    public void setTime( double time ) {
        this.time = time;
    }

    public void setAmplitude( double value ) {
        this.amplitude = value;
    }

    public void setFrequency( double frequency ) {
        this.frequency = frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getFrequency() {
        return frequency;
    }

}
