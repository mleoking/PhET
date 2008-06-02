/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.SVGNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

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

    // Only a portion of the image will correspond to the location of the
    // particle container within the model.  The rest of the image is "fluff",
    // meaning that it provides visual cues to the user.  These constants
    // define where within the image the particle container should be mapped.
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT   = 0.3;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT  = 0.1;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM = 0.1;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_TOP    = 0.0;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final ParticleContainer m_container;
    private final MultipleParticleModel m_model;
    private PNode m_particleLayer = new PNode();
    private PImage m_cupImage;
    private HashMap m_mapParticlesToNodes;

    private double m_containerWidth;
    private double m_containerHeight;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode(PhetPCanvas canvas, MultipleParticleModel model) throws IOException {
        super();

        m_model               = model;
        m_container           = model.getParticleContainer();
        m_containerWidth      = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containerHeight     = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();
        m_cupImage            = StatesOfMatterResources.getImageNode(StatesOfMatterConstants.COFFEE_CUP_IMAGE);
        m_mapParticlesToNodes = new HashMap();
        
        // Scale the cup image based on the size of the container.
        double neededImageWidth = 
            m_containerWidth / (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT - NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT);
        m_cupImage.setScale( neededImageWidth / m_cupImage.getWidth() );
        
        // Register for notifications of particle changes from the model.
        model.addListener( new MultipleParticleModel.Listener(){
            public void particlesMoved(){
                // Update the positions of the particles.
                updateParticleNodes();
            }
        });

        addChild(m_cupImage);
        addChild(m_particleLayer);
        
        // TODO: JPB TBD - For testing.
//        PPath circle = new PPath(new Ellipse2D.Double(1000, 1000, 240, 240));
//        circle.setPaint( Color.green );
//        addChild(circle);

        reset();
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
    
    public void reset(){
        m_mapParticlesToNodes.clear();
        m_particleLayer.removeAllChildren();
        
        List particles = m_model.getParticles();
        
        for ( Iterator iterator = particles.iterator(); iterator.hasNext(); ) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle) iterator.next();
            ParticleNode particleNode = new ParticleNode(particle);
            m_mapParticlesToNodes.put( particle, particleNode );
            setParticleNodePosition(particle, particleNode);
            addChild(particleNode);
        }
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    // TODO: JPB TBD - Is this needed?
    private void update() {
    }
    
    /**
     * Move the particle nodes to correspond with the positions of the
     * particles within the model.
     */
    public void updateParticleNodes(){
        
        List particles = m_model.getParticles();
        
        for ( Iterator iterator = particles.iterator(); iterator.hasNext(); ) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle) iterator.next();
            ParticleNode particleNode = (ParticleNode)m_mapParticlesToNodes.get( particle );
            if (particleNode != null){
                particleNode.setOffset( particle.getX() - particle.getRadius(), particle.getY() - particle.getRadius() );;
            }
            else{
                System.err.println("Unable to find corresponding particle node for particle in model.");
            }
        }
    }
    
    private void setParticleNodePosition(StatesOfMatterParticle particle, ParticleNode particleNode){
        double radius = particle.getRadius();
        particleNode.setOffset( particle.getX() - radius, particle.getY() - radius );
    }
}