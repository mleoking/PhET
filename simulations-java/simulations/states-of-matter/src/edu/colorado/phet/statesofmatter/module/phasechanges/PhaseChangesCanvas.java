// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.BicyclePumpNode;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode;
import edu.colorado.phet.statesofmatter.view.StoveNode;
import edu.colorado.phet.statesofmatter.view.TemperatureUnits;
import edu.colorado.phet.statesofmatter.view.instruments.CompositeThermometerNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas where the visual objects for the phase changes tab are placed.
 *
 * @author John Blanco
 */
public class PhaseChangesCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private static final double CANVAS_WIDTH = 24000;
    private static final double CANVAS_HEIGHT = CANVAS_WIDTH * ( 3.0d / 4.0d );

    // Translation factors, used to set origin of canvas area.
    private static final double WIDTH_TRANSLATION_FACTOR = 0.29;  // 0 puts the vertical origin all the way left, 1 is all the way to the right.
    private static final double HEIGHT_TRANSLATION_FACTOR = 0.73; // 0 puts the horizontal origin at the top of the window, 1 puts it at the bottom.

    // Sizes, in terms of overall canvas size, of the nodes on the canvas.
    private static final double BURNER_NODE_HEIGHT = CANVAS_WIDTH * 0.15;
    private static final double PUMP_HEIGHT = CANVAS_HEIGHT / 2;
    private static final double PUMP_WIDTH = CANVAS_WIDTH / 4;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private MultipleParticleModel m_model;
    private ParticleContainerNode m_particleContainer;
    private ModelViewTransform m_mvt;
    private CompositeThermometerNode m_thermometerNode;
    private Random m_rand;
    private double m_rotationRate = 0;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public PhaseChangesCanvas( final MultipleParticleModel multipleParticleModel, Property<TemperatureUnits> temperatureUnits ) {

        m_model = multipleParticleModel;
        m_rand = new Random();

        // Create the Model-View transform that we will be using.
        m_mvt = new ModelViewTransform( 1.0, 1.0, 0.0, 0.0, false, true );

        // Set the transform strategy so that the particle container is in a
        // reasonable place given that point (0,0) on the canvas represents
        // the lower left corner of the particle container.
        setWorldTransformStrategy( new RenderingSizeStrategy( this,
                                                              new PDimension( CANVAS_WIDTH, CANVAS_HEIGHT ) ) {
            protected AffineTransform getPreprocessedTransform() {
                return AffineTransform.getTranslateInstance( getWidth() * WIDTH_TRANSLATION_FACTOR,
                                                             getHeight() * HEIGHT_TRANSLATION_FACTOR );
            }
        } );

        // Set ourself up as a listener to the model.
        m_model.addListener( new MultipleParticleModel.Adapter() {
            public void temperatureChanged() {
                updateThermometerTemperature();
            }

            public void containerSizeChanged() {
                updateThermometerPosition();
            }

            public void containerExplodedStateChanged( boolean containerExploded ) {
                if ( containerExploded ) {
                    // Set a random rotation rate.
                    m_rotationRate = -( Math.PI / 100 + ( m_rand.nextDouble() * Math.PI / 50 ) );
                }
                else {
                    m_rotationRate = 0;
                }
            }
        } );

        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );

        // Create the particle container.
        m_particleContainer = new ParticleContainerNode( m_model, m_mvt, true, true );

        // Get the rectangle that describes the position of the particle
        // container within the model, since the various nodes below will
        // all be positioned relative to it.
        Rectangle2D containerRect = m_model.getParticleContainerRect();

        // Add the pump.
        BicyclePumpNode pump = new BicyclePumpNode( PUMP_WIDTH, PUMP_HEIGHT, m_model );
        pump.setOffset( containerRect.getX() + containerRect.getWidth(),
                        containerRect.getY() - pump.getFullBoundsReference().height -
                        containerRect.getHeight() * 0.2 );
        addWorldChild( pump );

        // Add the particle container after the pressure meter so it can be
        // on top of it.
        addWorldChild( m_particleContainer );

        // Add a thermometer for displaying temperature.
        m_thermometerNode = new CompositeThermometerNode( containerRect.getWidth() * 0.20,
                                                          containerRect.getHeight() * 0.32,
                                                          StatesOfMatterConstants.MAX_DISPLAYED_TEMPERATURE,
                                                          temperatureUnits );
        addWorldChild( m_thermometerNode );
        updateThermometerTemperature();
        updateThermometerPosition();

        // Add a burner that the user can use to add or remove heat from the
        // particle container.
        final StoveNode stoveNode = new StoveNode( m_model, this.getBackground() );
        stoveNode.setScale( BURNER_NODE_HEIGHT / stoveNode.getFullBoundsReference().height );
        stoveNode.setOffset( containerRect.getCenterX() - stoveNode.getFullBoundsReference().width / 2,
                             m_particleContainer.getFullBoundsReference().getMaxY() + 600 );
        addWorldChild( stoveNode );

        // Add a "Reset All" button.
        final ResetAllButtonNode resetAllButton = new ResetAllButtonNode( new Resettable[] { multipleParticleModel }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
            scale( 30 ); // Scale to reasonable size.  Scale factor was empirically determined.
            setOffset( stoveNode.getFullBoundsReference().getMinX() - getFullBoundsReference().width - 5000,
                       stoveNode.getFullBoundsReference().getMaxY() - getFullBoundsReference().height );
        }};
        addWorldChild( resetAllButton );

        // Add a floating clock control.
        final BooleanProperty clockRunning = new BooleanProperty( false );
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isRunning ) {
                multipleParticleModel.getClock().setRunning( isRunning );
            }
        } );
        final FloatingClockControlNode floatingClockControlNode = new FloatingClockControlNode( clockRunning, null,
                                                                                                multipleParticleModel.getClock(), null,
                                                                                                new Property<Color>( Color.white ) ) {{
            scale( 30 ); // Scale to reasonable size.  Scale factor was empirically determined.
            setOffset( resetAllButton.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                       resetAllButton.getFullBoundsReference().getMinY() - getFullBoundsReference().height - 200 );
        }};
        addWorldChild( floatingClockControlNode );

        // Add the button for returning the lid to the container once it has
        // been blown off.
        final TextButtonNode returnLidButton = new TextButtonNode( StatesOfMatterStrings.RETURN_LID, new PhetFont( 18 ), Color.YELLOW ) {{

            // Scale and position the button.  The values used here were
            // empirically determined, adjust as needed for optimal look.
            scale( 30 );
            setOffset( -getFullBoundsReference().getWidth() - 500, -8000 );

            // Tell the model to return the lid when this button is pressed.
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    multipleParticleModel.returnLid();
                }
            } );
        }};
        addWorldChild( returnLidButton );
        returnLidButton.setVisible( multipleParticleModel.getContainerExploded() );

        // Control the visibility of the Return Lid button.  It should only be
        // visible when the container has exploded.
        multipleParticleModel.addListener( new MultipleParticleModel.Adapter() {
            @Override public void containerExplodedStateChanged( boolean containerExploded ) {
                returnLidButton.setVisible( containerExploded );
            }
        } );

        // Make sure that the floating clock control sees the change when the
        // clock gets started.
        multipleParticleModel.getClock().addClockListener( new ClockAdapter() {
            @Override public void clockStarted( ClockEvent clockEvent ) {
                clockRunning.set( true );
            }
        } );
    }

    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
    public void reset() {
        m_particleContainer.reset();
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------

    /**
     * Update the value displayed in the thermometer.
     */
    private void updateThermometerTemperature() {
        m_thermometerNode.setTemperatureInDegreesKelvin( m_model.getTemperatureInKelvin() );
    }

    /**
     * Update the position of the thermometer so that it stays on the lid.
     */
    private void updateThermometerPosition() {
        Rectangle2D containerRect = m_model.getParticleContainerRect();

        if ( !m_model.getContainerExploded() ) {
            if ( m_thermometerNode.getRotation() != 0 ) {
                m_thermometerNode.setRotation( 0 );
            }
        }
        else {
            // The container is exploding, so spin the thermometer.
            m_thermometerNode.rotateInPlace( m_rotationRate );
        }

        m_thermometerNode.setOffset(
                containerRect.getX() + containerRect.getWidth() * 0.24,
                containerRect.getY() - containerRect.getHeight() -
                ( m_thermometerNode.getFullBoundsReference().height * 0.5 ) );
    }
}
