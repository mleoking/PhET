// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;


/**
 * A membrane channel openness strategy for which the openness can be set
 * through its API.  There is a time constant that makes the openness level
 * move gradually from open to closed and vice versa.
 * 
 * @author John Blanco
 */
public class TimedSettableOpennessStrategy extends MembraneChannelOpennessStrategy {
    
    private static final double CHANGE_RATE = 4;  // Per second of sim time.
    
    private double targetOpennessValue = 0; // Closed by default.
    
    /**
     * Constructor.
     */
    public TimedSettableOpennessStrategy( ConstantDtClock clock ) {
        super( clock );
    }
    
    /**
     * Constructor.  No clock is supplied, so instances constructed with this
     * constructor will need to be explicitly stepped in order to get them to
     * do anything.
     */
    public TimedSettableOpennessStrategy(double initialOpenness){
        super();
        assert initialOpenness >= 0 && initialOpenness <= 1; 
        setOpenness( initialOpenness );
        targetOpennessValue = initialOpenness;
    }

    public void open(){
        targetOpennessValue = 1;
    }
    
    public void close(){
        targetOpennessValue = 0;
    }

    @Override
    public void stepInTime( double dt ) {
        if (getOpenness() < targetOpennessValue){
            setOpenness( Math.min( getOpenness() + CHANGE_RATE * dt, targetOpennessValue ) );
        }
        else if (getOpenness() > targetOpennessValue){
            setOpenness( Math.max( getOpenness() - CHANGE_RATE * dt, targetOpennessValue ) );
        }
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.membranechannels.model.MembraneChannelOpennessStrategy#isDynamic()
     */
    @Override
    protected boolean isDynamic() {
        return true;
    }
}
