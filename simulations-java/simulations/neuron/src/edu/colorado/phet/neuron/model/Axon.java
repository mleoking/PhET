/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;


/**
 * This class represents the main class for modeling the axon.  It acts as the
 * central location where the interaction between the membrane, the atoms
 * (i.e. ions), and the gates is all governed.
 *
 * @author John Blanco
 */
public class Axon {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final NeuronClock clock;
    private Particle particle = new Particle();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Axon( NeuronClock clock ) {
        super();
        
        this.clock = clock;   
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public NeuronClock getClock() {
        return clock;
    }    
    
    public Particle getParticle(){
    	return particle;
    }
}
