/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT   = 0.35;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT  = 0.05;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_BOTTOM = 0.1;
    private static final double NON_CONTAINER_IMAGE_FRACTION_FROM_TOP    = 0.05;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final ParticleContainer m_container;
    private final MultipleParticleModel m_model;
    private PPath m_particleArea;
    private PImage m_cupImage;
    private HashMap m_mapParticlesToNodes;

    private double m_containmentAreaWidth;
    private double m_containmentAreaHeight;
    private double m_containmentAreaOffsetX;
    private double m_containmentAreaOffsetY;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode(PhetPCanvas canvas, MultipleParticleModel model) throws IOException {
        
        super();

        m_model               = model;
        m_container           = model.getParticleContainer();
        
        // Internal initialization.
        m_containmentAreaWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();
        m_mapParticlesToNodes   = new HashMap();

        // Set up the image that will be used.
        m_cupImage = StatesOfMatterResources.getImageNode(StatesOfMatterConstants.COFFEE_CUP_IMAGE);
        
        // Scale the cup image based on the size of the container.
        double neededImageWidth = 
            m_containmentAreaWidth / (1 - NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT - NON_CONTAINER_IMAGE_FRACTION_FROM_RIGHT);
        m_cupImage.setScale( neededImageWidth / m_cupImage.getWidth() );
        
        // Calculate the offset for the area within the overall image where
        // the particles will be contained.
        m_containmentAreaOffsetX = neededImageWidth * NON_CONTAINER_IMAGE_FRACTION_FROM_LEFT;
        m_containmentAreaOffsetY = m_cupImage.getFullBounds().height * NON_CONTAINER_IMAGE_FRACTION_FROM_TOP;
        
        // Add the particle area, which is where the particles will be
        // maintained, which is a rectangular area that matches the size of
        // the container in the model.
        m_particleArea = new PPath(new Rectangle2D.Double(0, 0, m_containmentAreaWidth, m_containmentAreaHeight));
        m_particleArea.setOffset( m_containmentAreaOffsetX, m_containmentAreaOffsetY );
        m_particleArea.setStrokePaint( Color.RED );
        m_particleArea.setStroke( new BasicStroke(50) );
        
        // Register for notifications of particle changes from the model.
        model.addListener( new MultipleParticleModel.Listener(){
            public void particlesMoved(){
                // Update the positions of the particles.
                updateParticleNodes();
            }
            public void resetOccurred(){
                reset();
            }
        });

        addChild(m_cupImage);
        addChild(m_particleArea);
        
        // TODO: JPB TBD - For testing.
//        PPath containerRect = new PPath(new Rectangle2D.Double(1000, 1000, 240, 240));
//        containerRect.setStrokePaint( Color.RED );
//        addChild(containerRect);

        update();
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public List getParticleNodes() {
        return Collections.unmodifiableList(m_particleArea.getChildrenReference());
    }

    public ParticleNode getParticleNode(int i) {
        return (ParticleNode)m_particleArea.getChild(i);
    }

    public void addParticleNode(ParticleNode particleNode) {
        m_particleArea.addChild(particleNode);
    }
    
    /**
     * Reset this not be getting rid of all current particle/node relationships
     * and creating new ones based on the current state of the model.
     */
    public void reset(){
        m_mapParticlesToNodes.clear();
        m_particleArea.removeAllChildren();
        
        List particles = m_model.getParticles();
        
        for ( Iterator iterator = particles.iterator(); iterator.hasNext(); ) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle) iterator.next();
            ParticleNode particleNode = new ParticleNode(particle);
            m_mapParticlesToNodes.put( particle, particleNode );
            System.out.println("Contains key yields: " + m_mapParticlesToNodes.containsKey( particle ));
            setParticleNodePosition(particle, particleNode);
            addParticleNode(particleNode);
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
        
        int numParticles = particles.size();
        for (int i = 0; i < numParticles; i++){
            Object testKey = particles.get( i );
            System.out.println("Contains key yields: " + m_mapParticlesToNodes.containsKey( testKey ));
        }
        
        for ( Iterator iterator = particles.iterator(); iterator.hasNext(); ) {
            StatesOfMatterParticle particle = (StatesOfMatterParticle) iterator.next();
            System.out.println("Contains key yields: " + m_mapParticlesToNodes.containsKey( particle ));
            System.out.println("isEmpty yields: " + m_mapParticlesToNodes.isEmpty());
            Set setOfKeys = m_mapParticlesToNodes.keySet();
            System.out.println("setOfKeys = " + setOfKeys);
            ParticleNode particleNode = (ParticleNode)m_mapParticlesToNodes.get( particle );
            if (particleNode != null){
                setParticleNodePosition( particle, particleNode );
            }
            else{
                System.err.println("Unable to find corresponding particle node for particle in model.");
            }
        }
    }
    
    private void setParticleNodePosition(StatesOfMatterParticle particle, ParticleNode particleNode){
        // Note that particle positions assume that the origin (0,0) is at the
        // lower left corner of the container, and PNodes assume that 0, 0 is
        // the upper left, so translation must occur.
        particleNode.setOffset( particle.getX(), m_containmentAreaHeight - particle.getY() );
    }
}