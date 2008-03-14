/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * AlphaParticleNode - This class is used to represent an alpha particle in
 * the view.
 *
 * @author John Blanco
 */
public class AlphaParticleNode extends PNode {
    
    private final static double PARTICLE_DIAMETER = 4.2d;  // Femto meters.
    
    private PNode _displayImage;
    private AlphaParticle _alphaParticle;
    
    public AlphaParticleNode(AlphaParticle alphaParticle)
    {
        _alphaParticle = alphaParticle;
        
        _displayImage = NuclearPhysics2Resources.getImageNode("Alpha Particle.png");
        _displayImage.scale( PARTICLE_DIAMETER/_displayImage.getWidth() );
        addChild(_displayImage);
        alphaParticle.addListener(new AlphaParticle.Listener(){
            public void positionChanged()
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
