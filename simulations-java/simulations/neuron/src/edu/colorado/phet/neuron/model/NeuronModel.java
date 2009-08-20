/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.neuron.model;


/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeuronModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final NeuronClock clock;
    private Particle particle = new Particle();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NeuronModel( NeuronClock clock ) {
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
