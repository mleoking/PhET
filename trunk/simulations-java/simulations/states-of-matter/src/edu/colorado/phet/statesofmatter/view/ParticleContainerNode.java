/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.view.instruments.DialGaugeNode;
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
    
    // Constants that control whether the container is drawn using Java2D,
    // whether it is loaded as a single image, or whether it is loaded as
    // several image pieces.  TODO:  JPB TBD - This has been implemented to
    // ease the comparison of these options and make sure it will work on all
    // platforms.  Once a final decision is made, some or all of this can
    // probably be removed.
    public static final int DRAWN_CONTAINER = 0;
    public static final int SINGLE_IMAGE_CONTAINER = 1;
    public static final int MULTI_IMAGE_CONTAINER = 2;   // Container is made of multiple pieces.
    public static final int CONTAINER_TYPE = MULTI_IMAGE_CONTAINER;
    public static boolean LOAD_CONTAINER_BACKGROUND_IMAGE = false;

    // TODO: JPB TBD - Constant that turns on/off a rectangle that shows the outline of the node.
    // This should be removed when no longer needed.
    private static final boolean SHOW_RECTANGLE = false;

    // Constants that control the appearance of the drawn container.
    private static final Color CONTAINER_EDGE_COLOR = Color.YELLOW;
    private static final float CONTAINER_LINE_WIDTH = 100;
    private static final Stroke CONTAINER_EDGE_STROKE = new BasicStroke(CONTAINER_LINE_WIDTH);
    private static final Stroke HIDDEN_CONTAINER_EDGE_STROKE = new BasicStroke(CONTAINER_LINE_WIDTH / 4,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,0, new float[] {CONTAINER_LINE_WIDTH, CONTAINER_LINE_WIDTH}, 0);
    
    // Constants that control the appearance of the image.
    private static final String CONTAINER_FRONT_IMAGE_NAME = "cup_3D_front_70_split.png";
    private static final String CONTAINER_FRONT_LEFT_IMAGE_NAME = "cup_3D_front_70_split_left.png";
    private static final String CONTAINER_FRONT_RIGHT_IMAGE_NAME = "cup_3D_front_70_split_right.png";
    private static final String CONTAINER_FRONT_TOP_IMAGE_NAME = "cup_3D_front_70_split_top.png";
    private static final String CONTAINER_FRONT_BOTTOM_IMAGE_NAME = "cup_3D_front_70_split_bottom.png";
    private static final String CONTAINER_BACK_TOP_IMAGE_NAME = "cup_3D_top_70_fragment.png";
    private static final String LID_IMAGE_NAME = "cup_3D_cap_70.png";
