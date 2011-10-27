// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterGlobalState;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.ModelViewTransform;
import edu.colorado.phet.statesofmatter.view.ParticleContainerNode;
import edu.colorado.phet.statesofmatter.view.StoveNode;
import edu.colorado.phet.statesofmatter.view.instruments.CompositeThermometerNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This is the canvas that represents the play area for the "Solid, Liquid,
 * Gas" tab of this simulation.
 *
 * @author John Blanco
 */
public class SolidLiquidGasCanvas extends PhetPCanvas implements Resettable {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private static final double CANVAS_WIDTH = 23000;
    private static final double CANVAS_HEIGHT = CANVAS_WIDTH * ( 3.0d / 4.0d );

    // Translation factors, used to set origin of canvas area.
    private static final double WIDTH_TRANSLATION_FACTOR = 0.3;  // 0 to 1, 0 is left and 1 is right.
    private static final double HEIGHT_TRANSLATION_FACTOR = 0.72; // 0 to 1, 0 is up and 1 is down.

    // Sizes, in terms of overall canvas size, of the nodes on the canvas.
    private static final double BURNER_NODE_HEIGHT = CANVAS_WIDTH * 0.15;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private MultipleParticleModel m_model;
    private ParticleContainerNode m_particleContainer;
    private CompositeThermometerNode m_thermometerNode;
    private BooleanProperty m_clockRunning = new BooleanProperty( true );

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public SolidLiquidGasCanvas( final MultipleParticleModel multipleParticleModel ) {

        m_model = multipleParticleModel;

        // Create the Model-View transform that we will be using.
        ModelViewTransform m_mvt = new ModelViewTransform( 1.0, 1.0, 0.0, 0.0, false, true );

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
        } );

        // Set the background color.  This may change based on teacher options.
        StatesOfMatterGlobalState.whiteBackground.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean whiteBackground ) {
                if ( whiteBackground ) {
                    // White background.
                    setBackground( Color.WHITE );
                }
                else {
                    // Default background.
                    setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );
                }
            }
        } );

        // Create and add the particle container.
        m_particleContainer = new ParticleContainerNode( m_model, m_mvt, false, false );
        addWorldChild( m_particleContainer );

        // Add a thermometer for displaying temperature.
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        m_thermometerNode = new CompositeThermometerNode( containerRect.getX() + containerRect.getWidth() * 0.18,
                                                          containerRect.getY() + containerRect.getHeight() * 0.30,
                                                          StatesOfMatterConstants.MAX_DISPLAYED_TEMPERATURE,
                                                          StatesOfMatterGlobalState.temperatureUnitsProperty );
        m_thermometerNode.setOffset(
                containerRect.getX() + containerRect.getWidth() * 0.23,
                containerRect.getY() - containerRect.getHeight() * 1.2 );
        addWorldChild( m_thermometerNode );

        // Add a burner that the user can use to add or remove heat from the
        // particle container.
        final StoveNode stoveNode = new StoveNode( m_model, this.getBackground() );
        stoveNode.setScale( BURNER_NODE_HEIGHT / stoveNode.getFullBoundsReference().height );
        stoveNode.setOffset( m_particleContainer.getFullBoundsReference().getCenterX() - stoveNode.getFullBoundsReference().width / 2,
                             m_particleContainer.getFullBoundsReference().getMaxY() + 600 );
        addWorldChild( stoveNode );

        // Add a "Reset All" button.
        final ResetAllButtonNode resetAllButton = new ResetAllButtonNode( new Resettable[] { multipleParticleModel, this }, this, 18, Color.BLACK, new Color( 255, 153, 0 ) ) {{
            setConfirmationEnabled( false );
            scale( 30 ); // Scale to reasonable size.  Scale factor was empirically determined.
            setOffset( stoveNode.getFullBoundsReference().getMinX() - getFullBoundsReference().width - 5000,
                       stoveNode.getFullBoundsReference().getMaxY() - getFullBoundsReference().height );
        }};
        addWorldChild( resetAllButton );

        // Add a floating clock control.
        m_clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isRunning ) {
                multipleParticleModel.getClock().setRunning( isRunning );
            }
        } );
        addWorldChild( new FloatingClockControlNode( m_clockRunning, null,
                                                     multipleParticleModel.getClock(), null,
                                                     new Property<Color>( Color.white ) ) {{
            scale( 30 ); // Scale to reasonable size.  Scale factor was empirically determined.
            setOffset( resetAllButton.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                       resetAllButton.getFullBoundsReference().getMinY() - getFullBoundsReference().height - 200 );
        }} );
    }

    public void reset() {
        m_clockRunning.set( true );  // In case clock was paused prior to reset.
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
}
