/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.util.Random;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.umd.cs.piccolo.PNode;

/**
 * AlphaParticleNode - This class is used to represent an alpha particle in
 * the view.
 *
 * @author John Blanco
 */
public class AlphaParticleNode extends PNode {
    
    private final static double PARTICLE_DIAMETER = 3.2d;  // Femto meters.
    
    private PNode _displayImage;
    private AlphaParticle _alphaParticle;
    
    public AlphaParticleNode(AlphaParticle alphaParticle)
    {
        _alphaParticle = alphaParticle;
        Random rand = new Random();
        
        // Randomly choose an image for this particle.  This is done to give
        // the nucleus a more random and thus realistic look.
        if (rand.nextDouble() > 0.5){
           _displayImage = NuclearPhysicsResources.getImageNode("Alpha Particle 001.jpg");
        }
        else {
            _displayImage = NuclearPhysicsResources.getImageNode("Alpha Particle 002.jpg");            
        }
        
        _displayImage.scale( PARTICLE_DIAMETER/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
        addChild(_displayImage);
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
    
    private void update(){
        _displayImage.setOffset( _alphaParticle.getPosition().getX() - PARTICLE_DIAMETER/2,  
                _alphaParticle.getPosition().getY() - PARTICLE_DIAMETER/2);
    }
}
