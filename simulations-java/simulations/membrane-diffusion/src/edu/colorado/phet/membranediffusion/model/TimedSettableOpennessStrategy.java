/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;


/**
 * A membrane channel for which the openness can be set.  There is a time
 * constant that makes the openness level move gradually from open to closed
 * and vice versa.
 * 
 * @author John Blanco
 */
public class TimedSettableOpennessStrategy extends MembraneChannelOpennessStrategy {
    
    private static final double CHANGE_RATE = 0.1;  // Per second of sim time.
    
    private double targetOpennessValue = 0;
    private double currentOpennessValue = 0;

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranediffusion.model.MembraneChannelOpennessStrategy#getOpenness()
     */
    @Override
    public double getOpenness() {
        return currentOpennessValue;
    }
    
    public void open(){
        targetOpennessValue = 1;
    }
    
    public void close(){
        targetOpennessValue = 0;
    }

    @Override
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        if (currentOpennessValue < targetOpennessValue){
            currentOpennessValue = Math.min( currentOpennessValue + CHANGE_RATE * dt, targetOpennessValue );
        }
        else if (currentOpennessValue > targetOpennessValue){
            currentOpennessValue = Math.max( currentOpennessValue - CHANGE_RATE * dt, targetOpennessValue );
        }
    }
}
