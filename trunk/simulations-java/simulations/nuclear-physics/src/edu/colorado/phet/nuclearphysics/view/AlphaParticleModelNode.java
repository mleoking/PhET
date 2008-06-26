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
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaParticleModelNode(AlphaParticle alphaParticle)
    {
        _alphaParticle = alphaParticle;
        
        alphaParticle.addListener(new AlphaParticle.Listener(){
            public void positionChanged(AlphaParticle alpha)
            {
                update();
            }
            
        });
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    private void update(){
        _displayNode.setOffset( _alphaParticle.getPosition().getX() - NuclearPhysicsConstants.ALPHA_PARTICLE_DIAMETER/2,  
                _alphaParticle.getPosition().getY() - NuclearPhysicsConstants.ALPHA_PARTICLE_DIAMETER/2);
    }
}
