// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.view;


import java.awt.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.controls.IntensitySlider;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.photonabsorption.PhotonAbsorptionResources;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel.PhotonTarget;
import edu.colorado.phet.common.photonabsorption.model.WavelengthConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PNode that represents the photon emitter in the view.  The graphical
 * representation of the emitter changes based on the wavelength of photons
 * that the model is set to emit.
 * <p/>
 * This node is set up such that setting its offset at on the photon emission
 * point in the model should position it correctly.  This assumes that photons
 * are emitted to the right.
 *
 * @author John Blanco
 */
public class PhotonEmitterNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static int SLIDER_RANGE = 100;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private PImage photonEmitterImage;
    private final PhotonAbsorptionModel model;
    private final PNode emitterImageLayer;
    private final PNode emissionControlSliderLayer;
    private final double emitterImageWidth;
    private final IntensitySlider emissionRateControlSlider;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param width - Desired width of the emitter image in screen coords. The
     *              height will be based on the aspect ratio of the image.
     * @param mvt   - Model-view transform for translating between model and
     *              view coordinate systems.
     * @param model
     */
    public PhotonEmitterNode( double width, ModelViewTransform2D mvt, final PhotonAbsorptionModel model ) {

        this.model = model;
        this.emitterImageWidth = width;

        // Listen to the model for events that may cause this node to change
        // state.
        model.addListener( new PhotonAbsorptionModel.Adapter() {
            @Override
            public void emittedPhotonWavelengthChanged() {
                updateImage( emitterImageWidth );
            }
        } );

        // Create the layers on which the other nodes will be placed.
        emitterImageLayer = new PNode();
        addChild( emitterImageLayer );
        emissionControlSliderLayer = new PNode();
        addChild( emissionControlSliderLayer );

        // Add the initial image.
        updateImage( emitterImageWidth );

        // Add the slider that will control the rate of photon emission.
        Dimension emissionControlSliderSize = new Dimension( 100, 30 ); // This may be adjusted as needed for best look.
        emissionRateControlSlider = new IntensitySlider( PhetColorScheme.RED_COLORBLIND, IntensitySlider.HORIZONTAL, emissionControlSliderSize );
        emissionRateControlSlider.setMinimum( 0 );
        emissionRateControlSlider.setMaximum( SLIDER_RANGE );
        emissionRateControlSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double sliderProportion = (double) emissionRateControlSlider.getValue() / (double) SLIDER_RANGE;
                if ( sliderProportion == 0 ) {
                    model.setPhotonEmissionPeriod( Double.POSITIVE_INFINITY );
                }
                else if ( model.getPhotonTarget() == PhotonTarget.CONFIGURABLE_ATMOSPHERE ) {
                    // Note the implicit conversion from frequency to period in the following line.
                    model.setPhotonEmissionPeriod(
                            PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_MULTIPLE_TARGET / sliderProportion );
                }
                else {
                    // Note the implicit conversion from frequency to period in the following line.
                    model.setPhotonEmissionPeriod(
                            PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_SINGLE_TARGET / sliderProportion );
                }
            }
        } );

        EmissionRateControlSliderNode emissionRateControlSliderNode = new EmissionRateControlSliderNode( model );
        PBounds emitterImageBounds = photonEmitterImage.getFullBoundsReference();
        emissionRateControlSliderNode.setOffset(
                emitterImageBounds.getCenterX() - emissionRateControlSliderNode.getFullBoundsReference().getWidth() / 2,
                emitterImageBounds.getCenterY() - emissionRateControlSliderNode.getFullBoundsReference().getHeight() / 2 );

        emissionControlSliderLayer.addChild( emissionRateControlSliderNode );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Set the appropriate image based on the current setting for the
     * wavelength of the emitted photons.
     */
    private void updateImage( double flashlightWidth ) {

        // Clear any existing image.
        emitterImageLayer.removeAllChildren();

        // Create the flashlight image node, setting the offset such that the
        // center right side of the image is the origin.  This assumes that
        // photons will be emitted horizontally to the right.
        if ( model.getEmittedPhotonWavelength() == WavelengthConstants.IR_WAVELENGTH ) {
            photonEmitterImage = new PImage( PhotonAbsorptionResources.getImage( "heat-lamp.png" ) );
        }
        else if ( model.getEmittedPhotonWavelength() == WavelengthConstants.VISIBLE_WAVELENGTH ) {
            photonEmitterImage = new PImage( PhotonAbsorptionResources.getImage( "flashlight2.png" ) );
        }
        else if ( model.getEmittedPhotonWavelength() == WavelengthConstants.UV_WAVELENGTH ) {
            photonEmitterImage = new PImage( PhotonAbsorptionResources.getImage( "uv_light_2.png" ) );
        }
        else if ( model.getEmittedPhotonWavelength() == WavelengthConstants.MICRO_WAVELENGTH ) {
            photonEmitterImage = new PImage( PhotonAbsorptionResources.getImage( "microwave-transmitter.png" ) );
        }

        photonEmitterImage.scale( flashlightWidth / photonEmitterImage.getFullBoundsReference().width );
        photonEmitterImage.setOffset( -flashlightWidth, -photonEmitterImage.getFullBoundsReference().height / 2 );

        emitterImageLayer.addChild( photonEmitterImage );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    // ------------------------------------------------------------------------

    /**
     * Class that implements the slider that is used to control the emission
     * rate of photons.  The slider will update its background color based on
     * the emission wavelength, and will adjust its position as the
     * corresponding setting in the model changes.
     *
     * @author John Blanco
     */
    private static class EmissionRateControlSliderNode extends PNode {

        private final PhotonAbsorptionModel model;
        private final IntensitySlider emissionRateControlSlider;

        public EmissionRateControlSliderNode( final PhotonAbsorptionModel model ) {

            this.model = model;

            // Listen to the model for events that may cause this node to change
            // state.
            this.model.addListener( new PhotonAbsorptionModel.Adapter() {
                @Override
                public void emittedPhotonWavelengthChanged() {
                    update();
                }

                @Override
                public void photonEmissionPeriodChanged() {
                    update();
                }
            } );

            Dimension emissionControlSliderSize = new Dimension( 100, 30 ); // This may be adjusted as needed for best look.
            emissionRateControlSlider = new IntensitySlider( Color.RED, IntensitySlider.HORIZONTAL, emissionControlSliderSize ) {{
                setMinimum( 0 );
                setMaximum( SLIDER_RANGE );
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        double sliderProportion = (double) emissionRateControlSlider.getValue() / (double) SLIDER_RANGE;
                        if ( sliderProportion == 0 ) {
                            model.setPhotonEmissionPeriod( Double.POSITIVE_INFINITY );
                        }
                        else if ( model.getPhotonTarget() == PhotonTarget.CONFIGURABLE_ATMOSPHERE ) {
                            // Note the implicit conversion from frequency to period in the following line.
                            model.setPhotonEmissionPeriod( PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_MULTIPLE_TARGET / sliderProportion );
                        }
                        else {
                            // Note the implicit conversion from frequency to period in the following line.
                            model.setPhotonEmissionPeriod( PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_SINGLE_TARGET / sliderProportion );
                        }
                    }
                } );
            }};

            PSwing emissionRateControlSliderPSwing = new PSwing( emissionRateControlSlider );
            addChild( emissionRateControlSliderPSwing );
        }

        private void update() {
            // Adjust the position of the slider.  Note that we do a conversion
            // between period and frequency and map it into the slider's range.
            int mappedFrequency;
            if ( model.getPhotonTarget() == PhotonTarget.CONFIGURABLE_ATMOSPHERE ) {
                mappedFrequency = (int) Math.round( PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_MULTIPLE_TARGET /
                                                    model.getPhotonEmissionPeriod() * SLIDER_RANGE );
            }
            else {
                mappedFrequency = (int) Math.round( PhotonAbsorptionModel.MIN_PHOTON_EMISSION_PERIOD_SINGLE_TARGET /
                                                    model.getPhotonEmissionPeriod() * SLIDER_RANGE );
            }

            emissionRateControlSlider.setValue( mappedFrequency );

            // Update the color of the slider.
            if ( model.getEmittedPhotonWavelength() == WavelengthConstants.IR_WAVELENGTH ) {
                emissionRateControlSlider.setColor( PhetColorScheme.RED_COLORBLIND );
            }
            else if ( model.getEmittedPhotonWavelength() == WavelengthConstants.VISIBLE_WAVELENGTH ) {
                emissionRateControlSlider.setColor( Color.YELLOW );
            }
            else if ( model.getEmittedPhotonWavelength() == WavelengthConstants.UV_WAVELENGTH ) {
                emissionRateControlSlider.setColor( new Color( 200, 0, 200 ) );
            }
            else if ( model.getEmittedPhotonWavelength() == WavelengthConstants.MICRO_WAVELENGTH ) {
                emissionRateControlSlider.setColor( new Color( 200, 200, 200 ) );
            }
            else {
                System.err.println( getClass().getName() + "- Error: Unrecognized photon." );
            }
        }
    }
}
