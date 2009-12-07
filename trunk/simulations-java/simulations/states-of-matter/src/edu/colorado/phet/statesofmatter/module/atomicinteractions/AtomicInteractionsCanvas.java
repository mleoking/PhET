/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.atomicinteractions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.help.DefaultWiggleMe;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.DualAtomModel;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.module.InteractionPotentialDiagramNode;
import edu.colorado.phet.statesofmatter.view.GrabbableParticleNode;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleForceNode;
import edu.colorado.phet.statesofmatter.view.PushpinNode;
import edu.umd.cs.piccolo.PNode;
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
public class AtomicInteractionsCanvas extends PhetPCanvas {
    
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
    private static final double WIGGLE_ME_HEIGHT = CANVAS_HEIGHT * 0.06;
    
    // Constant used to control size of push pin.
    private static final double PUSH_PIN_WIDTH = CANVAS_WIDTH * 0.10;
    
    // The following constant controls whether the wiggle me appears.  This
    // was requested in the original specification, but after being reviewed
    // on 9/4/2008, it was requested that it be removed.  Then, after
    // interviews conducted in late 2009, it was decide that it should be
    // added back.  The constant is being kept in case this decision is
    // reversed (again) at some point in the future.
    private static final boolean ENABLE_WIGGLE_ME = true;
    
    // Constant to turn on/off a set of vertical lines that can be used to
    // check the alignment between the graph and the atoms.
    private static final boolean SHOW_ALIGNMENT_LINES = false;
        
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private DualAtomModel m_model;
    private ModelViewTransform m_mvt;
    private StatesOfMatterAtom m_fixedParticle;
    private StatesOfMatterAtom m_movableParticle;
    private ParticleForceNode m_fixedParticleNode;
    private GrabbableParticleNode m_movableParticleNode;
    private InteractionPotentialDiagramNode m_interactionPotentialDiagram;
    private StatesOfMatterAtom.Listener m_atomListener;
    private boolean m_showAttractiveForces;
    private boolean m_showRepulsiveForces;
    private boolean m_showTotalForces;
    private GradientButtonNode m_retrieveAtomButtonNode;
    private DefaultWiggleMe m_wiggleMe;
    private boolean m_wiggleMeShown;
    private PushpinNode m_pushPinNode;
    private PNode m_fixedParticleLayer;
    private PNode m_movableParticleLayer;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public AtomicInteractionsCanvas(DualAtomModel dualParticleModel) {
        
        m_model = dualParticleModel;
        m_showAttractiveForces = false;
        m_showRepulsiveForces = false;
        m_showTotalForces = false;
        m_wiggleMeShown = false;
        m_movableParticle = m_model.getMovableAtomRef();
        m_fixedParticle = m_model.getFixedAtomRef();
        
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
        m_model.addListener( new DualAtomModel.Adapter(){
            public void fixedAtomAdded(StatesOfMatterAtom particle){
                handleFixedParticleAdded( particle );
            }
            public void fixedAtomRemoved(StatesOfMatterAtom particle){
                handleFixedParticleRemoved( particle );
            }
            public void movableAtomAdded(StatesOfMatterAtom particle){
                handleMovableParticleAdded( particle );
            }
            public void movableAtomRemoved(StatesOfMatterAtom particle){
                handleMovableParticleRemoved( particle );
            }
            public void movableAtomDiameterChanged(){
            	updateMinimumXForMovableAtom();
            };
            public void fixedAtomDiameterChanged(){
            	updateMinimumXForMovableAtom();
            };
        });

        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );
        
        // Create the listener for monitoring particle motion.
        m_atomListener = new StatesOfMatterAtom.Adapter(){
            public void positionChanged(){
            	handlePositionChanged();
            }
            
            public void radiusChanged(){
            	handleParticleRadiusChanged();
            };
        };
        
        // Add the interaction potential diagram.  IMPORTANT NOTE: The diagram
        // needs to be sized so that one picometer on the canvas is the same as
        // one picometer on the diagram.  Hence the somewhat tricky scaling
        // calculation.
        m_interactionPotentialDiagram = new InteractiveInteractionPotentialDiagram(m_model.getSigma(),
                m_model.getEpsilon(), true, m_model);
        double desiredWidth = m_interactionPotentialDiagram.getXAxisRange() 
        		/ m_interactionPotentialDiagram.getXAxisGraphProportion();
        double diagramScaleFactor = desiredWidth / m_interactionPotentialDiagram.getFullBoundsReference().width;
        m_interactionPotentialDiagram.scale( diagramScaleFactor );
        
