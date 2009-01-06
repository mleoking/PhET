/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;

/**
 * This class is used to represent an alpha particle in the view and extends
 * the functionality of the basic node so that it can track the location of
 * the particle within the model and update itself accordingly.
 *
 * @author John Blanco
 */
public class AlphaParticleModelNode extends AlphaParticleNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private AlphaParticle _alphaParticle;
    private AlphaParticle.Listener _alphaParticleListener;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaParticleModelNode(AlphaParticle alphaParticle)
    {
        _alphaParticle = alphaParticle;
        _alphaParticleListener = new AlphaParticle.Listener(){
            public void positionChanged(AlphaParticle alpha)
            {
                update();
            }
        };
        
        alphaParticle.addListener(_alphaParticleListener);
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Remove all registrations for listeners so that we don't cause memory
     * leaks when we want to get rid of this guy.
     */
    public void cleanup(){
    	_alphaParticle.removeListener(_alphaParticleListener);
    	_alphaParticle = null;
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void update(){
        _displayNode.setOffset( _alphaParticle.getPosition() );
    }
}
