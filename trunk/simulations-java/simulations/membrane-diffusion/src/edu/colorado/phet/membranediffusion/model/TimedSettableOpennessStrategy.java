/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;


/**
 * A membrane channel openness strategy for which the openness can be set
 * through its API.  There is a time constant that makes the openness level
 * move gradually from open to closed and vice versa.
 * 
 * @author John Blanco
 */
public class TimedSettableOpennessStrategy extends MembraneChannelOpennessStrategy {
    
    private static final double CHANGE_RATE = 4;  // Per second of sim time.
    
    private double targetOpennessValue = 0;

    public void open(){
        targetOpennessValue = 1;
    }
    
    public void close(){
        targetOpennessValue = 0;
    }

    @Override
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if (getOpenness() < targetOpennessValue){
            setOpenness( Math.min( getOpenness() + CHANGE_RATE * dt, targetOpennessValue ) );
        }
        else if (getOpenness() > targetOpennessValue){
            setOpenness( Math.max( getOpenness() - CHANGE_RATE * dt, targetOpennessValue ) );
        }
    }
}
