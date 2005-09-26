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

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.control.IntensitySlider;
import edu.colorado.phet.control.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * BeamControl
 * <p/>
 * A wavelength slider and intensity slider for controlling a CollimatedBeam
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class BeamControl extends CompositePhetGraphic implements CollimatedBeam.RateChangeListener {
public class BeamControl extends GraphicLayerSet implements CollimatedBeam.RateChangeListener {
    private ApparatusPanel apparatusPanel;
    private Point location;
    private IntensitySlider intensitySlider;
    private BufferedImage panelImage;

    private Point intensitySliderRelLoc = new Point( 70, 30 );
    private Point intensitySliderLoc;

    private Point spectrumSliderLoc;
    private Point spectrumSliderRelLoc = new Point( 13, 93 );
    private Dimension spectrumSize = new Dimension( 105, 19 );
    private SpectrumSliderWithReadout wavelengthSlider;

    /**
     * @param apparatusPanel
     * @param location
     * @param beam
     * @param maximumRate
     */
    public BeamControl( ApparatusPanel apparatusPanel, Point location,
                        CollimatedBeam beam, double maximumRate ) {
        this.apparatusPanel = apparatusPanel;
        this.location = location;

        // The background panel
        PhetImageGraphic panelGraphic = new PhetImageGraphic( apparatusPanel,
                                                              PhotoelectricConfig.IMAGE_DIRECTORY + "beam-control.png " );
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

    private void addWavelengthSlider( final CollimatedBeam beam ) {
        // Make a spectrum wavelengthSlider
        final SpectrumSliderWithSquareCursor wavelengthSlider1 = new SpectrumSliderWithSquareCursor( apparatusPanel,
                                                                                                     100,
                                                                                                     850 );
        wavelengthSlider = new SpectrumSliderWithReadout( apparatusPanel,
                                                          wavelengthSlider1,
                                                          beam,
                                                          100,
                                                          850,
                                                          spectrumSliderLoc );
        wavelengthSlider.setLocation( spectrumSliderRelLoc ); // default is (0,0)
//        wavelengthSlider.setLocation( spectrumSliderLoc );

//        wavelengthSlider.setLocation( new Point( (int)location.getX() + 10, (int)location.getY() + 70 ) ); // default is (0,0)
        wavelengthSlider.setOrientation( SpectrumSliderWithSquareCursor.HORIZONTAL ); // default is HORIZONTAL
        wavelengthSlider.setTransmissionWidth( 1.0 ); // default is 0.0
        wavelengthSlider.setKnobSize( new Dimension( 20, 20 ) ); // default is (20,30)
        wavelengthSlider.setSpectrumSize( spectrumSize ); // default is (200,25)
//        wavelengthSlider.setSpectrumSize( new Dimension( 150, 25 ) ); // default is (200,25)
//        wavelengthSlider.setSpectrumSize( new Dimension( 150, 30 ) ); // default is (200,25)
//        addGraphic( wavelengthSlider1, DischargeLampsConfig.CONTROL_LAYER );
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

    private void addIntensitySlider( final CollimatedBeam beam, double maximumRate ) {
        // Make a spectrum intensitySlider
        Dimension size = new Dimension( 138, 12 );
//        Dimension size = new Dimension( 150, 20 );
        intensitySlider = new IntensitySlider( VisibleColor.wavelengthToColor( beam.getWavelength() ),
                                               IntensitySlider.HORIZONTAL, size );
        intensitySlider.setMaximum( (int)maximumRate );
        int xLoc = (int)( location.getX() + getWidth() / 2 - size.getWidth() / 2 );
        intensitySlider.setLocation( intensitySliderLoc ); // default is (0,0)
//        intensitySlider.setLocation( new Point( xLoc, (int)location.getY() ) ); // default is (0,0)
//        intensitySlider.setLocation( new Point( (int)location.getX(), (int)location.getY() ) ); // default is (0,0)
        apparatusPanel.add( intensitySlider );

        IntensityReadout intensityReadout = new IntensityReadout( apparatusPanel, beam );
//        intensityReadout.setLocation( (int)(intensitySliderLoc.getX() + intensitySlider.getWidth()),
//                                      20);
        intensityReadout.setLocation( (int)( intensitySliderLoc.getX() + intensitySlider.getWidth() ) + 4,
                                      (int)( intensitySliderLoc.getY() + intensitySlider.getHeight() / 2 - intensityReadout.getHeight() / 2 ) - 1 );
//        intensityReadout.setLocation( xLoc + intensitySlider.getWidth(),
//                                      (int)location.getY() + intensitySlider.getHeight() / 2 - intensityReadout.getHeight() / 2 );
        apparatusPanel.addGraphic( intensityReadout, 1E14 );

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
        intensitySlider.setVisible( visible );
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event ) {
        intensitySlider.setValue( (int)event.getRate() );
    }
}
