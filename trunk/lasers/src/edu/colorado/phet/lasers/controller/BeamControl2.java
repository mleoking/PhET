package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.control.IntensitySlider;
import edu.colorado.phet.control.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Class: BeamControl2
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Dec 10, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class BeamControl2 extends GraphicLayerSet implements CollimatedBeam.RateChangeListener {
    private ApparatusPanel apparatusPanel;
    private Point location;
    private IntensitySlider photonRateSlider;

    /**
     * @param apparatusPanel
     * @param location
     * @param beam
     * @param maximumRate
     * @param lowerLimitingBeam
     * @param upperLimitingBeam
     */
    public BeamControl2( ApparatusPanel apparatusPanel, Point location,
                         CollimatedBeam beam, double maximumRate,
                         CollimatedBeam lowerLimitingBeam,
                         CollimatedBeam upperLimitingBeam ) {
        this.apparatusPanel = apparatusPanel;
        this.location = location;
        addWavelengthSlider( beam, lowerLimitingBeam, upperLimitingBeam );
        addIntensitySlider( beam, maximumRate );
        beam.addRateChangeListener( this );
    }

    private void addWavelengthSlider( final CollimatedBeam beam,
                                      final CollimatedBeam lowerLimitingBeam,
                                      final CollimatedBeam upperLimitingBeam ) {
        // Make a spectrum wavelengthSlider
        final SpectrumSliderWithSquareCursor wavelengthSliderWithSquareCursor = new SpectrumSliderWithSquareCursor( apparatusPanel );
        wavelengthSliderWithSquareCursor.setLocation( new Point( (int)location.getX() + 10, (int)location.getY() + 50 ) ); // default is (0,0)
        wavelengthSliderWithSquareCursor.setOrientation( SpectrumSliderWithSquareCursor.HORIZONTAL ); // default is HORIZONTAL
        wavelengthSliderWithSquareCursor.setTransmissionWidth( 1.0 ); // default is 0.0
        wavelengthSliderWithSquareCursor.setKnobSize( new Dimension( 10, 15 ) ); // default is (20,30)
        wavelengthSliderWithSquareCursor.setSpectrumSize( new Dimension( 100, 30 ) ); // default is (200,25)
        addGraphic( wavelengthSliderWithSquareCursor, 20 );
        wavelengthSliderWithSquareCursor.setValue( (int)( beam.getWavelength() ) );
        wavelengthSliderWithSquareCursor.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = wavelengthSliderWithSquareCursor.getValue();
                // The wavelength may be limited by the wavelength of another beam
                if( upperLimitingBeam != null && upperLimitingBeam.isEnabled() ) {
                    double limitingWavelength = upperLimitingBeam.getWavelength();
                    int limitingValue = (int)( limitingWavelength );
                    value = Math.max( value, limitingValue );
                }
                if( lowerLimitingBeam != null && lowerLimitingBeam.isEnabled() ) {
                    double limitingWavelength = lowerLimitingBeam.getWavelength();
                    int limitingValue = (int)( limitingWavelength );
                    value = Math.min( value, limitingValue );
                }
                beam.setWavelength( (int)( value ) );
            }
        } );
        wavelengthSliderWithSquareCursor.setValue( (int)( beam.getWavelength() ) );
    }

    private void addIntensitySlider( final CollimatedBeam beam, double maximumRate ) {
        // Make a spectrum photonRateSlider
        photonRateSlider = new IntensitySlider( VisibleColor.wavelengthToColor( beam.getWavelength() ),
                                                IntensitySlider.HORIZONTAL, new Dimension( 100, 30 ) );
        photonRateSlider.setMaximum( (int)maximumRate );
        photonRateSlider.setLocation( new Point( (int)location.getX(), (int)location.getY() ) ); // default is (0,0)
        apparatusPanel.add( photonRateSlider );
        photonRateSlider.setValue( 0 );
        photonRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setPhotonsPerSecond( photonRateSlider.getValue() );
            }
        } );
        beam.addWavelengthChangeListener( new WavelengthChangeListener( photonRateSlider ) );
    }

    public IntensitySlider getIntensityControl() {
        return photonRateSlider;
    }

    public class WavelengthChangeListener implements CollimatedBeam.WavelengthChangeListener {
        private IntensitySlider slider;

        public WavelengthChangeListener( IntensitySlider slider ) {
            this.slider = slider;
        }

        public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event ) {
            slider.setColor( VisibleColor.wavelengthToColor( event.getWavelength() ) );
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        photonRateSlider.setVisible( visible );
    }

    //----------------------------------------------------------------
    // LeftSystemEvent handling
    //----------------------------------------------------------------

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        photonRateSlider.setValue( (int)event.getRate() );
    }
}
