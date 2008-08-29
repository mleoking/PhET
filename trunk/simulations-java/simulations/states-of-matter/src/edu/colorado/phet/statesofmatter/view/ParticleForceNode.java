/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


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
    // the force arrow.  The values are arbitrary and are chosen to look good
    // in this particular sim, so tweak them as needed for optimal appearance.
    private static final double FORCE_ARROW_MAX_LENGTH = 1000;
    private static final double FORCE_ARROW_TAIL_WIDTH = 100;
    private static final double FORCE_ARROW_HEAD_WIDTH = 200;
    private static final double FORCE_ARROW_HEAD_LENGTH = 200;

    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    private Vector2DNode m_forceVectorNode;
    private boolean m_showForces;
    
    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------

    public ParticleForceNode( StatesOfMatterAtom particle, ModelViewTransform mvt, boolean useGradient ) {
        super( particle, mvt, useGradient );
        
        m_showForces = false;
        
        m_forceVectorNode = new Vector2DNode(0, 0, 1000, 1000);
        m_forceVectorNode.setMagnitudeAngle( 500, 0 );
        addChild(m_forceVectorNode);
        m_forceVectorNode.setArrowFillPaint( Color.YELLOW );
        m_forceVectorNode.setHeadSize( FORCE_ARROW_HEAD_WIDTH, FORCE_ARROW_HEAD_LENGTH );
        m_forceVectorNode.setTailWidth( FORCE_ARROW_TAIL_WIDTH );
        m_forceVectorNode.setVisible( m_showForces );
    }

    public ParticleForceNode( StatesOfMatterAtom particle, ModelViewTransform mvt ) {
        this( particle, mvt, false );
    }
    
    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Other Public Methods
    //-----------------------------------------------------------------------------
    
    public void setShowForces( boolean showForces ){
        
        m_showForces = showForces;
        m_forceVectorNode.setVisible( m_showForces );
    }

    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------




    
}
