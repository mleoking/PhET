// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
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
public class SolidLiquidGasCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Canvas size in pico meters, since this is a reasonable scale at which
    // to display molecules.  Assumes a 4:3 aspect ratio.
    private final double CANVAS_WIDTH = 23000;
    private final double CANVAS_HEIGHT = CANVAS_WIDTH * ( 3.0d / 4.0d );

    // Translation factors, used to set origin of canvas area.
    private final double WIDTH_TRANSLATION_FACTOR = 0.3;  // 0 to 1, 0 is left and 1 is right.
    private final double HEIGHT_TRANSLATION_FACTOR = 0.72; // 0 to 1, 0 is up and 1 is down.

    // Sizes, in terms of overall canvas size, of the nodes on the canvas.
    private final double BURNER_NODE_WIDTH = CANVAS_WIDTH / 2.5;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private MultipleParticleModel m_model;
    private ParticleContainerNode m_particleContainer;
    private ModelViewTransform m_mvt;
    private CompositeThermometerNode m_thermometerNode;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public SolidLiquidGasCanvas( final MultipleParticleModel multipleParticleModel ) {

        m_model = multipleParticleModel;

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
        } );

        // Set the background color.
        setBackground( StatesOfMatterConstants.CANVAS_BACKGROUND );

        // Create and add the particle container.
        m_particleContainer = new ParticleContainerNode( m_model, m_mvt, false, false );
        addWorldChild( m_particleContainer );

        // Add a thermometer for displaying temperature.
        Rectangle2D containerRect = m_model.getParticleContainerRect();
        m_thermometerNode = new CompositeThermometerNode( containerRect.getX() + containerRect.getWidth() * 0.18,
                                                          containerRect.getY() + containerRect.getHeight() * 0.30,
                                                          StatesOfMatterConstants.MAX_DISPLAYED_TEMPERATURE );
        m_thermometerNode.setOffset(
                containerRect.getX() + containerRect.getWidth() * 0.23,
                containerRect.getY() - containerRect.getHeight() * 1.2 );
        addWorldChild( m_thermometerNode );

        // Add a burner that the user can use to add or remove heat from the
        // particle container.
        final StoveNode stoveNode = new StoveNode( m_model, this.getBackground() );
        stoveNode.setScale( BURNER_NODE_WIDTH / stoveNode.getFullBoundsReference().width );
        stoveNode.setOffset( containerRect.getX() + containerRect.getWidth() * 0.3,
                             containerRect.getY() + containerRect.getHeight() * 0.1 );
        addWorldChild( stoveNode );

        // Add a floating clock control.
        BooleanProperty clockRunning = new BooleanProperty( true );
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean isRunning ) {
                multipleParticleModel.getClock().setRunning( isRunning );
            }
        } );
        addWorldChild( new FloatingClockControlNode( clockRunning, null,
                                                     multipleParticleModel.getClock(), null,
                                                     new Property<Color>( Color.white ) ) {{
            scale( 30 ); // Scale to reasonable size.  Scale factor was empirically determined.
            setOffset( -getFullBoundsReference().width * 1.2,
                       stoveNode.getFullBoundsReference().getMaxY() - getFullBoundsReference().height );
        }} );
    }

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
}