//    private static final String CONTAINER_BACK_IMAGE_NAME = "cup_3D_back_solid_light.png";
    private static final String CONTAINER_BACK_IMAGE_NAME = "cup_3D_back_light.jpg";
    private static final double LID_POSITION_TWEAK_FACTOR = 65; // Empirically determined value for aligning lid and container body.
    private static final double FRONT_TOP_IMAGE_OFFSET_TWEAK = 19; // Empirically determined value for position corresponding fragment.
    private static final double FRONT_BOTTOM_IMAGE_OFFSET_TWEAK = 17; // Empirically determined value for position corresponding fragment.
    
    // Constant(s) that affect the appearance of both depictions of the container.
    private static final double ELLIPSE_HEIGHT_PROPORTION = 0.15;  // Height of ellipses as a function of overall height.
    private static final double PRESSURE_GAUGE_WIDTH_PROPORTION = 0.44;
    
    // Constants the control the positioning of non-container portions of this node.
    private static final double PRESSURE_METER_Y_OFFSET = -3000;
    private static final double PRESSURE_METER_X_OFFSET_PROPORTION = 0.80;
    
    // Maximum value expected for pressure, in atmospheres.
    private final double MAX_PRESSURE = 100;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final MultipleParticleModel m_model;
    private ModelViewTransform m_mvt;
    private double m_containmentAreaWidth;
    private double m_containmentAreaHeight;
    private PNode m_lowerParticleLayer;
    private PNode m_upperParticleLayer;
    private PNode m_topContainerLayer;
    private PNode m_middleContainerLayer;
    private PNode m_bottomContainerLayer;
    private PNode m_containerLid;
    private PPath m_tempContainerRect;
    private Random m_rand;
    private double m_rotationAmount;
    private DialGaugeNode m_pressureMeter;
    private double m_pressureMeterElbowOffset;
    private double m_containerHeightAtExplosion;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public ParticleContainerNode(MultipleParticleModel model, ModelViewTransform mvt, boolean volumeControlEnabled, 
    		boolean pressureGaugeEnabled) {
        
        m_model = model;
        m_mvt = mvt;
        m_containmentAreaWidth  = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();
        m_rand = new Random();
        
        // Set ourself up as a listener to the model.
        m_model.addListener( new MultipleParticleModel.Adapter(){
            public void particleAdded(StatesOfMatterAtom particle){
                if (particle instanceof HydrogenAtom){
                    m_lowerParticleLayer.addChild( new ParticleNode(particle, m_mvt));
                }
                else{
                    m_upperParticleLayer.addChild( new ParticleNode(particle, m_mvt));
                }
            }
            public void containerSizeChanged(){
                handleContainerSizeChanged();
            }
            public void pressureChanged(){
            	if (m_pressureMeter != null){
            		m_pressureMeter.setValue(m_model.getPressureInAtmospheres());
            	}
            }
            public void containerExploded(){
            	m_containerHeightAtExplosion = m_model.getParticleContainerHeight();
            	m_rotationAmount = Math.PI/100 + (m_rand.nextDouble() * Math.PI/50);
            	if (m_rand.nextBoolean()){
            		m_rotationAmount = -m_rotationAmount;
            	}
            }
        });

        // Create the "layer" nodes in the appropriate order so that the
        // various components of this node can be added appropriately.
        m_bottomContainerLayer = new PNode();
        m_bottomContainerLayer.setPickable( false );
        m_bottomContainerLayer.setChildrenPickable( false );
        addChild( m_bottomContainerLayer );
        m_lowerParticleLayer = new PNode();
        m_lowerParticleLayer.setPickable( false );
        m_lowerParticleLayer.setChildrenPickable( false );
        m_lowerParticleLayer.setOffset( 0, m_containmentAreaHeight );  // Compensate for inverted Y axis.
        addChild( m_lowerParticleLayer );
        m_upperParticleLayer = new PNode();
        m_upperParticleLayer.setPickable( false );
        m_upperParticleLayer.setChildrenPickable( false );
        m_upperParticleLayer.setOffset( 0, m_containmentAreaHeight );  // Compensate for inverted Y axis.
        addChild( m_upperParticleLayer );
        m_middleContainerLayer = new PNode();
        m_middleContainerLayer.setPickable( false );
        m_middleContainerLayer.setChildrenPickable( true );
        addChild( m_middleContainerLayer );
        m_topContainerLayer = new PNode();
        m_topContainerLayer.setPickable( false );
        m_topContainerLayer.setChildrenPickable( false );
        addChild( m_topContainerLayer );
        
        // Create the visual representation of the container.
        if (CONTAINER_TYPE == SINGLE_IMAGE_CONTAINER){
            loadSingleContainerImage();
        }
        else if ( CONTAINER_TYPE == MULTI_IMAGE_CONTAINER ){
            loadMultipleContainerImages();
        }
        else{
            drawContainer();
        }
        
        if (volumeControlEnabled){
            // Add the finger for pressing down on the top of the container.
            PointingHandNode fingerNode = new PointingHandNode( m_model );
            // Note that this node will set its own offset, since it has to be
            // responsible for positioning itself later based on user interaction.
            m_middleContainerLayer.addChild( fingerNode );
            
            // Add the handle to the lid.
            HandleNode handleNode = new HandleNode(50, 100, Color.YELLOW);
            handleNode.rotate( Math.PI / 2 );
            handleNode.setOffset( (m_containerLid.getWidth() / 2) + (handleNode.getFullBoundsReference().width / 2), 0 );
            m_containerLid.addChild( handleNode );
        }
        
        if (pressureGaugeEnabled){
            // Add the pressure meter.
            m_pressureMeter = new DialGaugeNode(PRESSURE_GAUGE_WIDTH_PROPORTION * m_containmentAreaWidth, 
            		StatesOfMatterStrings.PRESSURE_GAUGE_TITLE, 0, MAX_PRESSURE, 
            		StatesOfMatterStrings.PRESSURE_GAUGE_UNITS);
            m_pressureMeter.setElbowEnabled(true);
            m_middleContainerLayer.addChild( m_pressureMeter );
            updatePressureGauge();
            m_pressureMeterElbowOffset =  m_pressureMeter.getFullBoundsReference().getCenterY();
        }
        
        // TODO: JPB TBD - This is temporary for debugging and should
        // be removed at some point.
        if (SHOW_RECTANGLE){
            // Draw a rectangle to show exactly where the container boundaries are.
            m_tempContainerRect = new PPath(m_model.getParticleContainerRect());
            m_tempContainerRect.setStrokePaint( Color.red );
            m_tempContainerRect.setStroke( new BasicStroke(50) );
            addChild( m_tempContainerRect );
        }
        
        // Position this node so that the origin of the canvas, i.e. position
        // x=0, y=0, is at the lower left corner of the container.
        double xPos = 0;
        double yPos = -m_containmentAreaHeight;
        setOffset( xPos, yPos );

        // Set ourself to be non-pickable so that we don't get mouse events.
        setPickable( false );
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    
    public void reset(){
        // Stubbed for now.
    }
    
    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    /**
     * Handle a notification that the container size has changed.
     */
    private void handleContainerSizeChanged(){
        // IMPORTANT NOTE: This routine assumes that only the height of the
        // container can change, since this was true when this routine was
        // created and it isn't worth the effort to make it more general.  If
        // this assumption is ever invalidated, this routine will need to be
        // changed.
        double containerHeight = m_model.getParticleContainerHeight();
        if (!m_model.getContainerExploded()){
        	if (m_containerLid.getRotation() != 0){
        		m_containerLid.setRotation(0);
        	}
        	m_containerLid.setOffset(( m_containmentAreaWidth - m_containerLid.getFullBoundsReference().width) / 2, 
        			m_containmentAreaHeight - containerHeight - (m_containerLid.getFullBoundsReference().height / 2) + LID_POSITION_TWEAK_FACTOR);
        }
        else {
        	// Rotate the lid to create the visual appearance of it being
        	// blown off the top of the container.
        	m_containerLid.rotateAboutPoint(m_rotationAmount, (m_containmentAreaWidth / 2) / m_containerLid.getScale(), 0);
        	double centerPosY = m_containmentAreaHeight - containerHeight - (m_containerLid.getFullBoundsReference().height / 2) + LID_POSITION_TWEAK_FACTOR;
        	double currentPosY = m_containerLid.getOffset().getY();
        	double newPosX = m_containerLid.getOffset().getX();
        	double newPosY;
        	if ( currentPosY > centerPosY ){
        		newPosY = centerPosY;
        	}
        	else{
        		newPosY = currentPosY;
        	}
        	m_containerLid.setOffset(newPosX , newPosY);
        }
        
        updatePressureGauge();
        
        // TODO: JPB TBD temp code.
        if (SHOW_RECTANGLE){
            m_tempContainerRect.setPathTo( m_model.getParticleContainerRect() );
            m_tempContainerRect.setOffset( 0, StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT - m_model.getParticleContainerHeight() );
        }
    }
    
    private void drawContainer() {
        // Create the bottom of the container, which will appear below (or
        // behind) the particles in the Z-order.
        
        double ellipseHeight = m_containmentAreaHeight * ELLIPSE_HEIGHT_PROPORTION;
        
        // Create the bottom of the container.
        PPath hiddenContainerEdge = new PPath(new Ellipse2D.Double(0, 0, m_containmentAreaWidth, ellipseHeight));
        hiddenContainerEdge.setStroke( HIDDEN_CONTAINER_EDGE_STROKE );
        hiddenContainerEdge.setStrokePaint( CONTAINER_EDGE_COLOR );
        m_bottomContainerLayer.addChild( hiddenContainerEdge );

        Ellipse2D outerEllipse = new Ellipse2D.Double( -CONTAINER_LINE_WIDTH / 2, -CONTAINER_LINE_WIDTH / 2, 
                m_containmentAreaWidth + CONTAINER_LINE_WIDTH, ellipseHeight + CONTAINER_LINE_WIDTH );
        Ellipse2D innerEllipse = new Ellipse2D.Double( CONTAINER_LINE_WIDTH / 2, CONTAINER_LINE_WIDTH / 2, 
                m_containmentAreaWidth - CONTAINER_LINE_WIDTH, ellipseHeight - CONTAINER_LINE_WIDTH );
        Rectangle2D topHalfRect = new Rectangle2D.Double(-CONTAINER_LINE_WIDTH / 2, -CONTAINER_LINE_WIDTH / 2,
                m_containmentAreaWidth + CONTAINER_LINE_WIDTH, ellipseHeight / 2);
        Area bottomEdgeArea = new Area(outerEllipse);
        bottomEdgeArea.subtract( new Area(innerEllipse) );
        bottomEdgeArea.subtract( new Area(topHalfRect) );
        PPath bottomFrontContainerEdge = new PPath(bottomEdgeArea);
        bottomFrontContainerEdge.setPaint( CONTAINER_EDGE_COLOR );
        m_bottomContainerLayer.addChild( bottomFrontContainerEdge );

        m_bottomContainerLayer.setOffset( 0, m_containmentAreaHeight - (ellipseHeight / 2) );

        // Create the lid of the container, which will appear above the
        // particles in the Z-order.

        PPath containerTop = new PPath( new Ellipse2D.Double(0, 0, m_containmentAreaWidth, ellipseHeight) );
        containerTop.setStroke( CONTAINER_EDGE_STROKE );
        containerTop.setStrokePaint( CONTAINER_EDGE_COLOR );
        HandleNode containerTopHandle = new HandleNode(m_containmentAreaWidth * 0.18, m_containmentAreaHeight * 0.05, Color.RED);
        m_containerLid = new PNode();
        m_containerLid.setPickable( false );
        m_containerLid.setChildrenPickable( false );
        m_containerLid.addChild( containerTop );
        m_containerLid.addChild( containerTopHandle );
        m_middleContainerLayer.addChild( m_containerLid );
        m_containerLid.setOffset( 0, -ellipseHeight / 2 );
        
        // Create the left and right edges of the container.
        
        PPath containerLeftSide = new PPath( new Line2D.Double(0, 0, 0, m_containmentAreaHeight) );
        containerLeftSide.setStroke( CONTAINER_EDGE_STROKE );
        containerLeftSide.setStrokePaint( CONTAINER_EDGE_COLOR );
        m_topContainerLayer.addChild( containerLeftSide );
        
        PPath containerRightSide = 
            new PPath( new Line2D.Double(m_containmentAreaWidth, 0, m_containmentAreaWidth, m_containmentAreaHeight) );
        containerRightSide.setStroke( CONTAINER_EDGE_STROKE );
        containerRightSide.setStrokePaint( CONTAINER_EDGE_COLOR );
        m_topContainerLayer.addChild( containerRightSide );
    }

    private void loadSingleContainerImage(){
        
        // Load the image that will be used for the front of the container.
        PImage containerImageNode = StatesOfMatterResources.getImageNode(CONTAINER_FRONT_IMAGE_NAME);
        
        // Scale the container image based on the size of the container.
        containerImageNode.setScale( m_containmentAreaWidth / containerImageNode.getWidth() );
        
        // Add the image to the top layer node.
        m_topContainerLayer.addChild(containerImageNode);
        containerImageNode.setOffset( 0, 0 );
        
        // Add the lid of the container.
        m_containerLid = StatesOfMatterResources.getImageNode(LID_IMAGE_NAME);
        m_containerLid.setScale( m_containmentAreaWidth / containerImageNode.getWidth() );
        m_containerLid.setPickable( false );
        m_middleContainerLayer.addChild( m_containerLid );
        m_containerLid.setOffset( 0, (-m_containerLid.getFullBoundsReference().height / 2) + LID_POSITION_TWEAK_FACTOR );

        if (LOAD_CONTAINER_BACKGROUND_IMAGE){
            // Load the image that will be used for the back of the container.
            PImage containerBackImageNode = StatesOfMatterResources.getImageNode(CONTAINER_BACK_IMAGE_NAME);
            
            // Scale the container image based on the size of the container.
            containerBackImageNode.setScale( m_containmentAreaWidth / containerBackImageNode.getFullBoundsReference().width );
            
            // Add the image to the bottom layer node.
            m_bottomContainerLayer.addChild(containerBackImageNode);
            containerBackImageNode.setOffset( 0, -(m_model.getParticleContainerHeight() * ELLIPSE_HEIGHT_PROPORTION / 2) );
        }
    }
    
    private void loadMultipleContainerImages(){
        
        // Load the images that will make up the container.
        PImage containerLeftSideImageNode = StatesOfMatterResources.getImageNode(CONTAINER_FRONT_LEFT_IMAGE_NAME);
        PImage containerRightSideImageNode = StatesOfMatterResources.getImageNode(CONTAINER_FRONT_RIGHT_IMAGE_NAME);
        PImage containerBottomImageNode = StatesOfMatterResources.getImageNode(CONTAINER_FRONT_BOTTOM_IMAGE_NAME);
        PImage containerTopImageNode = StatesOfMatterResources.getImageNode(CONTAINER_FRONT_TOP_IMAGE_NAME);
        
        // Create a single PNode to contain these images, and add the images to it.
        PNode containerNode = new PNode();
        containerNode.addChild( containerLeftSideImageNode );
        containerNode.addChild( containerTopImageNode );
        containerTopImageNode.setOffset( containerLeftSideImageNode.getFullBoundsReference().width, 
                FRONT_TOP_IMAGE_OFFSET_TWEAK );
        containerNode.addChild( containerBottomImageNode );
        containerBottomImageNode.setOffset( containerLeftSideImageNode.getFullBoundsReference().width,
                containerLeftSideImageNode.getFullBoundsReference().height - 
                containerBottomImageNode.getFullBoundsReference().height + FRONT_BOTTOM_IMAGE_OFFSET_TWEAK);
        containerNode.addChild( containerRightSideImageNode );
        containerRightSideImageNode.setOffset( containerBottomImageNode.getFullBoundsReference().getMaxX(), 0 );
        
        // Scale the container node based on the size of the container.
        containerNode.setScale( m_containmentAreaWidth / containerNode.getFullBoundsReference().width );
        
        // Add the image to the top layer node.
        m_topContainerLayer.addChild(containerNode);
        containerNode.setOffset( 0, 0 );
        
        // Add the lid of the container.
        m_containerLid = StatesOfMatterResources.getImageNode(LID_IMAGE_NAME);
        m_containerLid.setScale( m_containmentAreaWidth / m_containerLid.getFullBoundsReference().width );
        m_containerLid.setPickable( false );
        m_middleContainerLayer.addChild( m_containerLid );
        m_containerLid.setOffset( 0, (-m_containerLid.getFullBoundsReference().height / 2) + LID_POSITION_TWEAK_FACTOR );

        if (LOAD_CONTAINER_BACKGROUND_IMAGE){
            // Load the image that will be used for the back of the container.
            PImage containerBackImageNode = StatesOfMatterResources.getImageNode(CONTAINER_BACK_IMAGE_NAME);
            
            // Scale the container image based on the size of the container.
            containerBackImageNode.setScale( m_containmentAreaWidth / containerBackImageNode.getFullBoundsReference().width );
            
            // Add the image to the bottom layer node.
            m_bottomContainerLayer.addChild(containerBackImageNode);
            containerBackImageNode.setOffset( 0, -(m_model.getParticleContainerHeight() * ELLIPSE_HEIGHT_PROPORTION / 2) );
        }
        else {
            // Just load an image for the back of the top.
            PImage containerTopBackImageNode = StatesOfMatterResources.getImageNode(CONTAINER_BACK_TOP_IMAGE_NAME);
            
            // Scale the container image based on the size of the container.
            containerTopBackImageNode.setScale( m_containmentAreaWidth / containerTopBackImageNode.getFullBoundsReference().width );
            
            // Add the image to the bottom layer node.
            m_bottomContainerLayer.addChild(containerTopBackImageNode);
            containerTopBackImageNode.setOffset( 0, -(m_model.getParticleContainerHeight() * ELLIPSE_HEIGHT_PROPORTION / 2) );
        }
    }
    
    /**
     * Update the position and other aspects of the gauge so that it stays
     * connected to the lid or moves as it should when the container
     * explodes.
     */
    private void updatePressureGauge(){
    	
    	if (m_pressureMeter != null){
	        Rectangle2D containerRect = m_model.getParticleContainerRect();
	
	        if (!m_model.getContainerExploded()){
	            if (m_pressureMeter.getRotation() != 0){
	            	m_pressureMeter.setRotation(0);
	            }
	            m_pressureMeter.setOffset(-m_pressureMeter.getFullBoundsReference().width * PRESSURE_METER_X_OFFSET_PROPORTION,
	            		PRESSURE_METER_Y_OFFSET);
	            m_pressureMeter.setElbowHeight(StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT - 
	            		containerRect.getHeight() - m_pressureMeterElbowOffset);
	        }
	        else{
	        	// The container is exploding, so spin and move the gauge.
	        	m_pressureMeter.rotateInPlace(-Math.PI / 20);
	            m_pressureMeter.setOffset(
	            		-m_pressureMeter.getFullBoundsReference().width * PRESSURE_METER_X_OFFSET_PROPORTION, 
	            		PRESSURE_METER_Y_OFFSET + m_containerHeightAtExplosion + 
	            		( ( m_containerHeightAtExplosion - m_model.getParticleContainerHeight() ) * 1.5 ) ); 
	        }
    	}
    }
}