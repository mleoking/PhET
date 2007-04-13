/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.enums.PotentialType;


/**
 * PotentialFactory is a factory for creating potential energy objects.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialFactory {

    /* Not intended for instantiation */
    private PotentialFactory() {}
    
    /**
     * Creates a potential energy that corresponds to a specified enum value.
     * 
     * @param potentialType
     * @return
     */
    public static AbstractPotential createPotentialEnergy( PotentialType potentialType ) {
        AbstractPotential potentialEnergy;
        if ( potentialType == PotentialType.CONSTANT ) {
            potentialEnergy = new ConstantPotential();
        }
        else if ( potentialType == PotentialType.STEP ) {
            potentialEnergy = new StepPotential();
        }
        else if ( potentialType == PotentialType.SINGLE_BARRIER ) {
            potentialEnergy = new SingleBarrierPotential();
        }
        else if ( potentialType == PotentialType.DOUBLE_BARRIER ) {
            potentialEnergy = new DoubleBarrierPotential();
        }
        else {
            throw new IllegalStateException( "unsupported potentialType: " + potentialType );
        }
        return potentialEnergy;
    }
    
    /**
     * Determines the enum values that corresponds to a specified potential energy.
     * 
     * @param potentialEnergy
     * @return
     */
    public static PotentialType getPotentialType( AbstractPotential potentialEnergy ) {
        PotentialType potentialType = null;
        if ( potentialEnergy instanceof ConstantPotential ) {
            potentialType = PotentialType.CONSTANT;
        }
        else if ( potentialEnergy instanceof StepPotential ) {
            potentialType = PotentialType.STEP;
        }
        else if ( potentialEnergy instanceof SingleBarrierPotential ) {
            potentialType = PotentialType.SINGLE_BARRIER;
        }
        else if ( potentialEnergy instanceof DoubleBarrierPotential ) {
            potentialType = PotentialType.DOUBLE_BARRIER;
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + potentialEnergy.getClass().getName() );
        }
        return potentialType;
    }
    
    /**
     * Clones a potential energy object.
     * 
     * @param pe
     * @return
     */
    public static AbstractPotential clonePotentialEnergy( AbstractPotential pe ) {
        AbstractPotential peNew = null;
        if ( pe instanceof ConstantPotential ) {
            peNew = new ConstantPotential( (ConstantPotential) pe );
        }
        else if ( pe instanceof StepPotential ) {
            peNew = new StepPotential( (StepPotential) pe );
        }
        else if ( pe instanceof SingleBarrierPotential ) {
            peNew = new SingleBarrierPotential( (SingleBarrierPotential) pe );
        }
        else if ( pe instanceof BarrierPotential ) {
            peNew = new DoubleBarrierPotential( (DoubleBarrierPotential) pe );
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + pe.getClass().getName() );
        }
        return peNew;
    }
}
