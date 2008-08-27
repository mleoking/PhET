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
import edu.colorado.phet.statesofmatter.view.GrabbableParticleNode;
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
    private StatesOfMatterAtom m_fixedParticle;
    private StatesOfMatterAtom m_movableParticle;
    private ParticleNode m_fixedParticleNode;
    private GrabbableParticleNode m_movableParticleNode;
    private InteractionPotentialDiagramNode m_diagram;
    private StatesOfMatterAtom.Listener m_atomListener;
    private boolean m_useGradient;
    

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public InteractionPotentialCanvas(DualParticleModel dualParticleModel) {
        
        m_model = dualParticleModel;
        
        // Decide whether to use gradients when drawing the particles.
        m_useGradient = true;
        if (PhetUtilities.getOperatingSystem() == PhetUtilities.OS_MACINTOSH){
            // We have been having trouble with gradients causing Macs to
            // crash and/or run very slowly, so we don't use them when running
            // there.
            m_useGradient = false;
        }

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
        
        // Create the listener for monitoring particle motion.
        m_atomListener = new StatesOfMatterAtom.Adapter(){
            public void positionChanged(){
                updatePositionMarkerOnDiagram();
            };
        };
        
        // Add the interaction potential diagram.
        m_diagram = new InteractionPotentialDiagramNode(m_model.getSigma(), 
                m_model.getEpsilon(), true);
        m_diagram.scale( DIAGRAM_NODE_WIDTH / m_diagram.getFullBoundsReference().width );
        m_diagram.setOffset( -(DIAGRAM_NODE_WIDTH / 2), - m_diagram.getFullBoundsReference().height * 1.3 );
        addWorldChild( m_diagram );
        
        // Register for notifications of important events from the model.
        m_model.addListener( new DualParticleModel.Adapter(){
            public void fixedParticleAdded(StatesOfMatterAtom particle){
                handleFixedParticleAdded( particle );
            }
            public void fixedParticleRemoved(StatesOfMatterAtom particle){
                handleFixedParticleRemoved( particle );
            }
            public void movableParticleAdded(StatesOfMatterAtom particle){
                handleMovableParticleAdded( particle );
            }
            public void movableParticleRemoved(StatesOfMatterAtom particle){
                handleMovableParticleRemoved( particle );
            }
            public void interactionPotentialChanged(){
                handleInteractionPotentialChanged();
            }
        });
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleFixedParticleAdded(StatesOfMatterAtom particle){
        // Add an atom node for this guy.
        m_fixedParticle = particle;
        m_fixedParticleNode = new ParticleNode(particle, m_mvt, m_useGradient);
        addWorldChild( m_fixedParticleNode );
        particle.addListener( m_atomListener );
        updatePositionMarkerOnDiagram();
    }
    
    private void handleFixedParticleRemoved(StatesOfMatterAtom particle){
        // Get rid of the node for this guy.
        if (m_fixedParticleNode != null){
            
            // Remove the particle node.
            removeWorldChild( m_fixedParticleNode );
        }
        else{
            System.err.println("Error: Problem encountered removing node from canvas.");
        }
        particle.removeListener( m_atomListener );
        updatePositionMarkerOnDiagram();
        m_fixedParticleNode = null;
    }
    
    private void handleMovableParticleAdded(StatesOfMatterAtom particle){
        // Add an atom node for this guy.
        m_movableParticle = particle;
        m_movableParticleNode = new GrabbableParticleNode(m_model, particle, m_mvt, m_useGradient);
        addWorldChild( m_movableParticleNode );
        particle.addListener( m_atomListener );
        updatePositionMarkerOnDiagram();
    }
    
    private void handleMovableParticleRemoved(StatesOfMatterAtom particle){
        // Get rid of the node for this guy.
        if (m_movableParticleNode != null){
            
            // Remove the particle node.
            removeWorldChild( m_movableParticleNode );
        }
        else{
            System.err.println("Error: Problem encountered removing node from canvas.");
        }
        particle.removeListener( m_atomListener );
        updatePositionMarkerOnDiagram();
        m_movableParticleNode = null;
    }
    
    private void handleInteractionPotentialChanged(){
        m_diagram.setLjPotentialParameters( m_model.getSigma(), m_model.getEpsilon() );
    }
    
    /**
     * Update the position marker on the Lennard-Jones potential diagram.
     * This will indicate the amount of potential being experienced between
     * the two atoms in the model.
     */
    private void updatePositionMarkerOnDiagram(){
        
        if ((m_fixedParticle != null) && (m_movableParticle != null))
        {
            double distance = m_fixedParticle.getPositionReference().distance( m_movableParticle.getPositionReference() );

            if (distance > 0){
                m_diagram.setMarkerEnabled( true );
                m_diagram.setMarkerPosition( distance );
            }
            else{
                m_diagram.setMarkerEnabled( false );
            }
        }
        else{
            m_diagram.setMarkerEnabled( false );
        }
    }
}
