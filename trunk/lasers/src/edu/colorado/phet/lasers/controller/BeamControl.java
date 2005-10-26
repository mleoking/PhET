/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.control.IntensitySlider;
import edu.colorado.phet.control.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.control.SpectrumSlider;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.lasers.model.photon.Beam;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;

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
public class BeamControl extends GraphicLayerSet implements Beam.RateChangeListener {
    private ApparatusPanel apparatusPanel;
    private double minWavelength;
    private double maxWavelength;
    private IntensitySlider intensitySlider;

    private Point intensitySliderRelLoc = new Point( 68, 28 );
    private Dimension intensitySliderSize = new Dimension( 142, 16 );
    private Point intensitySliderLoc;

    private Point spectrumSliderLoc;
    private Point spectrumSliderRelLoc = new Point( 13, 93 );
    private Dimension spectrumSize = new Dimension( 145, 19 );
    private SpectrumSlider wavelengthSlider;

    /**
     * @param apparatusPanel
     * @param location
     * @param beam
     * @param maximumRate
     */
    public BeamControl( ApparatusPanel apparatusPanel, Point location,
                        Beam beam, double maximumRate, double minWavelength, double maxWavelength ) {
        this.apparatusPanel = apparatusPanel;
        this.minWavelength = minWavelength;
        this.maxWavelength = maxWavelength;

        // The background panel
        PhetImageGraphic panelGraphic = new PhetImageGraphic( apparatusPanel,
                                                              LaserConfig.BEAM_CONTROL_PANEL_IMAGE );
        panelGraphic.setRegistrationPoint( 100, 0 );
        addGraphic( panelGraphic );
        panelGraphic.setLocation( 0, 0 );

        intensitySliderLoc = new Point( location.x + intensitySliderRelLoc.x,
                                        location.y + intensitySliderRelLoc.y );
        spectrumSliderLoc = new Point( location.x + spectrumSliderRelLoc.x,
                                       location.y + spectrumSliderRelLoc.y );

        this.setLocation( location );

        addWavelengthSlider( beam );
        addIntensitySlider( beam, maximumRate );
        beam.addRateChangeListener( this );
    }

    private void addWavelengthSlider( final Beam beam ) {
        // Make a spectrum wavelengthSlider
        wavelengthSlider = new SpectrumSlider( apparatusPanel, minWavelength, maxWavelength );
        wavelengthSlider.setLocation( spectrumSliderRelLoc ); // default is (0,0)
        wavelengthSlider.setOrientation( SpectrumSliderWithSquareCursor.HORIZONTAL ); // default is HORIZONTAL
        wavelengthSlider.setTransmissionWidth( 1.0 ); // default is 0.0
        wavelengthSlider.setKnobSize( new Dimension( 20, 20 ) ); // default is (20,30)
        wavelengthSlider.setSpectrumSize( spectrumSize ); // default is (200,25)
        addGraphic( wavelengthSlider, DischargeLampsConfig.CONTROL_LAYER );
        wavelengthSlider.setValue( (int)( beam.getWavelength() ) );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = wavelengthSlider.getValue();
                beam.setWavelength( (int)( value ) );
            }
        } );
        wavelengthSlider.setValue( (int)( beam.getWavelength() ) );
    }

    private void addIntensitySlider( final Beam beam, double maximumRate ) {
        // Make a spectrum intensitySlider
        intensitySlider = new IntensitySlider( VisibleColor.wavelengthToColor( beam.getWavelength() ),
                                               IntensitySlider.HORIZONTAL, intensitySliderSize );
        intensitySlider.setMaximum( (int)maximumRate );
        intensitySlider.setLocation( intensitySliderLoc ); // default is (0,0)
        apparatusPanel.add( intensitySlider );

//        IntensityReadout intensityReadout = new IntensityReadout( apparatusPanel, beam );
//        intensityReadout.setLocation( (int)( intensitySliderLoc.getX() + intensitySlider.getWidth() ) + 4,
//                                      (int)( intensitySliderLoc.getY() + intensitySlider.getHeight() / 2 - intensityReadout.getHeight() / 2 ) - 1 );
//        apparatusPanel.addGraphic( intensityReadout, 1E14 );

        intensitySlider.setValue( 0 );
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setPhotonsPerSecond( intensitySlider.getValue() );
            }
        } );
        beam.addWavelengthChangeListener( new WavelengthChangeListener( intensitySlider ) );
    }

    public IntensitySlider getIntensityControl() {
        return intensitySlider;
    }

    public class WavelengthChangeListener implements Beam.WavelengthChangeListener {
        private IntensitySlider slider;

        public WavelengthChangeListener( IntensitySlider slider ) {
            this.slider = slider;
        }

        public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
            slider.setColor( VisibleColor.wavelengthToColor( event.getWavelength() ) );
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        intensitySlider.setVisible( visible );
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        intensitySlider.setValue( (int)event.getRate() );
    }
}
