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
public class BeamControl extends GraphicLayerSet implements Beam.RateChangeListener, Beam.WavelengthChangeListener {
    private ApparatusPanel apparatusPanel;
    private IntensitySlider intensitySlider;

    private Point intensitySliderRelLoc = new Point( -87, 28 );
    private Dimension intensitySliderSize = new Dimension( 101, 29 );
    private Point intensitySliderLoc;

    private Point spectrumSliderLoc;
    private Point spectrumSliderRelLoc = new Point( -77, 92 );
    private Dimension spectrumSize = new Dimension( 101, 31 );
    private SpectrumSliderWithSquareCursor wavelengthSlider;
    ;

    /**
     * @param apparatusPanel
     * @param location
     * @param beam
     */
    public BeamControl( ApparatusPanel apparatusPanel,
                        Point location,
                        Beam beam,
                        double minWavelength,
                        double maxWavelength,
                        String imageFile ) {
        this.apparatusPanel = apparatusPanel;
        this.setLocation( location );

        // The background panel
        PhetImageGraphic panelGraphic = new PhetImageGraphic( apparatusPanel, imageFile );
        panelGraphic.setRegistrationPoint( 100, 0 );
        addGraphic( panelGraphic );
        panelGraphic.setLocation( 0, 0 );

        intensitySliderLoc = new Point( location.x + intensitySliderRelLoc.x,
                                        location.y + intensitySliderRelLoc.y );
        spectrumSliderLoc = new Point( (int)spectrumSliderRelLoc.getX(),
                                       (int)spectrumSliderRelLoc.getY() );

        addWavelengthSlider( beam, minWavelength, maxWavelength );
        addIntensitySlider( beam, beam.getMaxPhotonsPerSecond() );
        beam.addRateChangeListener( this );
    }

    private void addWavelengthSlider( final Beam beam, double minWavelength, double maxWavelength ) {
        // Make a spectrum wavelengthSlider
        wavelengthSlider = new SpectrumSliderWithSquareCursor( apparatusPanel,
                                                               minWavelength,
                                                               maxWavelength );
        wavelengthSlider.setLocation( spectrumSliderLoc );
        wavelengthSlider.setOrientation( SpectrumSliderWithSquareCursor.HORIZONTAL ); // default is HORIZONTAL
        wavelengthSlider.setTransmissionWidth( 1.0 ); // default is 0.0
        wavelengthSlider.setKnobSize( new Dimension( 15, 12 ) ); // default is (20,30)
        wavelengthSlider.setSpectrumSize( spectrumSize ); // default is (200,25)
        addGraphic( wavelengthSlider, LaserConfig.CONTROL_LAYER );
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

        intensitySlider = new IntensitySlider( VisibleColor.wavelengthToColor( beam.getWavelength() ),
                                               IntensitySlider.HORIZONTAL, intensitySliderSize );
        intensitySlider.setMaximum( (int)maximumRate );
        intensitySlider.setLocation( (int)( intensitySliderLoc.getX() - getRegistrationPoint().getX() ),
                                     (int)( intensitySliderLoc.getY() - getRegistrationPoint().getY() ) );
        apparatusPanel.add( intensitySlider );
        intensitySlider.setValue( 0 );
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beam.setPhotonsPerSecond( intensitySlider.getValue() );
            }
        } );
        beam.addWavelengthChangeListener( new WavelengthChangeListener() );
    }

    public IntensitySlider getIntensityControl() {
        return intensitySlider;
    }

    public class WavelengthChangeListener implements Beam.WavelengthChangeListener {

        public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
            intensitySlider.setColor( VisibleColor.wavelengthToColor( event.getWavelength() ) );
            if( wavelengthSlider.getValue() != (int)( event.getWavelength() ) ) {
                wavelengthSlider.setValue( (int)( event.getWavelength() ) );
            }
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

    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
        // noop
    }
}
