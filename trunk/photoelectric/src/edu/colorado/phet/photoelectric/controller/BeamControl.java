/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.util.SwingThreadModelListener;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.control.IntensitySlider;
import edu.colorado.phet.control.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.quantum.model.Beam;
import edu.colorado.phet.quantum.model.Beam;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * BeamControl
 * <p/>
 * A wavelength slider and intensity slider for controlling a CollimatedBeam
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BeamControl extends GraphicLayerSet implements SwingThreadModelListener,
                                                            Beam.RateChangeListener {
    private ApparatusPanel apparatusPanel;
    private IntensitySlider intensitySlider;

    private Point intensitySliderRelLoc = new Point( 68, 28 );
    private Dimension intensitySliderSize = new Dimension( 142, 16 );
    private Point intensitySliderLoc;

    private Point spectrumSliderLoc;
    private Point spectrumSliderRelLoc = new Point( 13, 93 );
    private Dimension spectrumSize = new Dimension( 145, 19 );
    private SpectrumSliderWithReadout wavelengthSlider;
    private Beam beam;
    private boolean selfUpdating;


    /**
     * @param apparatusPanel
     * @param location
     * @param beam
     * @param maximumRate
     */
    public BeamControl( ApparatusPanel apparatusPanel, Point location,
                        Beam beam, double maximumRate ) {
        this.apparatusPanel = apparatusPanel;
        this.beam = beam;

        // The background panel
        PhetImageGraphic panelGraphic = new PhetImageGraphic( apparatusPanel,
                                                              PhotoelectricConfig.BEAM_CONTROL_PANEL_IMAGE );
        panelGraphic.setRegistrationPoint( 100, 0 );
        addGraphic( panelGraphic );
        panelGraphic.setLocation( 0, 0 );

        intensitySliderLoc = new Point( location.x + intensitySliderRelLoc.x,
                                        location.y + intensitySliderRelLoc.y );
        spectrumSliderLoc = new Point( location.x + spectrumSliderRelLoc.x,
                                       location.y + spectrumSliderRelLoc.y );

        this.setLocation( location );

        addIntensitySlider( beam, maximumRate );
        addWavelengthSlider( beam );
        beam.addRateChangeListener( this );
    }

    private void addWavelengthSlider( final Beam beam ) {
        // Make a spectrum wavelengthSlider
        final SpectrumSliderWithSquareCursor wavelengthSlider1 = new SpectrumSliderWithSquareCursor( apparatusPanel,
                                                                                                     PhotoelectricConfig.MIN_WAVELENGTH,
                                                                                                     PhotoelectricConfig.MAX_WAVELENGTH );
        wavelengthSlider = new SpectrumSliderWithReadout( apparatusPanel,
                                                          wavelengthSlider1,
                                                          beam,
                                                          PhotoelectricConfig.MIN_WAVELENGTH,
                                                          PhotoelectricConfig.MAX_WAVELENGTH,
                                                          spectrumSliderLoc );
        wavelengthSlider.setLocation( spectrumSliderRelLoc ); // default is (0,0)
        wavelengthSlider.setOrientation( SpectrumSliderWithSquareCursor.HORIZONTAL ); // default is HORIZONTAL
        wavelengthSlider.setTransmissionWidth( 1.0 ); // default is 0.0
        wavelengthSlider.setKnobSize( new Dimension( 20, 20 ) ); // default is (20,30)
        wavelengthSlider.setSpectrumSize( spectrumSize ); // default is (200,25)
        addGraphic( wavelengthSlider, DischargeLampsConfig.CONTROL_LAYER );
        wavelengthSlider.setValue( (int)( beam.getWavelength() ) );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                PhetUtilities.invokeLater( new Runnable() {
                    public void run() {
                        int value = wavelengthSlider.getValue();

                        // When the wavelength changes, the photon rate needs to change, too, so
                        // we need to make this call
                        double intensity = beam.getIntensity( PhotoelectricConfig.MAX_WAVELENGTH );

                        beam.setWavelength( (int)( value ) );

                        beam.setIntensity( intensity,
                                           PhotoelectricConfig.MAX_WAVELENGTH );
                        System.out.println( "beam.getPhotonsPerSecond() = " + beam.getPhotonsPerSecond() );
                    }
                } );
            }
        } );
        wavelengthSlider.setValue( (int)( beam.getWavelength() ) );
    }

    private void addIntensitySlider( final Beam beam, double maximumRate ) {
        // Make a spectrum intensitySlider
        intensitySlider = new IntensitySlider( VisibleColor.wavelengthToColor( beam.getWavelength() ),
                                               IntensitySlider.HORIZONTAL, intensitySliderSize );
//        intensitySlider.setMaximum( (int)maximumRate );
        intensitySlider.setMaximum( (int)beam.getMaxIntensity( PhotoelectricConfig.MAX_WAVELENGTH,
                                                               PhotoelectricConfig.MAX_WAVELENGTH ) );
        intensitySlider.setLocation( intensitySliderLoc ); // default is (0,0)
        apparatusPanel.add( intensitySlider );

        IntensityReadout intensityReadout = new IntensityReadout( apparatusPanel, beam );
        intensityReadout.setLocation( (int)( intensitySliderLoc.getX() + intensitySlider.getWidth() ) + 4,
                                      (int)( intensitySliderLoc.getY() + intensitySlider.getHeight() / 2 - intensityReadout.getHeight() / 2 ) - 1 );
        apparatusPanel.addGraphic( intensityReadout, 1E14 );

        intensitySlider.setValue( 0 );
        intensitySlider.addChangeListener( new IntesitySliderChangeListener( beam ) );
        beam.setPhotonsPerSecond( intensitySlider.getValue() );
        beam.addWavelengthChangeListener( new WavelengthChangeListener( intensitySlider ) );
    }

