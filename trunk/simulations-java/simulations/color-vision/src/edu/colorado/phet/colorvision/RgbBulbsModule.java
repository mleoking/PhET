// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision;

import java.awt.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.colorvision.control.ColorIntensitySlider;
import edu.colorado.phet.colorvision.control.RgbBulbsControlPanel;
import edu.colorado.phet.colorvision.event.VisibleColorChangeEvent;
import edu.colorado.phet.colorvision.event.VisibleColorChangeListener;
import edu.colorado.phet.colorvision.help.IntensitySliderWiggleMe;
import edu.colorado.phet.colorvision.help.WiggleMe;
import edu.colorado.phet.colorvision.model.Person;
import edu.colorado.phet.colorvision.model.PhotonBeam;
import edu.colorado.phet.colorvision.model.Spotlight;
import edu.colorado.phet.colorvision.view.PersonGraphic;
import edu.colorado.phet.colorvision.view.PhotonBeamGraphic;
import edu.colorado.phet.colorvision.view.SpotlightGraphic;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;

/**
 * RgbBulbsModule implements the simulation module that demonstrates how color
 * vision works in the context of three lights (red, green and blue).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RgbBulbsModule extends PhetGraphicsModule implements ChangeListener, VisibleColorChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double PERSON_BACKGROUND_LAYER = 1;
    private static final double RED_BEAM_LAYER = 2;
    private static final double GREEN_BEAM_LAYER = 3;
    private static final double BLUE_BEAM_LAYER = 4;
    private static final double RED_SPOTLIGHT_LAYER = 5;
    private static final double GREEN_SPOTLIGHT_LAYER = 6;
    private static final double BLUE_SPOTLIGHT_LAYER = 7;
    private static final double PERSON_FOREGROUND_LAYER = 8;
    private static final double WIGGLE_ME_LAYER = 9;

    // Colors
    private static final Color APPARATUS_BACKGROUND = ColorVisionConstants.APPARATUS_BACKGROUND;

    // Locations of model components
    private static final double PERSON_X = 450;
    private static final double PERSON_Y = 25;
    private static final double SPOTLIGHT_X = 120;
    private static final double RED_SPOTLIGHT_X = SPOTLIGHT_X;
    private static final double RED_SPOTLIGHT_Y = 105;
    private static final double GREEN_SPOTLIGHT_X = SPOTLIGHT_X;
    private static final double GREEN_SPOTLIGHT_Y = 325;
    private static final double BLUE_SPOTLIGHT_X = SPOTLIGHT_X;
    private static final double BLUE_SPOTLIGHT_Y = 545;

    // Locations of view components
    private static final int SLIDER_X = 50;
    private static final Point RED_SLIDER_LOCATION = new Point( SLIDER_X, 85 );
    private static final Point GREEN_SLIDER_LOCATION = new Point( SLIDER_X, 260 );
    private static final Point BLUE_SLIDER_LOCATION = new Point( SLIDER_X, 435 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 90, 195 );

    // Angles
    private static final double RED_SPOTLIGHT_ANGLE = 27.0;
    private static final double GREEN_SPOTLIGHT_ANGLE = 0.0;
    private static final double BLUE_SPOTLIGHT_ANGLE = -( RED_SPOTLIGHT_ANGLE );

    // Bounds
    private static final Dimension INTENSITY_SLIDER_SIZE = new Dimension( 35, 125 );
    private static final Rectangle BEAM_BOUNDS = new Rectangle( (int) SPOTLIGHT_X, (int) RED_SPOTLIGHT_Y, (int) ( PERSON_X - SPOTLIGHT_X + 100 ), (int) ( BLUE_SPOTLIGHT_Y - RED_SPOTLIGHT_Y ) );

    // Limits
    private static final int MAX_PHOTONS = 15;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Models
    private Person _personModel;
    private Spotlight _redSpotlightModel, _blueSpotlightModel, _greenSpotlightModel;
    private PhotonBeam _redPhotonBeamModel, _greenPhotonBeamModel, _bluePhotonBeamModel;

    // Views
    private ColorIntensitySlider _redSlider, _greenSlider, _blueSlider;

    // Help
    private WiggleMe _wiggleMe;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

//    //This override can make the apparatus panel come up at the right size even for resolutions less than 1024 x 768, see #2860
//    @Override public void setApparatusPanel( final ApparatusPanel apparatusPanel ) {
//        super.setApparatusPanel( apparatusPanel );
//
//        //Set the preferred size of the apparatus panel or it never renders
//        final Dimension size = new Dimension( 1024,630);
//        apparatusPanel.setPreferredSize( size );
//
//        //Use a PhETPCanvas with a PSwing around the apparatusPanel to handle the resizing
//        setSimulationPanel( new PhetPCanvas() {{
//            setWorldTransformStrategy( new CenteredStage( this, size ) );
//            addWorldChild( new PSwing( apparatusPanel ) );
//        }} );
//
//        //Wire up to make repaints happen whenever the clock ticks
//        if (apparatusPanel instanceof ApparatusPanel2){
//            ApparatusPanel2 p = (ApparatusPanel2) apparatusPanel;
//            p.getClock().addClockListener( new ClockAdapter() {
//                @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
//                    super.simulationTimeChanged( clockEvent );
//                    getSimulationPanel().repaint();
//                }
//            } );
//        }
//    }

    public RgbBulbsModule() {

        super( ColorVisionStrings.RGB_BULBS_MODULE_TITLE, new SwingClock( ColorVisionConstants.CLOCK_DELAY, ColorVisionConstants.CLOCK_DT ) );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );

        // Person model
        _personModel = new Person();
        _personModel.setLocation( PERSON_X, PERSON_Y );

        // Red Spotlight model
        _redSpotlightModel = new Spotlight();
        _redSpotlightModel.setColor( VisibleColor.RED );
        _redSpotlightModel.setIntensity( Spotlight.INTENSITY_MIN );
        _redSpotlightModel.setLocation( RED_SPOTLIGHT_X, RED_SPOTLIGHT_Y );
        _redSpotlightModel.setDirection( RED_SPOTLIGHT_ANGLE );

        // Green Spotlight model
        _greenSpotlightModel = new Spotlight();
        _greenSpotlightModel.setColor( VisibleColor.GREEN );
        _greenSpotlightModel.setIntensity( Spotlight.INTENSITY_MIN );
        _greenSpotlightModel.setLocation( GREEN_SPOTLIGHT_X, GREEN_SPOTLIGHT_Y );
        _greenSpotlightModel.setDirection( GREEN_SPOTLIGHT_ANGLE );

        // Blue Spotlight model
        _blueSpotlightModel = new Spotlight();
        _blueSpotlightModel.setColor( VisibleColor.BLUE );
        _blueSpotlightModel.setIntensity( Spotlight.INTENSITY_MIN );
        _blueSpotlightModel.setLocation( BLUE_SPOTLIGHT_X, BLUE_SPOTLIGHT_Y );
        _blueSpotlightModel.setDirection( BLUE_SPOTLIGHT_ANGLE );

        // Red photon beam model
        _redPhotonBeamModel = new PhotonBeam( _redSpotlightModel );
        _redPhotonBeamModel.setMaxPhotons( MAX_PHOTONS );
        _redPhotonBeamModel.setBounds( BEAM_BOUNDS );
        model.addModelElement( _redPhotonBeamModel );

        // Green photon beam model
        _greenPhotonBeamModel = new PhotonBeam( _greenSpotlightModel );
        _greenPhotonBeamModel.setMaxPhotons( MAX_PHOTONS );
        _greenPhotonBeamModel.setBounds( BEAM_BOUNDS );
        model.addModelElement( _greenPhotonBeamModel );

        // Blue photon beam model
        _bluePhotonBeamModel = new PhotonBeam( _blueSpotlightModel );
        _bluePhotonBeamModel.setMaxPhotons( MAX_PHOTONS );
        _bluePhotonBeamModel.setBounds( BEAM_BOUNDS );
        model.addModelElement( _bluePhotonBeamModel );

        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Control Panel
        this.setControlPanel( new RgbBulbsControlPanel() );

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( getClock() );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );

        // Person graphic
        PersonGraphic personGraphic = new PersonGraphic( apparatusPanel, PERSON_BACKGROUND_LAYER, PERSON_FOREGROUND_LAYER, _personModel );
        // Do not call apparatusPanel.addGraphic!

        // Red spotlight graphic
        SpotlightGraphic redSpotlightGraphic = new SpotlightGraphic( apparatusPanel, _redSpotlightModel );
        apparatusPanel.addGraphic( redSpotlightGraphic, RED_SPOTLIGHT_LAYER );

        // Green spotlight graphic
        SpotlightGraphic greenSpotlightGraphic = new SpotlightGraphic( apparatusPanel, _greenSpotlightModel );
        apparatusPanel.addGraphic( greenSpotlightGraphic, GREEN_SPOTLIGHT_LAYER );

        // Blue spotlight graphic
        SpotlightGraphic blueSpotlightGraphic = new SpotlightGraphic( apparatusPanel, _blueSpotlightModel );
        apparatusPanel.addGraphic( blueSpotlightGraphic, BLUE_SPOTLIGHT_LAYER );

        // Red photon beam graphic
        PhotonBeamGraphic redPhotonBeamGraphic = new PhotonBeamGraphic( apparatusPanel, _redPhotonBeamModel );
        apparatusPanel.addGraphic( redPhotonBeamGraphic, RED_BEAM_LAYER );

        // Green photon beam graphic
        PhotonBeamGraphic greenPhotonBeamGraphic = new PhotonBeamGraphic( apparatusPanel, _greenPhotonBeamModel );
        apparatusPanel.addGraphic( greenPhotonBeamGraphic, GREEN_BEAM_LAYER );

        // Blue photon beam graphic
        PhotonBeamGraphic bluePhotonBeamGraphic = new PhotonBeamGraphic( apparatusPanel, _bluePhotonBeamModel );
        apparatusPanel.addGraphic( bluePhotonBeamGraphic, BLUE_BEAM_LAYER );

        // Red intensity control
        _redSlider = new ColorIntensitySlider( apparatusPanel, VisibleColor.RED, INTENSITY_SLIDER_SIZE );
        _redSlider.setLocation( RED_SLIDER_LOCATION );
        apparatusPanel.addGraphic( _redSlider, RED_SPOTLIGHT_LAYER );

        // Green intensity control
        _greenSlider = new ColorIntensitySlider( apparatusPanel, VisibleColor.GREEN, INTENSITY_SLIDER_SIZE );
        _greenSlider.setLocation( GREEN_SLIDER_LOCATION );
        apparatusPanel.addGraphic( _greenSlider, GREEN_SPOTLIGHT_LAYER );

        // Blue intensity control
        _blueSlider = new ColorIntensitySlider( apparatusPanel, VisibleColor.BLUE, INTENSITY_SLIDER_SIZE );
        _blueSlider.setLocation( BLUE_SLIDER_LOCATION );
        apparatusPanel.addGraphic( _blueSlider, BLUE_SPOTLIGHT_LAYER );

        //----------------------------------------------------------------------------
        // Observers
        //----------------------------------------------------------------------------

        // Models notify their associated views of any updates.

        _personModel.addObserver( personGraphic );

        _redSpotlightModel.addObserver( redSpotlightGraphic );
        _redSpotlightModel.addObserver( _redPhotonBeamModel );

        _greenSpotlightModel.addObserver( greenSpotlightGraphic );
        _greenSpotlightModel.addObserver( _greenPhotonBeamModel );

        _blueSpotlightModel.addObserver( blueSpotlightGraphic );
        _blueSpotlightModel.addObserver( _bluePhotonBeamModel );

        _redPhotonBeamModel.addObserver( redPhotonBeamGraphic );
        _greenPhotonBeamModel.addObserver( greenPhotonBeamGraphic );
        _bluePhotonBeamModel.addObserver( bluePhotonBeamGraphic );

        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------

        // Photon beams notify when their perceived intensity changes.
        _redPhotonBeamModel.addColorChangeListener( this );
        _greenPhotonBeamModel.addColorChangeListener( this );
        _bluePhotonBeamModel.addColorChangeListener( this );

        // Slider notify when they are moved.
        _redSlider.addChangeListener( this );
        _greenSlider.addChangeListener( this );
        _blueSlider.addChangeListener( this );

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------

        // Wiggle Me for sliders
        _wiggleMe = new IntensitySliderWiggleMe( apparatusPanel, model );
        _wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        _wiggleMe.start();
        apparatusPanel.addGraphic( _wiggleMe, WIGGLE_ME_LAYER );

        // This module has no Help.
        super.setHelpEnabled( false );

    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Handles a VisibleColorChangeEvent, received when a photon hits the
     * person.
     *
     * @param event the event
     */
    public void colorChanged( VisibleColorChangeEvent event ) {

        //System.out.println( "colorChanged " + event ); // DEBUG

        // Ignore the color information in the event, and consult the photon
        // beams. Each beam contributes one color component.
        int red = (int) ( ( _redPhotonBeamModel.getPerceivedIntensity() / 100 ) * 255 );
        int green = (int) ( ( _greenPhotonBeamModel.getPerceivedIntensity() / 100 ) * 255 );
        int blue = (int) ( ( _bluePhotonBeamModel.getPerceivedIntensity() / 100 ) * 255 );

        // Opaque.
        int alpha = 255;

        // Update the color perceived by the person.
        VisibleColor perceivedColor = new VisibleColor( red, green, blue, alpha );
        _personModel.setColor( perceivedColor );
    }

    /**
     * Handles a ChangeEvent, received when a slider is moved.
     *
     * @param event the event
     */
    public void stateChanged( ChangeEvent event ) {

        //System.out.println( "stateChanged " + event ); // DEBUG

        // Disable the wiggle-me when a slider is moved.
        if ( _wiggleMe.isRunning() ) {
            _wiggleMe.stop();
            _wiggleMe.setVisible( false );
        }

        if ( event.getSource() == _redSlider ) {
            _redSpotlightModel.setIntensity( _redSlider.getValue() );
        }
        else if ( event.getSource() == _greenSlider ) {
            _greenSpotlightModel.setIntensity( _greenSlider.getValue() );
        }
        else if ( event.getSource() == _blueSlider ) {
            _blueSpotlightModel.setIntensity( _blueSlider.getValue() );
        }
        else {
            throw new IllegalArgumentException( "unexpected ChangeEvent from " + event.getSource() );
        }
    }

}