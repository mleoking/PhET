// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * DrainData helps to maintain a constant concentration as particles flow out the drain by tracking flow rate and timing
 *
 * @author Sam Reid
 */
public class DrainData {

    //initial number of solutes at time user started manipulating drain faucet, used to choose a target concentration in MicroModel
    int initialNumberSolutes;

    //initial volume at time user started manipulating drain faucet, in m^3, used to choose a target concentration in MicroModel
    double initialVolume;

    //the previous flow rate of the drain faucet, for purposes of recording the target concentration when user starts draining fluid.
    double previousDrainFlowRate;

    //The type of the particle to match, necessary since concentration is different for each solute
    public final Class<? extends Particle> type;

    public DrainData( Class<? extends Particle> type ) {
        this.type = type;
    }
}