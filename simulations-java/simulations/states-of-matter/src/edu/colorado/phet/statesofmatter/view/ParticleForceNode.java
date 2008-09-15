/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;


/**
 * This class adds the ability to display force-depicting arrows to its super
 * class.
 *
 * @author John Blanco
 */
public class ParticleForceNode extends ParticleNode {

    //-----------------------------------------------------------------------------
    // Class Data
    //-----------------------------------------------------------------------------
    
    // The following constants control some of the aspects of the appearance of
    // the force arrows.  The values are arbitrary and are chosen to look good
    // in this particular sim, so tweak them as needed for optimal appearance.
    private static final double FORCE_ARROW_REFERENCE_LENGTH = 500;
    private static final double FORCE_ARROW_REFERENCE_MAGNITUDE = 1E-22;
    private static final double FORCE_ARROW_TAIL_WIDTH = 100;
    private static final double FORCE_ARROW_HEAD_WIDTH = 200;
    private static final double FORCE_ARROW_HEAD_LENGTH = 200;
    private static final Color ATTRACTIVE_FORCE_COLOR = Color.GREEN;
    private static final Color REPULSIVE_FORCE_COLOR = Color.ORANGE;

    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    private double m_attractiveForce;
    private Vector2DNode m_attractiveForceVectorNode;
    private double m_repulsiveForce;
    private Vector2DNode m_repulsiveForceVectorNode;
    private boolean m_showAttractiveForces;
    private boolean m_showRepulsiveForces;
    
    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------

    public ParticleForceNode( StatesOfMatterAtom particle, ModelViewTransform mvt, boolean useGradient ) {
        super( particle, mvt, useGradient );
        
        m_showAttractiveForces = false;
        m_showRepulsiveForces = false;
        m_attractiveForce = 0;
        m_repulsiveForce = 0;
        
        m_attractiveForceVectorNode = new Vector2DNode(0, 0, FORCE_ARROW_REFERENCE_MAGNITUDE, FORCE_ARROW_REFERENCE_LENGTH);
        m_attractiveForceVectorNode.setMagnitudeAngle( 0, 0 );
        addChild(m_attractiveForceVectorNode);
        m_attractiveForceVectorNode.setArrowFillPaint( ATTRACTIVE_FORCE_COLOR );
        m_attractiveForceVectorNode.setHeadSize( FORCE_ARROW_HEAD_WIDTH, FORCE_ARROW_HEAD_LENGTH );
        m_attractiveForceVectorNode.setTailWidth( FORCE_ARROW_TAIL_WIDTH );
        m_attractiveForceVectorNode.setVisible( m_showAttractiveForces );

        m_repulsiveForceVectorNode = new Vector2DNode(0, 0, FORCE_ARROW_REFERENCE_MAGNITUDE, FORCE_ARROW_REFERENCE_LENGTH);
        m_repulsiveForceVectorNode.setMagnitudeAngle( 0, 0 );
        addChild(m_repulsiveForceVectorNode);
        m_repulsiveForceVectorNode.setArrowFillPaint( REPULSIVE_FORCE_COLOR );
        m_repulsiveForceVectorNode.setHeadSize( FORCE_ARROW_HEAD_WIDTH, FORCE_ARROW_HEAD_LENGTH );
        m_repulsiveForceVectorNode.setTailWidth( FORCE_ARROW_TAIL_WIDTH );
        m_repulsiveForceVectorNode.setVisible( m_showRepulsiveForces );
    }

    public ParticleForceNode( StatesOfMatterAtom particle, ModelViewTransform mvt ) {
        this( particle, mvt, false );
    }
    
    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------
    
    /**
     * Set the levels of attractive and repulsive forces being experienced by
     * the particles in the model so that they may be represented as force
     * vectors.
     */
    public void setForces( double attractiveForce, double repulsiveForce ) {
        m_attractiveForce = attractiveForce;
        m_repulsiveForce = repulsiveForce;
        updateForceVectors();
    }

    //-----------------------------------------------------------------------------
    // Other Public Methods
    //-----------------------------------------------------------------------------
    
    public void setShowAttractiveForces( boolean showForces ){
        
        m_showAttractiveForces = showForces;
        m_attractiveForceVectorNode.setVisible( m_showAttractiveForces );
    }

    public void setShowRepulsiveForces( boolean showForces ){
        
        m_showRepulsiveForces = showForces;
        m_repulsiveForceVectorNode.setVisible( showForces );
    }

    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------
    
    /**
     * Update the force vectors to reflect the forces being experienced by the
     * atom.
     */
    protected void updateForceVectors() {
        m_attractiveForceVectorNode.setMagnitudeAngle( m_attractiveForce, 0 );
        m_repulsiveForceVectorNode.setMagnitudeAngle( m_repulsiveForce, 0 );
    }
}
