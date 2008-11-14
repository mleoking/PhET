/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;
import edu.colorado.phet.nuclearphysics.view.LabeledNucleusNode;

/**
 * This class represents a non-composite Polonium 211 nucleus.  In other words,
 * this nucleus does not create or keep track of any constituent nucleons.
 *
 * @author John Blanco
 */
public class Polonium211Nucleus extends AtomicNucleus implements AlphaDecayControl {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.
    public static final int ORIGINAL_NUM_PROTONS = 84;
    public static final int ORIGINAL_NUM_NEUTRONS = 127;
    
    // Random number generator used for calculating decay time based on half life.
    private static final Random RAND = new Random();
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private double _decayTime = 0;         // Time at which fission will occur.
    private double _activatedLifetime = 0; // Amount of time that nucleus has been or was active prior to decay.

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public Polonium211Nucleus(NuclearPhysicsClock clock, Point2D position){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
    }
    
    public Polonium211Nucleus(NuclearPhysicsClock clock){

        this(clock, new Point2D.Double(0, 0));
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public double getDecayTime(){
        return _decayTime;
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
    }
    
    /**
     * Activate the nucleus, meaning that it will now decay after some amount
     * of time.
     */
    public void activateDecay(){
    	
    	// Only allow activation if the nucleus hasn't already decayed.
    	if (_numNeutrons == ORIGINAL_NUM_NEUTRONS){
    		_decayTime = _clock.getSimulationTime() + calcPolonium211DecayTime();
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
    public double getActivatedLifetime(){
    	return _activatedLifetime;
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
	
	            // Send out the decay event to all listeners.
	            notifyAtomicWeightChanged(byProducts);
	            
	            // Set the decay time to 0 to indicate that decay has occurred and
	            // should not occur again.
	            _decayTime = 0;
	            
	            // Set the final value for the activation time.
	            _activatedLifetime = _decayTime;
	        }
	        else{
	        	// Not decaying yet, so updated the activated lifetime.
	        	_activatedLifetime += clockEvent.getSimulationTimeChange();
	        }
        }
    }
    
    /**
     * This method generates a value indicating the number of milliseconds for
     * a Polonium 211 nucleus to decay.  This calculation is based on the 
     * exponential decay formula and uses the decay constant for Polonium 211.
     * 
     * @return
     */
    private double calcPolonium211DecayTime(){
        double randomValue = RAND.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / 1.343)) * 1000;
        return tunnelOutMilliseconds;
    }
    
    /**
     * This main function is used to provide stand-alone testing of the class.
     * 
     * @param args - Unused.
     */
    public static void main(String [] args){
        Polonium211Nucleus nucleus = new Polonium211Nucleus(new NuclearPhysicsClock(24, 10));
        AtomicNucleusNode nucleusNode = new AtomicNucleusNode(nucleus);
        nucleus.setPosition(400, 300);
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        canvas.addWorldChild( nucleusNode );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }
}