//    public IntensitySlider getIntensityControl() {
//        return intensitySlider;
//    }

    public class WavelengthChangeListener implements Beam.WavelengthChangeListener {
        private IntensitySlider slider;

        public WavelengthChangeListener( IntensitySlider slider ) {
            this.slider = slider;
        }

        /**
         * When the wavelength changes, we want to keep the intensity constant, so we
         * need to adjust the photon rate of the beam
         *
         * @param event
         */
        public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
            double intensity = intensitySlider.getValue();
            beam.setIntensity( intensity, PhotoelectricConfig.MAX_WAVELENGTH );
//            slider.setColor( VisibleColor.wavelengthToColor( event.getWavelength() ) );
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        intensitySlider.setVisible( visible );
    }

    private double sliderValueToPhotonsPerSecond( int sliderValue ) {
        return ( (double)sliderValue / 100 ) * beam.getMaxPhotonsPerSecond();
    }

    private int photonsPerSecondToSliderValue( double photonsPerSecond ) {
        return (int)( ( photonsPerSecond / beam.getMaxPhotonsPerSecond() ) * 100 );
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        if( !selfUpdating ) {
//            intensitySlider.setValue( photonsPerSecondToSliderValue( beam.getPhotonsPerSecond() ) );
//        intensitySlider.setValue( (int)( 100 * beam.getPhotonsPerSecond() / beam.getMaxPhotonsPerSecond() ) );
            intensitySlider.setValue( (int)Math.round( beam.getIntensity( PhotoelectricConfig.MAX_WAVELENGTH ) ) );
        }
    }

    private class IntesitySliderChangeListener implements ChangeListener {
        private final Beam beam;

        public IntesitySliderChangeListener( Beam beam ) {
            this.beam = beam;
        }

        public void stateChanged( ChangeEvent e ) {
            PhetUtilities.invokeLater( new Runnable() {
                public void run() {
                    selfUpdating = true;
//                    beam.setPhotonsPerSecond( sliderValueToPhotonsPerSecond( intensitySlider.getValue() ) );
                    //                        beam.setPhotonsPerSecond( ( intensitySlider.getValue() / 100 ) * beam.getMaxPhotonsPerSecond() );
                    beam.setIntensity( intensitySlider.getValue(),
                                       PhotoelectricConfig.MAX_WAVELENGTH );
                    selfUpdating = false;
                }
            } );
        }
    }
}
