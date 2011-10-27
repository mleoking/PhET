// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterGlobalState;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.particle.HydrogenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.view.instruments.DialGaugeNode;
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

    public static final boolean LOAD_CONTAINER_BACKGROUND_IMAGE = false;

    // Constants that control the appearance of the image.
    private static final String CONTAINER_FRONT_LEFT_IMAGE_NAME = "cup_3D_front_70_split_left.png";
    private static final String CONTAINER_FRONT_RIGHT_IMAGE_NAME = "cup_3D_front_70_split_right.png";
    private static final String CONTAINER_FRONT_TOP_IMAGE_NAME = "cup_3D_front_70_split_top.png";
    private static final String CONTAINER_FRONT_BOTTOM_IMAGE_NAME = "cup_3D_front_70_split_bottom.png";
    private static final String CONTAINER_BACK_TOP_IMAGE_NAME = "cup_3D_top_70_fragment.png";
    private static final String LID_IMAGE_NAME = "cup_3D_cap_70.png";
    private static final String CONTAINER_BACK_IMAGE_NAME = "cup_3D_back_light.jpg";
    private static final double LID_POSITION_TWEAK_FACTOR = 65; // Empirically determined value for aligning lid and container body.
    private static final double FRONT_TOP_IMAGE_OFFSET_TWEAK = 19; // Empirically determined value for position corresponding fragment.
    private static final double FRONT_BOTTOM_IMAGE_OFFSET_TWEAK = 17; // Empirically determined value for position corresponding fragment.

    // Constant(s) that affect the appearance of both depictions of the container.
    private static final double ELLIPSE_HEIGHT_PROPORTION = 0.15;  // Height of ellipses as a function of overall height.
    private static final double PRESSURE_GAUGE_WIDTH_PROPORTION = 0.44;

    // Constants the control the positioning of non-container portions of this node.
    private static final double PRESSURE_GAUGE_Y_OFFSET = -3000;
    private static final double PRESSURE_METER_X_OFFSET_PROPORTION = 0.80;

    // Maximum value expected for pressure, in atmospheres.
    private static final double MAX_PRESSURE = 200;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    private final MultipleParticleModel m_model;
    private final ModelViewTransform m_mvt;
    private final double m_containmentAreaWidth;
    private final double m_containmentAreaHeight;
    private PNode m_lowerParticleLayer;
    private PNode m_upperParticleLayer;
    private final PNode m_topContainerLayer;
    private final PNode m_middleContainerLayer;
    private final PNode m_bottomContainerLayer;
    private PNode m_containerLid;
    private final Random m_rand;
    private double m_rotationAmount;
    private DialGaugeNode m_pressureMeter;
    private double m_pressureMeterElbowOffset;
    private double m_containerHeightAtExplosion;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public ParticleContainerNode( MultipleParticleModel model, ModelViewTransform mvt,
                                  boolean volumeControlEnabled, boolean pressureGaugeEnabled ) {

        m_model = model;
        m_mvt = mvt;
        m_containmentAreaWidth = StatesOfMatterConstants.CONTAINER_BOUNDS.getWidth();
        m_containmentAreaHeight = StatesOfMatterConstants.CONTAINER_BOUNDS.getHeight();
        m_rand = new Random();

        // Set ourself up as a listener to the model.
        m_model.addListener( new MultipleParticleModel.Adapter() {
            public void particleAdded( StatesOfMatterAtom particle ) {
                final ParticleNode particleNode = new ParticleNode( particle, m_mvt );
                if ( particle instanceof HydrogenAtom ) {
                    // Hydrogen atoms go on a lower layer so that water looks
                    // good.  Note there there are two types of hydrogen atoms,
                    // so some will end up on the top layer.
                    m_lowerParticleLayer.addChild( particleNode );
                }
                else {
                    m_upperParticleLayer.addChild( particleNode );
                }
                // Since the particles can be hard to see against a white
                // background, turn on their outlines if the background is
                // set this way.
                final VoidFunction1<Boolean> whiteBackgroundObserver = new VoidFunction1<Boolean>() {
                    public void apply( Boolean whiteBackground ) {
                        particleNode.setStrokeEnabled( whiteBackground );
                    }
                };
                StatesOfMatterGlobalState.whiteBackground.addObserver( whiteBackgroundObserver );
                // Avoid memory leaks be removing this observer when the
                // particle goes away.
                particle.addListener( new StatesOfMatterAtom.Adapter() {
                    @Override public void particleRemoved( StatesOfMatterAtom particle ) {
                        StatesOfMatterGlobalState.whiteBackground.removeObserver( whiteBackgroundObserver );
                    }
                } );
            }

            public void containerSizeChanged() {
                handleContainerSizeChanged();
            }

            public void pressureChanged() {
                if ( m_pressureMeter != null ) {
                    m_pressureMeter.setValue( m_model.getPressureInAtmospheres() );
                }
            }

            public void containerExplodedStateChanged( boolean containerExploded ) {
                m_containerHeightAtExplosion = m_model.getParticleContainerHeight();
                m_rotationAmount = Math.PI / 100 + ( m_rand.nextDouble() * Math.PI / 50 );
                if ( m_rand.nextBoolean() ) {
                    m_rotationAmount = -m_rotationAmount;
                }
            }
        } );

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
        loadMultipleContainerImages();

        if ( volumeControlEnabled ) {
            // Add the finger for pressing down on the top of the container.
            PointingHandNode fingerNode = new PointingHandNode( m_model );
            // Note that this node will set its own offset, since it has to be
            // responsible for positioning itself later based on user interaction.
            m_middleContainerLayer.addChild( fingerNode );

            // Add the handle to the lid.
            HandleNode handleNode = new HandleNode( 50, 100, Color.YELLOW );
            handleNode.rotate( Math.PI / 2 );
            handleNode.setOffset( ( m_containerLid.getWidth() / 2 ) + ( handleNode.getFullBoundsReference().width / 2 ), 0 );
            m_containerLid.addChild( handleNode );
        }

        if ( pressureGaugeEnabled ) {
            // Add the pressure meter.
            m_pressureMeter = new DialGaugeNode( PRESSURE_GAUGE_WIDTH_PROPORTION * m_containmentAreaWidth,
                                                 StatesOfMatterStrings.PRESSURE_GAUGE_TITLE, 0, MAX_PRESSURE,
                                                 StatesOfMatterStrings.PRESSURE_GAUGE_UNITS );
            m_pressureMeter.setElbowEnabled( true );
            m_middleContainerLayer.addChild( m_pressureMeter );
            updatePressureGauge();
            m_pressureMeterElbowOffset = m_pressureMeter.getFullBoundsReference().getCenterY();
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

    public void reset() {
        // Stubbed for now.
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    /**
     * Handle a notification that the container size has changed.
     */
    private void handleContainerSizeChanged() {
        // IMPORTANT NOTE: This routine assumes that only the height of the
        // container can change, since this was true when this routine was
        // created and it isn't worth the effort to make it more general.  If
        // this assumption is ever invalidated, this routine will need to be
        // changed.
        double containerHeight = m_model.getParticleContainerHeight();
        if ( !m_model.getContainerExploded() ) {
            if ( m_containerLid.getRotation() != 0 ) {
                m_containerLid.setRotation( 0 );
            }
            m_containerLid.setOffset( ( m_containmentAreaWidth - m_containerLid.getFullBoundsReference().width ) / 2,
                                      m_containmentAreaHeight - containerHeight - ( m_containerLid.getFullBoundsReference().height / 2 ) + LID_POSITION_TWEAK_FACTOR );
        }
        else {
            // Rotate the lid to create the visual appearance of it being
            // blown off the top of the container.
            m_containerLid.rotateAboutPoint( m_rotationAmount, ( m_containmentAreaWidth / 2 ) / m_containerLid.getScale(), 0 );
            double centerPosY = m_containmentAreaHeight - containerHeight - ( m_containerLid.getFullBoundsReference().height / 2 ) + LID_POSITION_TWEAK_FACTOR;
            double currentPosY = m_containerLid.getOffset().getY();
            double newPosX = m_containerLid.getOffset().getX();
            double newPosY;
            if ( currentPosY > centerPosY ) {
                newPosY = centerPosY;
            }
            else {
                newPosY = currentPosY;
            }
            m_containerLid.setOffset( newPosX, newPosY );
        }

        updatePressureGauge();
    }

    private void loadMultipleContainerImages() {

        // Load the images that will make up the container.
        PImage containerLeftSideImageNode = StatesOfMatterResources.getImageNode( CONTAINER_FRONT_LEFT_IMAGE_NAME );
        PImage containerRightSideImageNode = StatesOfMatterResources.getImageNode( CONTAINER_FRONT_RIGHT_IMAGE_NAME );
        PImage containerBottomImageNode = StatesOfMatterResources.getImageNode( CONTAINER_FRONT_BOTTOM_IMAGE_NAME );
        PImage containerTopImageNode = StatesOfMatterResources.getImageNode( CONTAINER_FRONT_TOP_IMAGE_NAME );

        // Create a single PNode to contain these images, and add the images to it.
        PNode containerNode = new PNode();
        containerNode.addChild( containerLeftSideImageNode );
        containerNode.addChild( containerTopImageNode );
        containerTopImageNode.setOffset( containerLeftSideImageNode.getFullBoundsReference().width,
                                         FRONT_TOP_IMAGE_OFFSET_TWEAK );
        containerNode.addChild( containerBottomImageNode );
        containerBottomImageNode.setOffset( containerLeftSideImageNode.getFullBoundsReference().width,
                                            containerLeftSideImageNode.getFullBoundsReference().height -
                                            containerBottomImageNode.getFullBoundsReference().height + FRONT_BOTTOM_IMAGE_OFFSET_TWEAK );
        containerNode.addChild( containerRightSideImageNode );
        containerRightSideImageNode.setOffset( containerBottomImageNode.getFullBoundsReference().getMaxX(), 0 );

        // Scale the container node based on the size of the container.
        containerNode.setScale( m_containmentAreaWidth / containerNode.getFullBoundsReference().width );

        // Add the image to the top layer node.
        m_topContainerLayer.addChild( containerNode );
        containerNode.setOffset( 0, 0 );

        // Add the lid of the container.
        m_containerLid = StatesOfMatterResources.getImageNode( LID_IMAGE_NAME );
        m_containerLid.setScale( m_containmentAreaWidth / m_containerLid.getFullBoundsReference().width );
        m_containerLid.setPickable( false );
        m_middleContainerLayer.addChild( m_containerLid );
        m_containerLid.setOffset( 0, ( -m_containerLid.getFullBoundsReference().height / 2 ) + LID_POSITION_TWEAK_FACTOR );

        if ( LOAD_CONTAINER_BACKGROUND_IMAGE ) {
            // Load the image that will be used for the back of the container.
            PImage containerBackImageNode = StatesOfMatterResources.getImageNode( CONTAINER_BACK_IMAGE_NAME );

            // Scale the container image based on the size of the container.
            containerBackImageNode.setScale( m_containmentAreaWidth / containerBackImageNode.getFullBoundsReference().width );

            // Add the image to the bottom layer node.
            m_bottomContainerLayer.addChild( containerBackImageNode );
            containerBackImageNode.setOffset( 0, -( m_model.getParticleContainerHeight() * ELLIPSE_HEIGHT_PROPORTION / 2 ) );
        }
        else {
            // Just load an image for the back of the top.
            PImage containerTopBackImageNode = StatesOfMatterResources.getImageNode( CONTAINER_BACK_TOP_IMAGE_NAME );

            // Scale the container image based on the size of the container.
            containerTopBackImageNode.setScale( m_containmentAreaWidth / containerTopBackImageNode.getFullBoundsReference().width );

            // Add the image to the bottom layer node.
            m_bottomContainerLayer.addChild( containerTopBackImageNode );
            containerTopBackImageNode.setOffset( 0, -( m_model.getParticleContainerHeight() * ELLIPSE_HEIGHT_PROPORTION / 2 ) );
        }
    }

    /**
     * Update the position and other aspects of the gauge so that it stays
     * connected to the lid or moves as it should when the container
     * explodes.
     */
    private void updatePressureGauge() {

        if ( m_pressureMeter != null ) {
            Rectangle2D containerRect = m_model.getParticleContainerRect();

            if ( !m_model.getContainerExploded() ) {
                if ( m_pressureMeter.getRotation() != 0 ) {
                    m_pressureMeter.setRotation( 0 );
                }
                m_pressureMeter.setOffset( -m_pressureMeter.getFullBoundsReference().width * PRESSURE_METER_X_OFFSET_PROPORTION,
                                           PRESSURE_GAUGE_Y_OFFSET );
                m_pressureMeter.setElbowHeight( StatesOfMatterConstants.PARTICLE_CONTAINER_INITIAL_HEIGHT -
                                                containerRect.getHeight() - m_pressureMeterElbowOffset );
            }
            else {
                // The container is exploding, so spin and move the gauge.
                m_pressureMeter.rotateInPlace( -Math.PI / 20 );
                m_pressureMeter.setOffset( -m_pressureMeter.getFullBoundsReference().width * PRESSURE_METER_X_OFFSET_PROPORTION,
                                           PRESSURE_GAUGE_Y_OFFSET - m_model.getParticleContainerHeight() + m_containerHeightAtExplosion );
            }
        }
    }
}