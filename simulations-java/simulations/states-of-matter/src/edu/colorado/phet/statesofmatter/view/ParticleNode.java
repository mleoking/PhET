/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.statesofmatter.model.StatesOfMatterParticleType;
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

    public static final ParticleNode TEST = new ParticleNode(StatesOfMatterParticle.TEST, new ModelViewTransform());

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private StatesOfMatterParticle m_particle;
    private final StatesOfMatterParticle.Listener m_particleListener;
    private ModelViewTransform m_mvt;
    Point2D.Double m_position;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleNode(StatesOfMatterParticle particle, ModelViewTransform mvt) {
        
        m_particle = particle;
        m_mvt = mvt;
        
        // Local initialization.
        m_position = new Point2D.Double();
        
        // Set ourself up to listen to this particle.
        m_particleListener = new StatesOfMatterParticle.Listener(){
            public void positionChanged(){
                updatePosition();
            }
            public void particleRemoved(StatesOfMatterParticle particle){
                handleParticleRemoved(particle);
            }
        };
        particle.addListener( m_particleListener );
        
        // Create the image that will represent this particle.
        PhetPPath path = new PhetPPath(chooseColor());
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
        if (m_particle != null){
            m_mvt.modelToView( m_particle.getPositionReference(), m_position );
            setOffset( m_position );
        }
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    /**
     * Handle the removal of the particle within the model that is being
     * represented in the view by this particle node.  This is done by
     * removing ourself from the canvas and by cleaning up any memory
     * references so that we can be garbage collected.
     * 
     * @param particle
     */
    private void handleParticleRemoved(StatesOfMatterParticle particle){
        
        // Remove ourself from the canvas.
        PNode parent = getParent();
        if (parent != null){
            parent.removeChild( this );
        }
        
        // Remove all children, since they have a reference to this object.
        removeAllChildren();
        
        // Explicitly clear our reference to the particle in the model.
        m_particle = null;
    }
    
    /**
     * Select the color for this particle.
     * 
     * TODO: JPB TBD - For now this is being set on the basis of the particle
     * radius, but down the road it should by on the particle type once that
     * is incorporated into the particle class, i.e. once there is a subclass
     * for each particle type.
     * 
     * @return
     */
    Color chooseColor(){
        
        Color color;
        
        if (m_particle.getRadius() * 2 == StatesOfMatterParticleType.getParticleDiameter( StatesOfMatterParticleType.ARGON )){
            color = new Color(0x6699ff);
        }
        else if (m_particle.getRadius() * 2 == StatesOfMatterParticleType.getParticleDiameter( StatesOfMatterParticleType.NEON )){
            color = new Color(0xff6666);
        }
        else if (m_particle.getRadius() * 2 == StatesOfMatterParticleType.getParticleDiameter( StatesOfMatterParticleType.OXYGEN )){
            color = new Color(0x669933);
        }
        else {
            color = Color.WHITE;
        }
        
        return color;
    }
}
