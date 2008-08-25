/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.interactionpotential;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.module.phasechanges.InteractionPotentialDiagramNode;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This is the canvas that represents the play area for the "Interaction
 * Potential" tab of this simulation.
 *
 * @author John Blanco
 */
public class InteractionPotentialCanvas extends PhetPCanvas {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 5000;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 2.0;
    private final double HEIGHT_TRANSLATION_FACTOR = 1.5;
    
    // Sizes, in terms of overall canvas size, of the nodes on the canvas.
    private final double DIAGRAM_NODE_WIDTH = CANVAS_WIDTH / 1.75;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private DualParticleModel m_model;
    private ModelViewTransform m_mvt;
    private ArrayList m_particleNodes;
    private Hashtable m_particleToNodeMap;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public InteractionPotentialCanvas(DualParticleModel dualParticleModel) {
        
        m_model = dualParticleModel;
        m_particleNodes = new ArrayList();
        m_particleToNodeMap = new Hashtable();
        
        // Set the transform strategy so that the the origin (i.e. point x=0,
        // y = 0) is in a reasonable place.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, 
                new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth()/WIDTH_TRANSLATION_FACTOR, 
                        getHeight()/HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );
        
        // Create the Model-View transform that we will be using.
        m_mvt = new ModelViewTransform(1.0, 1.0, 0.0, 0.0, false, true);
        
        // Add the interaction potential diagram.
        InteractionPotentialDiagramNode diagram = new InteractionPotentialDiagramNode(m_model.getSigma(), 
                m_model.getEpsilon(), true);
        diagram.scale( DIAGRAM_NODE_WIDTH / diagram.getFullBoundsReference().width );
        diagram.setOffset( -(DIAGRAM_NODE_WIDTH / 2), - diagram.getFullBoundsReference().height * 1.3 );
        addWorldChild( diagram );
        
        // Register for notifications of particles.
        m_model.addListener( new DualParticleModel.Adapter(){
            public void particleAdded(StatesOfMatterAtom particle){
                handleParticleAdded( particle );
            }
            public void particleRemoved(StatesOfMatterAtom particle){
                handleParticleRemoved( particle );
            }
        });
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleParticleAdded(StatesOfMatterAtom particle){
        // Add an atom node for this guy.
        boolean useGradient = true;
        if (PhetUtilities.getOperatingSystem() == PhetUtilities.OS_MACINTOSH){
            // We have been having trouble with gradients causing Macs to
            // crash and/or run very slowly, so we don't use them when running
            // there.
            useGradient = false;
        }
        ParticleNode particleNode = new ParticleNode(particle, m_mvt, useGradient);
        addWorldChild( particleNode );
        m_particleToNodeMap.put( particle, particleNode );
    }
    
    private void handleParticleRemoved(StatesOfMatterAtom particle){
        // Get rid of the node for this guy.
        Object particleNode = m_particleToNodeMap.get( particle );
        if (particleNode != null){
            
            // Remove the particle node.
            removeWorldChild( (PNode )particleNode );
            m_particleToNodeMap.remove( particle );
        }
        else{
            System.err.println("Error: Problem encountered removing node from canvas.");
        }
    }
}
