/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.interactionpotential;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.help.DefaultWiggleMe;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.DualParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.module.InteractionPotentialDiagramNode;
import edu.colorado.phet.statesofmatter.view.GrabbableParticleNode;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleForceNode;
import edu.colorado.phet.statesofmatter.view.PushpinNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
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
    private static final double CANVAS_WIDTH = 2000;
    private static final double CANVAS_HEIGHT = CANVAS_WIDTH * (3.0d/4.0d);
    
    // Translation factors, used to set origin of canvas area.
    private static final double WIDTH_TRANSLATION_FACTOR = 0.3;   // 0 puts the vertical origin all the way left, 1
                                                                  // is all the way to the right.
    private static final double HEIGHT_TRANSLATION_FACTOR = 0.78; // 0 puts the horizontal origin at the top of the 
                                                                  // window, 1 puts it at the bottom.
    
    // Constant used to control size of button.
    private static final double BUTTON_HEIGHT = CANVAS_WIDTH * 0.06;
    
    // Constant used to control size of wiggle me.
    private static final double WIGGLE_ME_HEIGHT = CANVAS_HEIGHT * 0.10;
    
    // Constant used to control size of push pin.
    private static final double PUSH_PIN_WIDTH = CANVAS_WIDTH * 0.10;
    
    // The following constant control whether the wiggle me appears.  This
    // was requested in the original specification, but after being reviewed
    // on 9/4/2008, it was requested that it be removed.  It is being left
    // in the code in case it is ever brought back.  Note that it is not fully
    // debugged, so will likely take some effort to get it working correctly.
    private static final boolean ENABLE_WIGGLE_ME = false;
        
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private DualParticleModel m_model;
    private ModelViewTransform m_mvt;
    private StatesOfMatterAtom m_fixedParticle;
    private StatesOfMatterAtom m_movableParticle;
    private ParticleForceNode m_fixedParticleNode;
    private GrabbableParticleNode m_movableParticleNode;
    private InteractionPotentialDiagramNode m_interactionPotentialdiagram;
    private StatesOfMatterAtom.Listener m_atomListener;
    private boolean m_showAttractiveForces;
    private boolean m_showRepulsiveForces;
    private boolean m_showTotalForces;
    private GradientButtonNode m_retrieveAtomButtonNode;
    private DefaultWiggleMe m_wiggleMe;
    private boolean m_wiggleMeShown;
    private PushpinNode m_pushPinNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public InteractionPotentialCanvas(DualParticleModel dualParticleModel) {
        
        m_model = dualParticleModel;
        m_showAttractiveForces = false;
        m_showRepulsiveForces = false;
        m_showTotalForces = false;
        m_wiggleMeShown = false;
        m_movableParticle = m_model.getMovableParticleRef();
        m_fixedParticle = m_model.getFixedParticleRef();
        
        // Set the transform strategy so that the the origin (i.e. point x=0,
        // y = 0) is in a reasonable place.
        setWorldTransformStrategy( new RenderingSizeStrategy(this, new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT) ){
            protected AffineTransform getPreprocessedTransform(){
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR, 
                        getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        });
        
        // Create the Model-View transform that we will be using.
        m_mvt = new ModelViewTransform(1.0, 1.0, 0.0, 0.0, false, true);
        
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
            public void movableParticleDiameterChanged(){
            	if (m_movableParticle != null){
            		m_movableParticleNode.setMinX( m_movableParticle.getRadius() * 1.9 );
            	}
            };
        });

        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );
        
        // Create the listener for monitoring particle motion.
        m_atomListener = new StatesOfMatterAtom.Adapter(){
            public void positionChanged(){
            	handlePositionChanged();
            }
            
            public void particleRadiusChanged(){
            	handleParticleRadiusChanged();
            };
        };
        
        // Add the interaction potential diagram.  IMPORTANT NOTE: The diagram
        // needs to be sized so that one picometer on the canvas is the same as
        // one picometer on the diagram.  Hence the somewhat tricky scaling
        // calculation.
        m_interactionPotentialdiagram = new InteractiveInteractionPotentialDiagram(m_model.getFixedMoleculeSigma(),
                m_model.getEpsilon(), true,m_model);
        double desiredWidth = m_interactionPotentialdiagram.getXAxisRange() + 
                ((1 - m_interactionPotentialdiagram.getXAxisGraphProportion()) * m_interactionPotentialdiagram.getXAxisRange());
        double diagramScaleFactor = desiredWidth / m_interactionPotentialdiagram.getFullBoundsReference().width;
        m_interactionPotentialdiagram.scale( diagramScaleFactor );
        
        // Position the diagram so that the x origin lines up with the fixed particle.
        m_interactionPotentialdiagram.setOffset( -m_interactionPotentialdiagram.getFullBoundsReference().width * (1 - m_interactionPotentialdiagram.getXAxisGraphProportion()), 
                  -m_interactionPotentialdiagram.getFullBoundsReference().height * 1.3 );
        addWorldChild( m_interactionPotentialdiagram );
        
        // Add the button for retrieving the atom to the canvas. 
        m_retrieveAtomButtonNode = new GradientButtonNode(StatesOfMatterStrings.RETRIEVE_ATOM, 16, new Color(0xffcc66));
        m_retrieveAtomButtonNode.scale( BUTTON_HEIGHT / m_retrieveAtomButtonNode.getFullBoundsReference().height );
        m_retrieveAtomButtonNode.setVisible( false );
        addWorldChild( m_retrieveAtomButtonNode );
        m_retrieveAtomButtonNode.setOffset( 
                m_interactionPotentialdiagram.getFullBoundsReference().getMaxX() - m_retrieveAtomButtonNode.getFullBoundsReference().width,
                StatesOfMatterConstants.MAX_SIGMA / 3 * 1.1 );  // Almost fully below the largest atom.
        
        // Register to receive button pushes.
        m_retrieveAtomButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                // Move the rogue particle back to its initial position.
                m_model.resetMovableParticlePos();
            }
        });
        
        // Create the push pin node that will be used to convey the idea that
        // the fixed atom is pinned to the canvas.  It will be added to the
        // canvas when the particles appear.
        m_pushPinNode = new PushpinNode();
        m_pushPinNode.scale( PUSH_PIN_WIDTH / m_pushPinNode.getFullBoundsReference().width );

        // Create the nodes that will represent the particles in the view.
        if (m_movableParticle != null){
        	handleMovableParticleAdded( m_movableParticle );
        }
        if (m_fixedParticle != null){
        	handleFixedParticleAdded( m_fixedParticle );
        }
    }
    
    //----------------------------------------------------------------------------
    // Public and Protected Methods
    //----------------------------------------------------------------------------
    
    /**
     * Turn on/off the displaying of the force arrows that represent the
     * attractive force.
     */
    public void setShowAttractiveForces( boolean showForces ){
        m_movableParticleNode.setShowAttractiveForces( showForces );
        m_fixedParticleNode.setShowAttractiveForces( showForces );
        m_showAttractiveForces = showForces;
    }
    
    /**
     * Turn on/off the displaying of the force arrows that represent the
     * repulsive force.
     */
    public void setShowRepulsiveForces( boolean showForces ){
        m_movableParticleNode.setShowRepulsiveForces( showForces );
        m_fixedParticleNode.setShowRepulsiveForces( showForces );
        m_showRepulsiveForces = showForces;
    }
    
    /**
     * Turn on/off the displaying of the force arrows that represent the
     * total force, i.e. attractive plus repulsive.
     */
    public void setShowTotalForces( boolean showForces ){
        m_movableParticleNode.setShowTotalForces( showForces );
        m_fixedParticleNode.setShowTotalForces( showForces );
        m_showTotalForces = showForces;
    }
    
    protected void updateLayout() {
        
        if ( getWorldSize().getWidth() <= 0 || getWorldSize().getHeight() <= 0 ) {
            // The canvas hasn't been sized yet, so don't try to lay it out.
            return;
        }
        
        if ((!m_wiggleMeShown) && (ENABLE_WIGGLE_ME)){
            // The wiggle me has not yet been shown, so show it.
            m_wiggleMe = new DefaultWiggleMe( this, "Move atom and release." );  // Note: Make this a string if the wiggle-me is ever added back in.
            m_wiggleMe.setArrowTailPosition( MotionHelpBalloon.BOTTOM_CENTER );
            double wiggleMeScale = WIGGLE_ME_HEIGHT / m_wiggleMe.getFullBoundsReference().height;
            m_wiggleMe.scale( wiggleMeScale );
            addWorldChild( m_wiggleMe );
            
            // Animate from off to the left to the position of the movable atom.
            PBounds diagramBounds = m_interactionPotentialdiagram.getFullBoundsReference();
            m_wiggleMe.setOffset( diagramBounds.getMinX() - (diagramBounds.width * 0.5), 
                    m_interactionPotentialdiagram.getFullBoundsReference().getMaxY() + m_wiggleMe.getFullBoundsReference().height );
            m_wiggleMe.animateToPositionScaleRotation( m_model.getMovableParticleRef().getX(),
                    m_interactionPotentialdiagram.getFullBoundsReference().getMaxY() + m_wiggleMe.getFullBoundsReference().height, wiggleMeScale, 0, 5000 );
            
            // Clicking anywhere on the canvas makes the wiggle me go away.
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    m_wiggleMe.setEnabled( false );
                    removeWorldChild( m_wiggleMe );
                    removeInputEventListener( this );
                    m_wiggleMe = null;
                }
            } );
        }
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleFixedParticleAdded(StatesOfMatterAtom particle){
        
        m_fixedParticle = particle;
        m_fixedParticleNode = new ParticleForceNode(particle, m_mvt, true);
        m_fixedParticleNode.setShowAttractiveForces( m_showAttractiveForces );
        m_fixedParticleNode.setShowRepulsiveForces( m_showRepulsiveForces );
        m_fixedParticleNode.setShowTotalForces( m_showTotalForces );
        addWorldChild( m_fixedParticleNode );
        
        particle.addListener( m_atomListener );
        
        updatePositionMarkerOnDiagram();
        
        // Add the push pin last so that it is on top of the fixed atom.
        // Note that the particulars of how this is positioned will need to
        // change if a different image is used.
        addWorldChild( m_pushPinNode );
        m_pushPinNode.setOffset( m_fixedParticle.getRadius() * 0.25, m_fixedParticle.getRadius() * 0.1);
    }
    
    private void handleFixedParticleRemoved(StatesOfMatterAtom particle){
        // Get rid of the node for this guy.
        if (m_fixedParticleNode != null){
            
            // Remove the particle node.
            removeWorldChild( m_fixedParticleNode );
            
            // Remove the pin holding the node.
            removeWorldChild( m_pushPinNode );
        }
        else{
            System.err.println("Error: Problem encountered removing node from canvas.");
        }
        particle.removeListener( m_atomListener );
        updatePositionMarkerOnDiagram();
        m_fixedParticleNode = null;
    }
    
    private void handleMovableParticleAdded(StatesOfMatterAtom particle){

    	// Add the atom node for this guy.

    	m_movableParticle = particle;
        m_movableParticleNode = new GrabbableParticleNode(m_model, particle, m_mvt, true, 0, 
        		Double.POSITIVE_INFINITY);
        m_movableParticleNode.setShowAttractiveForces( m_showAttractiveForces );
        m_movableParticleNode.setShowRepulsiveForces( m_showRepulsiveForces );
        m_movableParticleNode.setShowTotalForces( m_showTotalForces );
        addWorldChild( m_movableParticleNode );

        // Limit the particle's motion in the X direction so that it can
        // only slightly overlap with the fixed particle.
        m_movableParticleNode.setMinX( m_movableParticle.getRadius() * 1.9 );
        
        // Add ourself as a listener.
        particle.addListener( m_atomListener );
        
        // Update the position marker to represent the new particle's position.
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

    /**
     * Handle a notification of a change in the radius of a particle.
     * IMPORTANT NOTE: This is part of a workaround for a problem with
     * rendering the spherical nodes.  To make a long story short, there were
     * problems with resizing the nodes if they were being drawn with a
     * gradient, so this (and other) code was added to effectively turn off
     * the gradient while the particle was being resized and turn it back
     * on when it started moving again.
     */
	private void handleParticleRadiusChanged() {

		// The particles are being resized, so disable the gradients if they
		// are being used and if motion is paused.
		if (m_model.getParticleMotionPaused()){
		    if (m_fixedParticleNode.getGradientEnabled()){
		    	m_fixedParticleNode.setGradientEnabled(false);
			}
		    if (m_movableParticleNode.getGradientEnabled()){
		    	m_movableParticleNode.setGradientEnabled(false);
		    }
		}
	}
	
	private void handlePositionChanged() {
		
		if (!m_model.getParticleMotionPaused()){
			if (!m_fixedParticleNode.getGradientEnabled()){
	    		// The movable particle is moving, so turn the gradient
	    		// back on.
	    		m_fixedParticleNode.setGradientEnabled(true);
	    	}
	    	if (!m_movableParticleNode.getGradientEnabled()){
	    		// The movable particle is moving, so turn the gradient
	    		// back on.
	    		m_movableParticleNode.setGradientEnabled(true);
	    	}
		}
    	
        updatePositionMarkerOnDiagram();
        updateForceVectors();
        
        if ( ( getWorldSize().getWidth() > 0 ) &&
             ( m_model.getMovableParticleRef().getX() > (1 - WIDTH_TRANSLATION_FACTOR) * getWorldSize().getWidth())) {
            if ( !m_retrieveAtomButtonNode.isVisible() ) {
                // The particle is off the canvas and the button is not
                // yet shown, so show it.
                m_retrieveAtomButtonNode.setVisible( true );
            }
        }
        else if ( m_retrieveAtomButtonNode.isVisible() ) {
            // The particle is on the canvas but the button is visible
            // (which it shouldn't be), so hide it.
            m_retrieveAtomButtonNode.setVisible( false );
        }
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
                m_interactionPotentialdiagram.setMarkerEnabled( true );
                m_interactionPotentialdiagram.setMarkerPosition( distance );
            }
            else{
                m_interactionPotentialdiagram.setMarkerEnabled( false );
            }
        }
        else{
            m_interactionPotentialdiagram.setMarkerEnabled( false );
        }
    }
    
    private void updateForceVectors(){
        
        if ((m_fixedParticle != null) && (m_movableParticle != null))
        {
            m_fixedParticleNode.setForces( m_model.getAttractiveForce(), -m_model.getRepulsiveForce() );
            m_movableParticleNode.setForces( -m_model.getAttractiveForce(), m_model.getRepulsiveForce() );
        }
    }
}
