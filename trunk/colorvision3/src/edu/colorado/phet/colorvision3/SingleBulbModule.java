/* SingleBulbModule.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.colorvision3.control.SingleBulbControlPanel;
import edu.colorado.phet.colorvision3.control.SpectrumSlider;
import edu.colorado.phet.colorvision3.control.ToggleSwitch;
import edu.colorado.phet.colorvision3.event.VisibleColorChangeEvent;
import edu.colorado.phet.colorvision3.event.VisibleColorChangeListener;
import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.colorvision3.model.Person;
import edu.colorado.phet.colorvision3.model.PhotonBeam;
import edu.colorado.phet.colorvision3.model.SolidBeam;
import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.colorvision3.view.FilterGraphic;
import edu.colorado.phet.colorvision3.view.FilterHolderGraphic;
import edu.colorado.phet.colorvision3.view.PersonGraphic;
import edu.colorado.phet.colorvision3.view.PhotonBeamGraphic;
import edu.colorado.phet.colorvision3.view.PipeGraphic;
import edu.colorado.phet.colorvision3.view.SolidBeamGraphic;
import edu.colorado.phet.colorvision3.view.SpotlightGraphic;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * SingleBulbModule implements the simulation module that demonstrates how color vision
 * works in the context of a single light bulb and a filter. The light bulb may be 
 * white or monochromatic.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$ $Name$
 */
