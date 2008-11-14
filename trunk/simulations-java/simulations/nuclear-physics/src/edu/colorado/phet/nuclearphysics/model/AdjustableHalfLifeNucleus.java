/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * This class represents a non-composite nucleus that has an adjustable half
 * life.  There is obviously no such thing in nature, so the atomic weight of
 * the atom is chosen arbitrarily and other portions of the simulation must
 * "play along".
 *
 * @author John Blanco
 */
public class AdjustableHalfLifeNucleus extends AtomicNucleus implements AlphaDecayControl {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Bismuth 208.
    public static final int ORIGINAL_NUM_PROTONS = 83;
    public static final int ORIGINAL_NUM_NEUTRONS = 125;
    
    // Random number generator used for calculating decay time based on half life.
    private static final Random RAND = new Random();
    
    // Random number generator used for calculating decay time based on half life.
    private static final double DEFAULT_HALF_LIFE = 1.5;  // In seconds.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private double _decayTime = 0;       // Time at which fission will occur.
    private double _activatedLifetime = 0; // Amount of time that nucleus has been or was active prior to decay.
    private double _halfLife = 0;        // Half life, from which decay time is probabilistically calculated.
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public AdjustableHalfLifeNucleus(NuclearPhysicsClock clock, Point2D position){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        _halfLife = DEFAULT_HALF_LIFE;
    }
    
    public AdjustableHalfLifeNucleus(NuclearPhysicsClock clock){

        this(clock, new Point2D.Double(0, 0));
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public double getDecayTime(){
        return _decayTime;
    }
    
    public double getHalfLife(){
        return _halfLife;
    }
    
    public void setHalfLife(double halfLife){
        _halfLife = halfLife;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(){
        
        // Reset the decay time to 0, indicating that it shouldn't occur
        // until something changes.
        _decayTime = 0;
        _activatedLifetime = 0;

        if ((_numNeutrons != ORIGINAL_NUM_NEUTRONS) || (_numProtons != ORIGINAL_NUM_PROTONS)){
            // Decay has occurred.
            _numNeutrons = ORIGINAL_NUM_NEUTRONS;
            _numProtons = ORIGINAL_NUM_PROTONS;
            
            // Notify all listeners of the change to our atomic weight.
            notifyAtomicWeightChanged(null);
        }

        // Notify all listeners of the potential position change.
        notifyPositionChanged();
    }
    
    /**
     * Activate the nucleus, meaning that it will now decay after some amount
     * of time.
     */
    public void activateDecay(){
    	
    	// Only allow activation if the nucleus hasn't already decayed.
    	if (_numNeutrons == ORIGINAL_NUM_NEUTRONS){
    		_decayTime = _clock.getSimulationTime() + calcDecayTime();
    	}
    }
    
    /**
     * Return true if decay is currently active and false if not.
     */
    public boolean isDecayActive(){
    	if (_numNeutrons == ORIGINAL_NUM_NEUTRONS && _decayTime != 0){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    /**
     * Returns a value indicating how long the nucleus has been active without
     * having decayed.
     */
    public double getActivatedTime(){
    	return _activatedLifetime;
    }
    
    /**
     * Return a value indicating whether or not the nucleus has decayed.
     */
    public boolean hasDecayed(){
    	if (_numProtons < ORIGINAL_NUM_PROTONS){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response we check if it is time to decay.
     */
    protected void handleClockTicked(ClockEvent clockEvent)
    {
        super.handleClockTicked( clockEvent );
        
        // See if this nucleus is active, i.e. moving towards decay.
        if (_decayTime != 0){
         
        	// See if alpha decay should occur.
	        if (clockEvent.getSimulationTime() >= _decayTime ) {
	            // Cause alpha decay by generating an alpha particle and reducing our atomic weight.
	            ArrayList byProducts = new ArrayList();
	            byProducts.add( new AlphaParticle(_position.getX(), _position.getY()));
	            _numNeutrons -= 2;
	            _numProtons -= 2;
	
	            // Set the final value for the activation time.
	            _activatedLifetime += clockEvent.getSimulationTimeChange();
	            
	            // Send out the decay event to all listeners.
	            notifyAtomicWeightChanged(byProducts);
	            
	            // Set the decay time to 0 to indicate that decay has occurred and
	            // should not occur again.
	            _decayTime = 0;
	        }
	        else{
	        	// Not decaying yet, so updated the activated lifetime.
	        	_activatedLifetime += clockEvent.getSimulationTimeChange();
	        }
        }
    }
    
    /**
     * This method generates a value indicating the number of milliseconds for
     * a nucleus decay based on the half life.  This calculation is based on the 
     * exponential decay formula.
     * 
     * @return - a time value in milliseconds
     */
    private double calcDecayTime(){
    	
    	if (_halfLife == 0){
    		_decayTime = 0;
    	}
        double randomValue = RAND.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double decayMilliseconds = (-(Math.log( 1 - randomValue ) / 1.343)) * 1000;
        return decayMilliseconds;
    }
}
