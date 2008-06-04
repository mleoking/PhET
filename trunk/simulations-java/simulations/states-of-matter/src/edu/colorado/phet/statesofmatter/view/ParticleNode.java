/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D.Double;

import edu.colorado.phet.common.phetcommon.patterns.Updatable;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is a Piccolo PNode extension that represents a particle in the view.
 *
 * @author John Blanco
 */
public class ParticleNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    public static final ParticleNode TEST = new ParticleNode(StatesOfMatterParticle.TEST);

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final StatesOfMatterParticle m_particle;
    private final StatesOfMatterParticle.Listener m_particleListener;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleNode(StatesOfMatterParticle particle) {
        m_particle = particle;
        
        // Set ourself up to listen to this particle.
        m_particleListener = new StatesOfMatterParticle.Listener(){
            public void positionChanged(){
                updatePosition();
            }
        };
        particle.addListener( m_particleListener );
        
        // Create the image that will represent this particle.
        PhetPPath path = new PhetPPath(Color.blue);
        path.setPathTo( new Ellipse2D.Double(-particle.getRadius(), -particle.getRadius(), particle.getRadius() * 2, 
                particle.getRadius() * 2 ));
        addChild(path);
        
        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
        setChildrenPickable( false );

        updatePosition();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public void updatePosition() {
        setOffset(m_particle.getX(), m_particle.getY());
    }
}