public class SingleBulbModule extends Module implements ChangeListener, VisibleColorChangeListener
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

	// Rendering layers
  private static final double PERSON_BACKGROUND_LAYER = 1;
  private static final double BULB_PIPE_LAYER = 2;
  private static final double BULB_SLIDER_LAYER = 3;
  private static final double FILTER_SWITCH_LAYER = 4;
  private static final double FILTER_PIPE_LAYER = 5;
  private static final double FILTER_SLIDER_LAYER = 6;
  private static final double POST_FILTER_BEAM_LAYER = 7;
  private static final double FILTER_HOLDER_LAYER = 8;
  private static final double FILTER_LAYER = 9;
  private static final double PRE_FILTER_BEAM_LAYER = 10;
  private static final double PHOTON_BEAM_LAYER = 11;
  private static final double SPOTLIGHT_LAYER = 12;
  private static final double PERSON_FOREGROUND_LAYER = 13;
  private static final double BULB_SLIDER_LABEL_LAYER = 14;
  private static final double FILTER_SLIDER_LABEL_LAYER = 15;
  private static final double FILTER_SWITCH_LABEL_LAYER = 16;
  private static final double HELP_LAYER = Double.MAX_VALUE;

  // Colors
  private static Color APPARATUS_BACKGROUND = ColorVisionConfig.APPARATUS_BACKGROUND;
  private static Color LABEL_COLOR = ColorVisionConfig.LABEL_COLOR;
    
  // Fonts
  private static Font LABEL_FONT = ColorVisionConfig.LABEL_FONT;
  
	// Locations of model components
	private static final double PERSON_X      = 450;
	private static final double PERSON_Y      =  25;
	private static final double SPOTLIGHT_X   = 120;
	private static final double SPOTLIGHT_Y   = 325;
	private static final double FILTER_X      = 337;
	private static final double FILTER_Y      = 250;
	
	// Locations of view components
	private static final Point FILTER_SWITCH_LOCATION       = new Point( 352, 457 );
	private static final Point FILTER_SWITCH_LABEL_LOCATION = new Point( 350, 558 );
	private static final Point FILTER_HOLDER_LOCATION       = new Point( 342, 395 );
	private static final Point FILTER_SLIDER_LOCATION       = new Point( 100, 515 );
	private static final Point FILTER_SLIDER_LABEL_LOCATION = new Point( 100, 500 );
	private static final Point FILTER_PIPE_LOCATION         = new Point( 249, 415 );
	private static final Point BULB_SLIDER_LOCATION         = new Point( 100, 100 );
	private static final Point BULB_SLIDER_LABEL_LOCATION   = new Point( 100,  85 );
	private static final Point BULB_PIPE_LOCATION           = new Point(  50, 112 );
	
  //Angles
	private static final double SPOTLIGHT_ANGLE = 0.0;
	
	// Lengths 
	private static final int PRE_FILTER_BEAM_DISTANCE = 230;
	private static final int POST_FILTER_BEAM_DISTANCE = 470;
	
	// Bounds
	private static final Rectangle PHOTON_BEAM_BOUNDS =
	  new Rectangle( (int)SPOTLIGHT_X, (int)(SPOTLIGHT_Y - 75), 
	                 (int)(PERSON_X - SPOTLIGHT_X + 100), 150 );
	
	// Limits
	private static final int MAX_PHOTONS = 15;
	
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

	// Models 
	private Person _personModel;
	private Spotlight _spotlightModel;
	private PhotonBeam _photonBeamModel;
	private SolidBeam _preFilterBeamModel, _postFilterBeamModel;
	private Filter _filterModel;
	
	// Controls
	private SingleBulbControlPanel _controlPanel;
	private SpectrumSlider _filterSlider;
	private SpectrumSlider _bulbSlider;
	private PhetTextGraphic _bulbSliderLabel;
	private ToggleSwitch _filterSwitch;
	
	// Graphics that require control
	private FilterGraphic _filterGraphic;
	private PipeGraphic _filterPipe;
	private PipeGraphic _bulbPipe;
	private SolidBeamGraphic _preFilterBeamGraphic;
	private SolidBeamGraphic _postFilterBeamGraphic;
	private PhotonBeamGraphic _photonBeamGraphic;

	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------

  /**
  	* Sole constructor.
  	* 
  	* @param appModel the application model
  	*/
	public SingleBulbModule( ApplicationModel appModel )
	{
		super( SimStrings.get("SingleBulbModule.title") );
		
		//----------------------------------------------------------------------------
		// Models
    //----------------------------------------------------------------------------
		
		// Clock
		AbstractClock clock = appModel.getClock();

		// Module model
		BaseModel model = new BaseModel();
		this.setModel( model );

		// Person model
	  _personModel = new Person();
		_personModel.setLocation( PERSON_X, PERSON_Y );
		
		// Spotlight model
		_spotlightModel = new Spotlight();
		_spotlightModel.setColor( VisibleColor.WHITE );
		_spotlightModel.setIntensity( Spotlight.INTENSITY_MAX );
		_spotlightModel.setLocation( SPOTLIGHT_X, SPOTLIGHT_Y );
		_spotlightModel.setDirection( SPOTLIGHT_ANGLE );
		_spotlightModel.setIntensity( 100 );
		
		// Filter model
		_filterModel = new Filter();
    _filterModel.setLocation( FILTER_X, FILTER_Y );
		
		// Photon beam model
		_photonBeamModel = new PhotonBeam( _spotlightModel, _filterModel );
		_photonBeamModel.setMaxPhotons( MAX_PHOTONS );
		_photonBeamModel.setBounds( PHOTON_BEAM_BOUNDS );
		model.addModelElement( _photonBeamModel );
		
    // Pre-filter columnar beam model (unfiltered)
    _preFilterBeamModel = new SolidBeam( _spotlightModel );
    _preFilterBeamModel.setDistance( PRE_FILTER_BEAM_DISTANCE );
    
    // Post-filter columnar beam model (filtered)
    _postFilterBeamModel = new SolidBeam( _spotlightModel, _filterModel );
    _postFilterBeamModel.setDistance( POST_FILTER_BEAM_DISTANCE );
		
		//----------------------------------------------------------------------------
		// Views
    //----------------------------------------------------------------------------

		// Control Panel
	  _controlPanel = new SingleBulbControlPanel( this );
		this.setControlPanel( _controlPanel );
		
		// Apparatus Panel
		ApparatusPanel2 apparatusPanel = new ApparatusPanel2( model );
		apparatusPanel.setBackground( APPARATUS_BACKGROUND );
		this.setApparatusPanel( apparatusPanel );
		
		// Person graphic
		PersonGraphic personGraphic = 
		  new PersonGraphic( apparatusPanel, PERSON_BACKGROUND_LAYER, PERSON_FOREGROUND_LAYER, _personModel );
		// Do not call apparatusPanel.addGraphic!
    
    // Spotlight graphic
    SpotlightGraphic spotlightGraphic = new SpotlightGraphic( apparatusPanel, _spotlightModel );
    apparatusPanel.addGraphic( spotlightGraphic, SPOTLIGHT_LAYER );
    
    // Filter
    _filterGraphic = new FilterGraphic( apparatusPanel, _filterModel );
    apparatusPanel.addGraphic( _filterGraphic, FILTER_LAYER );
    
    // Filter holder
    FilterHolderGraphic filterHolder = new FilterHolderGraphic( apparatusPanel );
    filterHolder.setLocation( FILTER_HOLDER_LOCATION );
    apparatusPanel.addGraphic( filterHolder, FILTER_HOLDER_LAYER );
    
    // Pre-filter columnar beam
    _preFilterBeamGraphic = new SolidBeamGraphic( apparatusPanel, _preFilterBeamModel );
    _preFilterBeamGraphic.setAlphaScale( 95 /* percent */ );
    apparatusPanel.addGraphic( _preFilterBeamGraphic, PRE_FILTER_BEAM_LAYER );
    
    // Post-filter columnar beam
    _postFilterBeamGraphic = new SolidBeamGraphic( apparatusPanel, _postFilterBeamModel );
    _postFilterBeamGraphic.setAlphaScale( 100 /* percent */ );
    apparatusPanel.addGraphic( _postFilterBeamGraphic, POST_FILTER_BEAM_LAYER );
    
    // Photon beam
    _photonBeamGraphic = new PhotonBeamGraphic( apparatusPanel, _photonBeamModel );
    apparatusPanel.addGraphic( _photonBeamGraphic, PHOTON_BEAM_LAYER );
    
    // Filter Color slider
    _filterSlider = new SpectrumSlider( apparatusPanel );
    _filterSlider.setLocation( FILTER_SLIDER_LOCATION );
    _filterSlider.setKnobBorderColor( Color.WHITE );
    _filterSlider.setTransmissionWidth( _filterModel.getTransmissionWidth()/2 );
    apparatusPanel.addGraphic( _filterSlider, FILTER_SLIDER_LAYER );
    
    // Filter Color label
    PhetTextGraphic filterSliderLabel = new PhetTextGraphic( apparatusPanel, LABEL_FONT, 
        SimStrings.get( "filterSlider.label" ), LABEL_COLOR, 
        FILTER_SLIDER_LABEL_LOCATION.x, FILTER_SLIDER_LABEL_LOCATION.y );
    apparatusPanel.addGraphic( filterSliderLabel, FILTER_SLIDER_LABEL_LAYER );  
    
    // Bulb Color slider
    _bulbSlider = new SpectrumSlider( apparatusPanel );
    _bulbSlider.setLocation( BULB_SLIDER_LOCATION );
    _bulbSlider.setKnobBorderColor( Color.WHITE );
    apparatusPanel.addGraphic( _bulbSlider, BULB_SLIDER_LAYER );
 
    // Bulb Color label
    _bulbSliderLabel = new PhetTextGraphic( apparatusPanel, LABEL_FONT, 
        SimStrings.get( "bulbSlider.label" ), 
        LABEL_COLOR, BULB_SLIDER_LABEL_LOCATION.x, BULB_SLIDER_LABEL_LOCATION.y );
    apparatusPanel.addGraphic( _bulbSliderLabel, BULB_SLIDER_LABEL_LAYER );

    // Pipe connecting filter control to filter.
    _filterPipe = new PipeGraphic( apparatusPanel );
    _filterPipe.setThickness( 6 );
    _filterPipe.addSegment( PipeGraphic.HORIZONTAL, 0, 110, 105 );
    _filterPipe.addSegment( PipeGraphic.VERTICAL, 100,   0, 115 );
    _filterPipe.setLocation( FILTER_PIPE_LOCATION );
    apparatusPanel.addGraphic( _filterPipe, FILTER_PIPE_LAYER );
    
    // Pipe connecting wavelength control to spotlight. 
    _bulbPipe = new PipeGraphic( apparatusPanel );
    _bulbPipe.setThickness( 6 );
    _bulbPipe.addSegment( PipeGraphic.HORIZONTAL, 0, 0, 100 );
    _bulbPipe.addSegment( PipeGraphic.VERTICAL,   0, 0, 215 );
    _bulbPipe.addSegment( PipeGraphic.HORIZONTAL, 0, 210, 100 );
    _bulbPipe.setLocation( BULB_PIPE_LOCATION );
    apparatusPanel.addGraphic( _bulbPipe, BULB_PIPE_LAYER );

    // Filter on/off switch
    _filterSwitch = new ToggleSwitch( apparatusPanel, ColorVisionConfig.SWITCH_ON_IMAGE, ColorVisionConfig.SWITCH_OFF_IMAGE  );
    _filterSwitch.setLocation( FILTER_SWITCH_LOCATION );
    apparatusPanel.addGraphic( _filterSwitch, FILTER_SWITCH_LAYER );
    
    // Filter switch label
    PhetTextGraphic filterSwitchLabel = new PhetTextGraphic( apparatusPanel, LABEL_FONT, 
        SimStrings.get( "filterSwitch.label" ), LABEL_COLOR,
        FILTER_SWITCH_LABEL_LOCATION.x, FILTER_SWITCH_LABEL_LOCATION.y );
    apparatusPanel.addGraphic( filterSwitchLabel, FILTER_SWITCH_LABEL_LAYER );
    
		//----------------------------------------------------------------------------
		// Observers
    //----------------------------------------------------------------------------
		
    // Models notify their associated views (and other models) of any updates.
    
    _personModel.addObserver( personGraphic );
    
    _spotlightModel.addObserver( spotlightGraphic );
    _spotlightModel.addObserver( _photonBeamModel );
    _spotlightModel.addObserver( _preFilterBeamModel );
    _spotlightModel.addObserver( _postFilterBeamModel );
    
    _filterModel.addObserver( _filterGraphic );
    _filterModel.addObserver( _photonBeamModel );
    _filterModel.addObserver( _postFilterBeamModel );
    
    _photonBeamModel.addObserver( _photonBeamGraphic ); 
    _preFilterBeamModel.addObserver( _preFilterBeamGraphic ); 
    _postFilterBeamModel.addObserver( _postFilterBeamGraphic );

		//----------------------------------------------------------------------------
		// Listeners
    //----------------------------------------------------------------------------
		
    _controlPanel.addChangeListener( this );
    _filterSlider.addChangeListener( this );
    _bulbSlider.addChangeListener( this );
    _filterSwitch.addChangeListener( this );
    
    _photonBeamModel.addColorChangeListener( this );
    _postFilterBeamModel.addColorChangeListener( this );
    
		//----------------------------------------------------------------------------
		// Help
    //----------------------------------------------------------------------------

		// This module has no Help.
		super.setHelpEnabled( false );

		//----------------------------------------------------------------------------
		// Initial state
    //----------------------------------------------------------------------------

		_controlPanel.setBulbType( SingleBulbControlPanel.WHITE_BULB );
		_controlPanel.setBeamType( SingleBulbControlPanel.PHOTON_BEAM );
		double wavelength = ((VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH)/2) + VisibleColor.MIN_WAVELENGTH;
		_filterSlider.setValue( (int)wavelength );
		_bulbSlider.setValue( (int)wavelength );
		_filterSwitch.setOn( true );
		
	} // constructor
	
	//----------------------------------------------------------------------------
	// Event handling
  //----------------------------------------------------------------------------
	
  /**
   * Handles a VisibleColorChangeEvent, which occurs when the perceived color changes.
   * 
   * @param event the event
   * @throws IllegalArgumentException if the event is from an unexpected source
   */
  public void colorChanged( VisibleColorChangeEvent event )
  {
    //System.out.println( "colorChanged " + event ); // DEBUG
    
    if ( event.getSource() == _photonBeamModel || event.getSource() == _postFilterBeamModel )
    {
      _personModel.setColor( event.getColor() );
    }
    else
    {
      throw new IllegalArgumentException( "unexpected VisibleColorChangeEvent from " + event.getSource() );
    }
  }
  
  /**
   * Handles a ChangeEvent, which occurs when one of the UI controls is changed.
   * 
   * @param event the event
   * @throws IllegalArgumentException if the event is from an unexpected source
   */
  public void stateChanged( ChangeEvent event )
  {
    //System.out.println( "stateChanged " + event ); // DEBUG
    
    if (event.getSource() == _filterSlider )
    {
      // The filter slider was moved.
      double wavelength = _filterSlider.getValue();
      _filterModel.setTransmissionPeak( wavelength );
    }
    else if ( event.getSource() == _bulbSlider )
    {
      // The wavelength slider was moved.
      VisibleColor bulbColor = VisibleColor.WHITE;
      int bulbType = _controlPanel.getBulbType();
      if ( bulbType == SingleBulbControlPanel.MONOCHROMATIC_BULB )
      {
        bulbColor = new VisibleColor( _bulbSlider.getValue() );
      }
      _spotlightModel.setColor( bulbColor );
    }
    else if ( event.getSource() == _filterSwitch )
    {
      _filterModel.setEnabled( _filterSwitch.isOn() );
    }
    else if ( event.getSource() == _controlPanel )
    {
      // A control panel change was made.
      
      // Get current control panel settings.
      int bulbType = _controlPanel.getBulbType();
      int beamType = _controlPanel.getBeamType();
      boolean filterEnabled = _filterSwitch.isOn();
      
      // Bulb Type
      if ( bulbType == SingleBulbControlPanel.WHITE_BULB )
      {
        _bulbSlider.setVisible( false );
        _bulbSliderLabel.setVisible( false );
        _bulbPipe.setVisible( false );
        _spotlightModel.setColor( VisibleColor.WHITE );
      }
      else
      {
        _bulbSlider.setVisible( true );
        _bulbSliderLabel.setVisible( true );
        _bulbPipe.setVisible( true );
        double wavelength = _bulbSlider.getValue();
        VisibleColor bulbColor = new VisibleColor( wavelength );
        _spotlightModel.setColor( bulbColor );
      }
   
      // Beam Type
      if ( beamType == SingleBulbControlPanel.PHOTON_BEAM )
      {
        _photonBeamModel.setEnabled( true );
        _preFilterBeamModel.setEnabled( false );
        _postFilterBeamModel.setEnabled( false );
      }
      else
      {
        _photonBeamModel.setEnabled( false );
        _preFilterBeamModel.setEnabled( filterEnabled );
        _postFilterBeamModel.setEnabled( true );
      }
    }
    else
    {
      throw new IllegalArgumentException( "unexpected ChangeEvent from " + event.getSource() );
    }
  } // stateChanged
  
}

/* end of file */