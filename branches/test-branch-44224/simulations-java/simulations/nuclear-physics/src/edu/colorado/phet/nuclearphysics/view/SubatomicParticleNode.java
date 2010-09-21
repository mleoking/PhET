/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.common.model.SubatomicParticle;
import edu.umd.cs.piccolo.PNode;

/**
 * Abstract class that implements common behavior for representing subatomic
 * particles in the view.
 * 
 * @author John Blanco
 */
public abstract class SubatomicParticleNode extends PNode {
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private SubatomicParticle _particle;
    private SubatomicParticle.Listener _listener;
    
    /**
     * Constructor that creates a node that tracks a model element.
     * 
     * @param particle
     */
    public SubatomicParticleNode(SubatomicParticle particle)
    {
        _particle = particle;
        _listener = new SubatomicParticle.Listener(){
            public void positionChanged(SubatomicParticle particle)
            {
                update();
            }
        };
        
        _particle.addListener(_listener);
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    /**
     * Constructor for an instance that isn't tracking anything in the model.
     */
    public SubatomicParticleNode() {
		_particle = null; // Not tracking any particle.
	}
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public SubatomicParticle getParticle(){
        return _particle;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Remove all registrations for listeners so that we don't cause memory
     * leaks when we want to get rid of this guy.
     */
    public void cleanup(){
    	if (_particle != null){
    		_particle.removeListener(_listener);
    		_particle = null;
    	}
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void update(){
    	if (_particle != null){
    		setOffset( _particle.getPositionReference() );
    	}
    }
}
