/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.view.AtomicNucleusNode;

/**
 * This class represents a non-composite Polonium 211 nucleus.  In other words,
 * this nucleus does not create or keep track of any constituent nucleons.
 *
 * @author John Blanco
 */
public class Polonium211Nucleus extends AbstractAlphaDecayNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.
    public static final int ORIGINAL_NUM_PROTONS = 84;
    public static final int ORIGINAL_NUM_NEUTRONS = 127;
    
    // Half life for Polonium 211.
    public static double HALF_LIFE = 516; // In milliseconds.
    
    // Random number generator used for calculating decay time based on decay constant.
    private static final Random RAND = new Random();
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

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
    
    public double getHalfLife(){
    	return HALF_LIFE;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(){
    	
    	super.reset();
        
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
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / (0.693 / HALF_LIFE)));
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
