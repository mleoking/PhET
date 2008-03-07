/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

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
    
    private PPath _displayShape;
    private AlphaParticle _alphaParticle;
    
    public AlphaParticleNode(AlphaParticle alphaParticle)
    {
        _alphaParticle = alphaParticle;
        
        _displayShape = new PPath(new Ellipse2D.Double(alphaParticle.getPosition().getX(), alphaParticle.getPosition().getY(), 50, 50));
        _displayShape.setPaint( new Color(200, 100, 0) );
        addChild(_displayShape);
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
        _displayShape.setOffset( _alphaParticle.getPosition() );
    }
}