        // Position the diagram so that the x origin lines up with the fixed particle.
        m_interactionPotentialDiagram.setOffset( -m_interactionPotentialDiagram.getFullBoundsReference().width * m_interactionPotentialDiagram.getXAxisOffsetProportion(), 
                  -m_interactionPotentialDiagram.getFullBoundsReference().height * 1.3 );
        addWorldChild( m_interactionPotentialDiagram );
        
        // Add the button for retrieving the atom to the canvas. 
        m_retrieveAtomButtonNode = new GradientButtonNode(StatesOfMatterStrings.RETRIEVE_ATOM, 16, new Color(0xffcc66));
        m_retrieveAtomButtonNode.scale( BUTTON_HEIGHT / m_retrieveAtomButtonNode.getFullBoundsReference().height );
        m_retrieveAtomButtonNode.setVisible( false );
        addWorldChild( m_retrieveAtomButtonNode );
        m_retrieveAtomButtonNode.setOffset( 
                m_interactionPotentialDiagram.getFullBoundsReference().getMaxX() - m_retrieveAtomButtonNode.getFullBoundsReference().width,
                StatesOfMatterConstants.MAX_SIGMA / 3 * 1.1 );  // Almost fully below the largest atom.
        
        // Register to receive button pushes.
        m_retrieveAtomButtonNode.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                // Move the rogue particle back to its initial position.
                m_model.resetMovableAtomPos();
            }
        });
        
        // Create the push pin node that will be used to convey the idea that
        // the fixed atom is pinned to the canvas.  It will be added to the
        // canvas when the particles appear.
        m_pushPinNode = new PushpinNode();
        m_pushPinNode.scale( PUSH_PIN_WIDTH / m_pushPinNode.getFullBoundsReference().width );

        // Create the nodes that will act as layers for the fixed and movable
        // particles.  This is done so that the fixed particle can always
        // appear to be on top.
        m_movableParticleLayer = new PNode();
        addWorldChild( m_movableParticleLayer );
        m_fixedParticleLayer = new PNode();
        addWorldChild( m_fixedParticleLayer );
        
        // Create the nodes that will represent the particles in the view.
        if (m_movableParticle != null){
        	handleMovableParticleAdded( m_movableParticle );
        }
        if (m_fixedParticle != null){
        	handleFixedParticleAdded( m_fixedParticle );
        }
        
        // The following code creates a set of vertical lines that can be used
        // to make sure that the atoms are lining up correctly with the
        // various parts of the graph.
        
        if (SHOW_ALIGNMENT_LINES){
        	
        	PhetPPath fixedAtomVerticalCenterMarker = new PhetPPath(new Line2D.Double(0, 0, 0, 1000), 
        			new BasicStroke(7), Color.PINK);
        	fixedAtomVerticalCenterMarker.setOffset(0, -1000);
        	addWorldChild(fixedAtomVerticalCenterMarker);
        	
        	PhetPPath movableAtomVerticalCenterMarker = new PhetPPath(new Line2D.Double(0, 0, 0, 1000), 
        			new BasicStroke(7), Color.ORANGE);
        	movableAtomVerticalCenterMarker.setOffset(m_model.getMovableAtomRef().getX(), -1000);
        	addWorldChild(movableAtomVerticalCenterMarker);
        	
        	PhetPPath rightSideOfChartMarker = new PhetPPath(new Line2D.Double(0, 0, 0, 1000), 
        			new BasicStroke(7), Color.GREEN);
        	rightSideOfChartMarker.setOffset(1100, -1000);
        	addWorldChild(rightSideOfChartMarker);
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
            m_wiggleMe = new DefaultWiggleMe( this, StatesOfMatterStrings.WIGGLE_ME_CAPTION );
            m_wiggleMe.setBackground(Color.YELLOW);
            m_wiggleMe.setArrowTailPosition( MotionHelpBalloon.LEFT_CENTER );
            double wiggleMeScale = WIGGLE_ME_HEIGHT / m_wiggleMe.getFullBoundsReference().height;
            m_wiggleMe.scale( wiggleMeScale );
            addWorldChild( m_wiggleMe );
            
            // Animate from off the screen to the position of the movable atom.
            Rectangle viewportBounds = getBounds();
            Point2D wiggleMeInitialXPos = new Point2D.Double(viewportBounds.getMaxX(), 0);
            getPhetRootNode().screenToWorld(wiggleMeInitialXPos);
            wiggleMeInitialXPos.setLocation(wiggleMeInitialXPos.getX() + m_wiggleMe.getFullBoundsReference().width / 2, 0);
            m_wiggleMe.setOffset( wiggleMeInitialXPos.getX(), m_model.getMovableAtomRef().getY() );
            m_wiggleMe.animateToPositionScaleRotation( 
            		m_model.getMovableAtomRef().getX() + m_model.getMovableAtomRef().getRadius(),
            		m_model.getMovableAtomRef().getY(), wiggleMeScale, 0, 3000 );
            
            // Clicking anywhere on the canvas makes the wiggle me go away.
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    m_wiggleMe.setEnabled( false );
                    removeWorldChild( m_wiggleMe );
                    removeInputEventListener( this );
                    m_wiggleMe = null;
                }
            } );
            
            // Indicate that the wiggle-me has been shown so that we don't end
            // up showing it again.
            m_wiggleMeShown = true;
        }
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleFixedParticleAdded(StatesOfMatterAtom particle){
        
        m_fixedParticle = particle;
        m_fixedParticleNode = new ParticleForceNode(particle, m_mvt, true, true);
        m_fixedParticleNode.setShowAttractiveForces( m_showAttractiveForces );
        m_fixedParticleNode.setShowRepulsiveForces( m_showRepulsiveForces );
        m_fixedParticleNode.setShowTotalForces( m_showTotalForces );
        m_fixedParticleLayer.addChild( m_fixedParticleNode );
        
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
            m_fixedParticleLayer.removeChild( m_fixedParticleNode );
            
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
        m_movableParticleNode = new GrabbableParticleNode(m_model, particle, m_mvt, true, true, 
        		0, Double.POSITIVE_INFINITY);
        m_movableParticleNode.setShowAttractiveForces( m_showAttractiveForces );
        m_movableParticleNode.setShowRepulsiveForces( m_showRepulsiveForces );
        m_movableParticleNode.setShowTotalForces( m_showTotalForces );
        m_movableParticleLayer.addChild( m_movableParticleNode );

        // Limit the particle's motion in the X direction so that it can't
        // get to where there is too much overlap, or is on the other side
        // of the fixed particle.
        updateMinimumXForMovableAtom();
        
        // Add ourself as a listener.
        particle.addListener( m_atomListener );
        
        // Update the position marker to represent the new particle's position.
        updatePositionMarkerOnDiagram();
    }
    
    private void handleMovableParticleRemoved(StatesOfMatterAtom particle){
        // Get rid of the node for this guy.
        if (m_movableParticleNode != null){
            
            // Remove the particle node.
            m_movableParticleLayer.removeChild( m_movableParticleNode );
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
		if (m_model.getMotionPaused()){
		    if (m_fixedParticleNode.getGradientEnabled()){
		    	m_fixedParticleNode.setGradientEnabled(false);
			}
		    if (m_movableParticleNode.getGradientEnabled()){
		    	m_movableParticleNode.setGradientEnabled(false);
		    }
		}
	}
	
	private void handlePositionChanged() {
		
		if (!m_model.getMotionPaused()){
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
             ( m_model.getMovableAtomRef().getX() > (1 - WIDTH_TRANSLATION_FACTOR) * getWorldSize().getWidth())) {
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
                m_interactionPotentialDiagram.setMarkerEnabled( true );
                m_interactionPotentialDiagram.setMarkerPosition( distance );
            }
            else{
                m_interactionPotentialDiagram.setMarkerEnabled( false );
            }
        }
        else{
            m_interactionPotentialDiagram.setMarkerEnabled( false );
        }
    }
    
    /**
     * Update the minimum X value allowed for the movable atom.  This prevents
     * too much overlap between the atoms.
     */
    private void updateMinimumXForMovableAtom(){
    	if (m_movableParticle != null && m_fixedParticle != null){
//    		m_movableParticleNode.setMinX( ( m_fixedParticle.getRadius() + m_movableParticle.getRadius() ) * 0.35 );
    		m_movableParticleNode.setMinX( m_model.getSigma() * 0.9 );
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
