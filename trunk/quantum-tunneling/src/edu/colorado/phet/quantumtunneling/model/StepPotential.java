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


/**
 * StepPotential
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class StepPotential extends AbstractPotential {

    private static final double DEFAULT_STEP_POSITION = 5;
    private static final double DEFAULT_STEP_ENERGY = 5;
    
    public StepPotential() {
        super( 2 /* numberOfRegions */ );
        setRegion( 0, MIN_POSITION, DEFAULT_STEP_POSITION, 0 );
        setRegion( 1, getRegion( 0 ).getEnd(), MAX_POSITION, DEFAULT_STEP_ENERGY );
    }
    
    public StepPotential( StepPotential step ) {
        super( step );
    }
    
    public void setPosition( double position ) {
        if ( position == MIN_POSITION || position == MAX_POSITION ) {
            throw new IllegalArgumentException( "position cannot be at min or max range" );
        }
        double start = position;
        double end = getRegion( 1 ).getEnd();
        double energy = getRegion( 1 ).getEnergy();
        setRegion( 1, start, end, energy );
    }
    
    public double getPosition() {
        return getRegion( 1 ).getStart();
    }
    
    public void setEnergy( double energy ) {
        setEnergy( 1, energy );
    }
    
    public double getEnergy() {
        return getRegion( 1  ).getEnergy();
    }
}
