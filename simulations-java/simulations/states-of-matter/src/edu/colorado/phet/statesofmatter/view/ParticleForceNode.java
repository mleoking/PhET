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
    public static final Color ATTRACTIVE_FORCE_COLOR = Color.YELLOW;
    public static final Color REPULSIVE_FORCE_COLOR = Color.ORANGE;
    public static final Color TOTAL_FORCE_COLOR = Color.GREEN;
    private static final double COMPONENT_FORCE_ARROW_REFERENCE_LENGTH = 500;
    private static final double COMPONENT_FORCE_ARROW_REFERENCE_MAGNITUDE = 1E-22;
    private static final double TOTAL_FORCE_ARROW_REFERENCE_LENGTH = 1000;
    private static final double TOTAL_FORCE_ARROW_REFERENCE_MAGNITUDE = 1E-22;
    private static final double FORCE_ARROW_TAIL_WIDTH = 100;
    private static final double FORCE_ARROW_HEAD_WIDTH = 200;
    private static final double FORCE_ARROW_HEAD_LENGTH = 200;

    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    private double m_attractiveForce;
    private Vector2DNode m_attractiveForceVectorNode;
    private double m_repulsiveForce;
    private Vector2DNode m_repulsiveForceVectorNode;
    private Vector2DNode m_totalForceVectorNode;
    
    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------

    public ParticleForceNode( StatesOfMatterAtom particle, ModelViewTransform mvt ) {
        super( particle, mvt, true );
        
        m_attractiveForce = 0;
        m_repulsiveForce = 0;
        
        m_attractiveForceVectorNode = new Vector2DNode(0, 0, COMPONENT_FORCE_ARROW_REFERENCE_MAGNITUDE, 
                COMPONENT_FORCE_ARROW_REFERENCE_LENGTH);
        m_attractiveForceVectorNode.setMagnitudeAngle( 0, 0 );
        addChild(m_attractiveForceVectorNode);
        m_attractiveForceVectorNode.setArrowFillPaint( ATTRACTIVE_FORCE_COLOR );
        m_attractiveForceVectorNode.setHeadSize( FORCE_ARROW_HEAD_WIDTH, FORCE_ARROW_HEAD_LENGTH );
        m_attractiveForceVectorNode.setTailWidth( FORCE_ARROW_TAIL_WIDTH );
        m_attractiveForceVectorNode.setVisible( false );

        m_repulsiveForceVectorNode = new Vector2DNode(0, 0, COMPONENT_FORCE_ARROW_REFERENCE_MAGNITUDE, 
                COMPONENT_FORCE_ARROW_REFERENCE_LENGTH);
        m_repulsiveForceVectorNode.setMagnitudeAngle( 0, 0 );
        addChild(m_repulsiveForceVectorNode);
        m_repulsiveForceVectorNode.setArrowFillPaint( REPULSIVE_FORCE_COLOR );
        m_repulsiveForceVectorNode.setHeadSize( FORCE_ARROW_HEAD_WIDTH, FORCE_ARROW_HEAD_LENGTH );
        m_repulsiveForceVectorNode.setTailWidth( FORCE_ARROW_TAIL_WIDTH );
        m_repulsiveForceVectorNode.setVisible( false );

        m_totalForceVectorNode = new Vector2DNode(0, 0, TOTAL_FORCE_ARROW_REFERENCE_MAGNITUDE, 
                TOTAL_FORCE_ARROW_REFERENCE_LENGTH);
        m_totalForceVectorNode.setMagnitudeAngle( 0, 0 );
        addChild(m_totalForceVectorNode);
        m_totalForceVectorNode.setArrowFillPaint( TOTAL_FORCE_COLOR );
        m_totalForceVectorNode.setHeadSize( FORCE_ARROW_HEAD_WIDTH, FORCE_ARROW_HEAD_LENGTH );
        m_totalForceVectorNode.setTailWidth( FORCE_ARROW_TAIL_WIDTH );
        m_totalForceVectorNode.setVisible( false );
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
    
    public void setShowAttractiveForces( boolean showAttractiveForces ){
        m_attractiveForceVectorNode.setVisible( showAttractiveForces );
    }

    public void setShowRepulsiveForces( boolean showRepulsiveForces ){
        m_repulsiveForceVectorNode.setVisible( showRepulsiveForces );
    }
    
    public void setShowTotalForces( boolean showTotalForce ){
        m_totalForceVectorNode.setVisible( showTotalForce );
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
        m_totalForceVectorNode.setMagnitudeAngle( m_attractiveForce + m_repulsiveForce, 0 );
    }
}
