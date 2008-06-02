/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.SVGNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class is the "view" for the particle container.  This is where the
 * information about the nature of the image that is used to depict the
 * container is encapsulated.
 *
 * @author John Blanco
 */
public class ParticleContainerNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final ParticleContainer m_container;
    private PNode m_particleLayer = new PNode();
    private PImage m_cupImage;
    private double m_containerWidth;
    private double m_containerHeight;

    private static final double CUP_OVERSIZE = 1.2;
    private static final double CUP_X_OFFSET = 1.6;
    private static final double CUP_Y_OFFSET = 0.6;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode(PhetPCanvas canvas, ParticleContainer container) throws IOException {
        super();

        m_container       = container;
        m_containerWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth()  * CUP_OVERSIZE;
        m_containerHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight() * CUP_OVERSIZE;
        m_cupImage        = StatesOfMatterResources.getImageNode("coffee-cup-image.png");

        addChild(m_cupImage);
        addChild(m_particleLayer);

        update();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public List getParticleNodes() {
        return Collections.unmodifiableList(m_particleLayer.getChildrenReference());
    }

    public ParticleNode getParticleNode(int i) {
        return (ParticleNode)m_particleLayer.getChild(i);
    }

    public void addParticleNode(ParticleNode particleNode) {
        m_particleLayer.addChild(particleNode);
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    private void update() {
        Rectangle2D b = m_container.getShape().getBounds2D();
        
        // TODO: JPB TBD - May need to position or resize the container here, but not sure yet.
        // These are currently just emirically determined constants to make things look somewhat
        // reasonable.
        m_cupImage.setOffset( -11, -4 );
        m_cupImage.setScale( 0.03 );

    }
}