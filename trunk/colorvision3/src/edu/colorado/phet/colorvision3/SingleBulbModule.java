/* SingleBulbModule.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.colorvision3.event.ColorChangeEvent;
import edu.colorado.phet.colorvision3.event.ColorChangeListener;
import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.colorvision3.model.Person;
import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.colorvision3.model.VisibleColor;
import edu.colorado.phet.colorvision3.view.ColumnarBeamGraphic;
import edu.colorado.phet.colorvision3.view.FilterGraphic;
import edu.colorado.phet.colorvision3.view.PersonGraphic;
import edu.colorado.phet.colorvision3.view.PhotonBeamGraphic;
import edu.colorado.phet.colorvision3.view.PipeGraphic;
import edu.colorado.phet.colorvision3.view.SingleBulbControlPanel;
import edu.colorado.phet.colorvision3.view.SpectrumSlider;
import edu.colorado.phet.colorvision3.view.SpotlightGraphic;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * SingleBulbModule implements the simulation module that demonstrates how color vision
 * works in the context of a single light bulb and a filter. The light bulb may be 
 * white or monochromatic.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class SingleBulbModule extends Module implements ChangeListener, ColorChangeListener
{
	//----------------------------------------------------------------------------
	// Class data
  //----------------------------------------------------------------------------

	// Rendering layers
  private static final double PERSON_BACKGROUND_LAYER = 1;
  private static final double WAVELENGTH_PIPE_LAYER = 2;
  private static final double WAVELENGTH_SLIDER_LAYER = 3;
  private static final double FILTER_PIPE_LAYER = 4;
  private static final double FILTER_SLIDER_LAYER = 5;
  private static final double POST_FILTER_BEAM_LAYER = 6;
  private static final double FILTER_LAYER = 7;
  private static final double PRE_FILTER_BEAM_LAYER = 8;
  private static final double PHOTON_BEAM_LAYER = 9;
  private static final double SPOTLIGHT_LAYER = 10;
  private static final double PERSON_FOREGROUND_LAYER = 11;
  private static final double HELP_LAYER = Double.MAX_VALUE;

  // Colors
  private static Color APPARATUS_BACKGROUND = Color.black;
    
	// Locations (screen coordinates, relative to upper left)
	private static final int PERSON_X             = 400;
	private static final int PERSON_Y             =  25;
	private static final int SPOTLIGHT_X          = 120;
	private static final int SPOTLIGHT_Y          = 325;
	private static final int FILTER_X             = 337;
	private static final int FILTER_Y             = 250;
	private static final int FILTER_SLIDER_X      = 100;
	private static final int FILTER_SLIDER_Y      = 515;
	private static final int FILTER_PIPE_X        = 250;
	private static final int FILTER_PIPE_Y        = 415;
	private static final int WAVELENGTH_SLIDER_X  = 100;
	private static final int WAVELENGTH_SLIDER_Y  = 100;
	private static final int WAVELENGTH_PIPE_X    =  50;
	private static final int WAVELENGTH_PIPE_Y    = 112;
	
  //Angles
	private static final double SPOTLIGHT_ANGLE   = 0.0;
	
	// Lengths 
	private static final int PRE_FILTER_BEAM_DISTANCE = 230;
	private static final int POST_FILTER_BEAM_DISTANCE = 470;
	
	// Bounds
	private static final Rectangle PHOTON_BEAM_BOUNDS = new Rectangle( 0, 0, PERSON_X + 160, 10000 );
	
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------

	// Models 
	private Person _personModel;
	private Spotlight _spotlightModel;
	private Filter _filterModel;
	
	// Controls
	private SingleBulbControlPanel _controlPanel;
	private SpectrumSlider _filterSlider;
	private SpectrumSlider _wavelengthSlider;
	
	// Graphics that require control
	private FilterGraphic _filterGraphic;
	private PipeGraphic _filterPipe;
	private PipeGraphic _wavelengthPipe;
	private ColumnarBeamGraphic _preFilterBeam;
	private ColumnarBeamGraphic _postFilterBeam;
	private PhotonBeamGraphic _photonBeam;

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
		// Model
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
		_spotlightModel.setIntensity( 50 ); // to limit number of photons
		
		// Filter model
		_filterModel = new Filter();
    _filterModel.setLocation( FILTER_X, FILTER_Y );
		
		//----------------------------------------------------------------------------
		// View
    //----------------------------------------------------------------------------

		// Control Panel
	  _controlPanel = new SingleBulbControlPanel( this );
		this.setControlPanel( _controlPanel );
		
		// Apparatus Panel
		ApparatusPanel apparatusPanel = new ApparatusPanel();
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
    
    // Pre-filter columnar beam
    _preFilterBeam = new ColumnarBeamGraphic( apparatusPanel, _spotlightModel );
    _preFilterBeam.setDistance( PRE_FILTER_BEAM_DISTANCE );
    _preFilterBeam.setAlphaScale( 90 );
    apparatusPanel.addGraphic( _preFilterBeam, PRE_FILTER_BEAM_LAYER );
    
    // Post-filter columnar beam
    _postFilterBeam = new ColumnarBeamGraphic( apparatusPanel, _spotlightModel, _filterModel );
    _postFilterBeam.setDistance( POST_FILTER_BEAM_DISTANCE );
    _postFilterBeam.setAlphaScale( 60 );
    apparatusPanel.addGraphic( _postFilterBeam, POST_FILTER_BEAM_LAYER );
    
    // Photon beam
    _photonBeam = new PhotonBeamGraphic( apparatusPanel, _spotlightModel, _filterModel );
    _photonBeam.setBounds( PHOTON_BEAM_BOUNDS );
    apparatusPanel.addGraphic( _photonBeam, PHOTON_BEAM_LAYER );
    
    // Filter slider
    _filterSlider = new SpectrumSlider( apparatusPanel );
    _filterSlider.setLocation( FILTER_SLIDER_X, FILTER_SLIDER_Y );
    _filterSlider.setMinimum( (int) VisibleColor.MIN_WAVELENGTH );
    _filterSlider.setMaximum( (int) VisibleColor.MAX_WAVELENGTH );
    _filterSlider.setValue( (int) VisibleColor.MIN_WAVELENGTH );
    _filterSlider.setLabel( SimStrings.get("filterSlider.label") );
    _filterSlider.setTransmissionWidth( _filterModel.getTransmissionWidth()/2 );
    apparatusPanel.addGraphic( _filterSlider, FILTER_SLIDER_LAYER );
    
    // Pipe connecting filter control to filter.
    _filterPipe = new PipeGraphic( apparatusPanel );
    _filterPipe.setThickness( 5 );
    _filterPipe.addSegment( PipeGraphic.HORIZONTAL, 0, 110, 105 );
    _filterPipe.addSegment( PipeGraphic.VERTICAL, 100,   0, 115 );
    _filterPipe.setPosition( FILTER_PIPE_X, FILTER_PIPE_Y );
    apparatusPanel.addGraphic( _filterPipe, FILTER_PIPE_LAYER );
    
    // Wavelength slider
    _wavelengthSlider = new SpectrumSlider( apparatusPanel );
    _wavelengthSlider.setLocation( WAVELENGTH_SLIDER_X, WAVELENGTH_SLIDER_Y );
    _wavelengthSlider.setMinimum( (int) VisibleColor.MIN_WAVELENGTH );
    _wavelengthSlider.setMaximum( (int) VisibleColor.MAX_WAVELENGTH );
    _wavelengthSlider.setValue( (int) VisibleColor.MIN_WAVELENGTH );
    _wavelengthSlider.setLabel( SimStrings.get("wavelengthSlider.label") );
    apparatusPanel.addGraphic( _wavelengthSlider, WAVELENGTH_SLIDER_LAYER );
    
    // Pipe connecting wavelength control to spotlight. 
    _wavelengthPipe = new PipeGraphic( apparatusPanel );
    _wavelengthPipe.setThickness( 5 );
    _wavelengthPipe.addSegment( PipeGraphic.HORIZONTAL, 0, 0, 100 );
    _wavelengthPipe.addSegment( PipeGraphic.VERTICAL,   0, 0, 215 );
    _wavelengthPipe.addSegment( PipeGraphic.HORIZONTAL, 0, 210, 100 );
    _wavelengthPipe.setPosition( WAVELENGTH_PIPE_X, WAVELENGTH_PIPE_Y );
    apparatusPanel.addGraphic( _wavelengthPipe, WAVELENGTH_PIPE_LAYER );
    
		//----------------------------------------------------------------------------
		// Observers
    //----------------------------------------------------------------------------
		
    // Models notify their associated views of any updates.
    _personModel.addObserver( personGraphic );
    
    _spotlightModel.addObserver( spotlightGraphic );
    _spotlightModel.addObserver( _preFilterBeam );
    _spotlightModel.addObserver( _postFilterBeam );
    _spotlightModel.addObserver( _photonBeam );
    
    _filterModel.addObserver( _filterGraphic );
    _filterModel.addObserver( _postFilterBeam );
    _filterModel.addObserver( _photonBeam );

		//----------------------------------------------------------------------------
		// Listeners
    //----------------------------------------------------------------------------
		
    _controlPanel.addChangeListener( this );
    _filterSlider.addChangeListener( this );
    _wavelengthSlider.addChangeListener( this );
    
    _photonBeam.addColorChangeListener( this );
    _postFilterBeam.addColorChangeListener( this );
    
    clock.addClockTickListener( _photonBeam );
    
		//----------------------------------------------------------------------------
		// Help
    //----------------------------------------------------------------------------

		// This module has no Help.
		super.setHelpEnabled( false );

		//----------------------------------------------------------------------------
		// Initial state
    //----------------------------------------------------------------------------

		_controlPanel.setBulbType( SingleBulbControlPanel.WHITE_BULB );
		_controlPanel.setBeamType( SingleBulbControlPanel.SOLID_BEAM );
		_controlPanel.setFilterEnabled( true );
		_filterSlider.setValue( (int) VisibleColor.MIN_WAVELENGTH );
		_wavelengthSlider.setValue( (int) VisibleColor.MIN_WAVELENGTH );
		
	} // constructor
	
	//----------------------------------------------------------------------------
	// Event handling
  //----------------------------------------------------------------------------
	
  /**
   * Handles a ColorChangeEvent, which occurs when the perceived color changes.
   * 
   * @param event the event
   * @throws IllegalArgumentException if the event is from an unexpected source
   */
  public void colorChanged( ColorChangeEvent event )
  {
    if ( event.getSource() == _photonBeam || event.getSource() == _postFilterBeam )
    {
      _personModel.setColor( event.getColor() );
    }
    else
    {
      throw new IllegalArgumentException( "unexpected ColorChangeEvent from " + event.getSource() );
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
    if (event.getSource() == _filterSlider )
    {
      // The filter slider was moved.
      double filterWavelength = _filterSlider.getValue();
      _filterModel.setTransmissionPeak( filterWavelength );
    }
    else if ( event.getSource() == _wavelengthSlider )
    {
      // The wavelength slider was moved.
      VisibleColor bulbColor = VisibleColor.WHITE;
      int bulbType = _controlPanel.getBulbType();
      if ( bulbType == SingleBulbControlPanel.MONOCHROMATIC_BULB )
      {
        bulbColor = new VisibleColor( _wavelengthSlider.getValue() );
      }
      _spotlightModel.setColor( bulbColor );
    }
    else if ( event.getSource() == _controlPanel )
    {
      // A control panel change was made.
      
      // Get current control panel settings.
      int bulbType = _controlPanel.getBulbType();
      int beamType = _controlPanel.getBeamType();
      boolean filterEnabled = _controlPanel.getFilterEnabled();
      
      // Bulb Type
      if ( bulbType == SingleBulbControlPanel.WHITE_BULB )
      {
        _wavelengthSlider.setVisible( false );
        _wavelengthPipe.setVisible( false );
        _spotlightModel.setColor( VisibleColor.WHITE );
      }
      else
      {
        _wavelengthSlider.setVisible( true );
        _wavelengthPipe.setVisible( true );
        double wavelength = _wavelengthSlider.getValue();
        VisibleColor bulbColor = new VisibleColor( wavelength );
        _spotlightModel.setColor( bulbColor );
      }
   
      // Beam Type
      if ( beamType == SingleBulbControlPanel.SOLID_BEAM )
      {
        _preFilterBeam.setVisible( filterEnabled );
        _postFilterBeam.setVisible( true );
        _photonBeam.setVisible( false );
        // NOTE: Do *not* stop the photon beam; keep it running so
        // that it is in the proper state when it is made visible.
      }
      else
      {
        _preFilterBeam.setVisible( false );
        _postFilterBeam.setVisible( false );
        _photonBeam.setVisible( true );
      }
      
      // Filter enable
      _filterModel.setEnabled( filterEnabled );
      _filterSlider.setVisible( filterEnabled );
      _filterPipe.setVisible( filterEnabled );
      _filterGraphic.setVisible( filterEnabled );   
    }
    else
    {
      throw new IllegalArgumentException( "unexpected ChangeEvent from " + event.getSource() );
    }
  }
  

  
//  /**
//   * Updates all models and views.  The performance penalty for updating
//   * everything is negligible, and there the code is simplified.  This
//   * should result in easier debugging and maintenance.
//   */
//  public void update()
//  {
//    // Get current state information.
//    int bulbType = _controlPanel.getBulbType();
//    int beamType = _controlPanel.getBeamType();
//    boolean filterEnabled = _controlPanel.getFilterEnabled();
//    
//    // Update all model components.
//    updateModels( bulbType, beamType, filterEnabled );
//    
//    // Update all view components.
//    updateViews( bulbType, beamType, filterEnabled );
//  }
//  
//  /**
//   * Updates all models.
//   * 
//   * @param bulbType the bulb type
//   * @param beamType the beam type
//   * @param filterEnabled whether the filter is enabled
//   */
//  private void updateModels( int bulbType, int beamType, boolean filterEnabled )
//  {
//    // Spotlight
//    VisibleColor bulbColor = VisibleColor.WHITE;
//    if ( bulbType == SingleBulbControlPanel.MONOCHROMATIC_BULB )
//    {
//      bulbColor = new VisibleColor( _wavelengthSlider.getValue() );
//    }
//    _spotlightModel.setColor( bulbColor );
//         
//    // Filter
//    _filterModel.setEnabled( filterEnabled );
//    double filterWavelength = _filterSlider.getValue();
//    _filterModel.setTransmissionPeak( filterWavelength );
//    
//    // Person
//    VisibleColor perceivedColor = bulbColor;
//    if ( beamType == SingleBulbControlPanel.PHOTON_BEAM )
//    {
//      perceivedColor = _photonBeam.getPerceivedColor();
//    }
//    else
//    {
//      perceivedColor = _postFilterBeam.getPerceivedColor();
//    }
//    _personModel.setColor( perceivedColor );
//    
//  } // updateModels
//  
//  /**
//   * Updates all views.
//   * 
//   * @param bulbType the bulb type
//   * @param beamType the beam type
//   * @param filterEnabled whether the filter is enabled
//   */
//  private void updateViews( int bulbType, int beamType, boolean filterEnabled )
//  {  
//    // Wavelength control
//    if ( bulbType == SingleBulbControlPanel.WHITE_BULB )
//    {
//      _wavelengthSlider.setVisible( false );
//      _wavelengthPipe.setVisible( false );
//    }
//    else
//    {
//      _wavelengthSlider.setVisible( true );
//      _wavelengthPipe.setVisible( true );
//    }
// 
//    // Filter & filter control
//    _filterSlider.setVisible( filterEnabled );
//    _filterPipe.setVisible( filterEnabled );
//    _filterGraphic.setVisible( filterEnabled );
//    
//    // Beams
//    if ( beamType == SingleBulbControlPanel.SOLID_BEAM )
//    {
//      _preFilterBeam.setVisible( filterEnabled );
//      _postFilterBeam.setVisible( true );
//      _photonBeam.setVisible( false );
//      // NOTE: Do *not* stop the photon beam, keep it running so that 
//      // it's in the proper state when it is made visible.
//    }
//    else
//    {
//      _preFilterBeam.setVisible( false );
//      _postFilterBeam.setVisible( false );
//      _photonBeam.setVisible( true );
//    }  
//  } // updateViews
 
}

/* end of file */