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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.controls.SpectrumSliderWithSquareCursor;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.controls.IntensitySlider;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.quantum.model.Beam;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.view.MatchState;

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
    private BaseLaserModule baseLaserModule;
    private IntensitySlider intensitySlider;

    private Point intensitySliderRelLoc = new Point( -87, 28 );
    private Dimension intensitySliderSize = new Dimension( 101, 29 );
    private Point intensitySliderLoc;

    private Point spectrumSliderLoc;
    private Point spectrumSliderRelLoc = new Point( -77, 92 );
    private Dimension spectrumSize = new Dimension( 101, 31 );
    private SpectrumSliderWithSquareCursor wavelengthSlider;
    private Beam beam;

    public BeamControl( ApparatusPanel apparatusPanel,
                        BaseLaserModule baseLaserModule,
                        Point location,
                        Beam beam,
                        double minWavelength,
                        double maxWavelength,
                        BufferedImage image ) {
        this.apparatusPanel = apparatusPanel;
        this.baseLaserModule = baseLaserModule;
        this.beam = beam;
        this.setLocation( location );

        PhetImageGraphic backgroundPanel = new PhetImageGraphic( apparatusPanel, image );
        backgroundPanel.setRegistrationPoint( 100, 0 );
        addGraphic( backgroundPanel );
        backgroundPanel.setLocation( 0, 0 );

        intensitySliderLoc = new Point( location.x + intensitySliderRelLoc.x,
                                        location.y + intensitySliderRelLoc.y );
        spectrumSliderLoc = new Point( (int) spectrumSliderRelLoc.getX(),
                                       (int) spectrumSliderRelLoc.getY() );

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
        wavelengthSlider.setValue( (int) ( beam.getWavelength() ) );
        wavelengthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = wavelengthSlider.getValue();
                beam.setWavelength( value );
            }
        } );
        wavelengthSlider.addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mouseReleased( MouseEvent e ) {
                handleMatch();
            }
        } );
        wavelengthSlider.setValue( (int) ( beam.getWavelength() ) );
    }

    private void handleMatch() {
        MatchState match = baseLaserModule.getMatch( beam );
        if ( match != null ) {
            //match the state
            double exactTransitionEnergy = match.getTransitionEnergy();
            double wavelength = PhysicsUtil.energyToWavelength( Math.abs( exactTransitionEnergy ) );
            System.out.println( "exactTransitionEnergy = " + exactTransitionEnergy + ", wavelength=" + wavelength );
            beam.setWavelength( wavelength );

//            double energy = match.getMatchingEnergy();
//            double wavelength = PhysicsUtil.energyToWavelength( Math.abs( energy ) );
//            System.out.println( "energy = " + energy + ", wavelength=" + wavelength );
//            beam.setWavelength( wavelength );//hopefully fires an update
//            System.out.println( "BeamControl.mouseReleased, match=" + match );
        }
    }

    private void addIntensitySlider( final Beam beam, double maximumRate ) {
        intensitySlider = new IntensitySlider( VisibleColor.wavelengthToColor( beam.getWavelength() ),
                                               IntensitySlider.HORIZONTAL, intensitySliderSize );
        intensitySlider.setMaximum( (int) maximumRate );
        intensitySlider.setLocation( (int) ( intensitySliderLoc.getX() - getRegistrationPoint().getX() ),
                                     (int) ( intensitySliderLoc.getY() - getRegistrationPoint().getY() ) );
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
            if ( wavelengthSlider.getValue() != (int) ( event.getWavelength() ) ) {
                wavelengthSlider.setValue( (int) ( event.getWavelength() ) );
            }
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        intensitySlider.setVisible( visible );
    }

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        intensitySlider.setValue( (int) event.getRate() );
    }

    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
    }
}