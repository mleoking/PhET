/* SingleBulbModule.java */

package edu.colorado.phet.colorvision3;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.colorvision3.event.IntensityChangeEvent;
import edu.colorado.phet.colorvision3.event.IntensityChangeListener;
import edu.colorado.phet.colorvision3.model.Filter;
import edu.colorado.phet.colorvision3.model.Person2D;
import edu.colorado.phet.colorvision3.model.Spotlight2D;
import edu.colorado.phet.colorvision3.util.ColorUtil;
import edu.colorado.phet.colorvision3.view.ColumnarBeamGraphic;
import edu.colorado.phet.colorvision3.view.FilterGraphic;
import edu.colorado.phet.colorvision3.view.FilterPipeGraphic;
import edu.colorado.phet.colorvision3.view.PersonGraphic;
import edu.colorado.phet.colorvision3.view.PhotonBeamGraphic;
import edu.colorado.phet.colorvision3.view.SingleBulbControlPanel;
import edu.colorado.phet.colorvision3.view.SpectrumControl;
import edu.colorado.phet.colorvision3.view.SpotlightGraphic;
import edu.colorado.phet.colorvision3.view.WavelengthPipeGraphic;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * SingleBulbModule implements the simulation module that demonstrates how color vision
 * works in the context in a single light bulb and a filter. The light bulb may be 
 * white or monochromatic.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class SingleBulbModule extends Module  implements ChangeListener, IntensityChangeListener
{
	// Rendering layers
  private static final double PERSON_BACKGROUND_LAYER = 1;
  private static final double WAVELENGTH_PIPE_LAYER = 2;
  private static final double WAVELENGTH_CONTROL_LAYER = 3;
  private static final double FILTER_PIPE_LAYER = 4;
  private static final double FILTER_CONTROL_LAYER = 5;
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
	private static final int FILTER_CONTROL_X     = 100;
	private static final int FILTER_CONTROL_Y     = 515;
	private static final int FILTER_PIPE_X        = 250;
	private static final int FILTER_PIPE_Y        = 415;
	private static final int WAVELENGTH_CONTROL_X = 100;
	private static final int WAVELENGTH_CONTROL_Y = 100;
	private static final int WAVELENGTH_PIPE_X    =  50;
	private static final int WAVELENGTH_PIPE_Y    = 112;
	
  //Angles
	private static final double SPOTLIGHT_ANGLE   = 0.0;
	
	// Lengths 
	private static final int PRE_FILTER_BEAM_DISTANCE = 230;
	private static final int POST_FILTER_BEAM_DISTANCE = 470;
	
	// Bounds
	private static final Rectangle PHOTON_BEAM_BOUNDS = new Rectangle( 0, 0, PERSON_X + 160, 10000 );
	
	// Models 
	private Person2D _personModel;
	private Spotlight2D _spotlightModel;
	private Filter _filterModel;
	
	// Controls
	SingleBulbControlPanel _controlPanel;
	SpectrumControl _filterControl;
	SpectrumControl _wavelengthControl;
	
	// Graphics that require control
	FilterGraphic _filterGraphic;
	FilterPipeGraphic _filterPipe;
	WavelengthPipeGraphic _wavelengthPipe;
	ColumnarBeamGraphic _preFilterBeam;
	ColumnarBeamGraphic _postFilterBeam;
	PhotonBeamGraphic _photonBeam;

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
	  _personModel = new Person2D();
		_personModel.setLocation( PERSON_X, PERSON_Y );
		
		// Red Spotlight model
		_spotlightModel = new Spotlight2D();
		_spotlightModel.setColor( Color.red );
		_spotlightModel.setIntensity( Spotlight2D.INTENSITY_MAX );
		_spotlightModel.setLocation( SPOTLIGHT_X, SPOTLIGHT_Y );
		_spotlightModel.setDirection( SPOTLIGHT_ANGLE );
		_spotlightModel.setIntensity( 50 ); // to limit number of photons
		
		// Filter model
		_filterModel = new Filter();
		
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
    
    // Pre-filter columnar beam
    _preFilterBeam = new ColumnarBeamGraphic( apparatusPanel, _spotlightModel );
    _preFilterBeam.setDistance( PRE_FILTER_BEAM_DISTANCE );
    _preFilterBeam.setAlphaScale( 100 );
    apparatusPanel.addGraphic( _preFilterBeam, PRE_FILTER_BEAM_LAYER );
    
    // Post-filter columnar beam
    _postFilterBeam = new ColumnarBeamGraphic( apparatusPanel, _spotlightModel );
    _postFilterBeam.setDistance( POST_FILTER_BEAM_DISTANCE );
    _postFilterBeam.setAlphaScale( 50 );
    apparatusPanel.addGraphic( _postFilterBeam, POST_FILTER_BEAM_LAYER );
    
    // Photon beam
    _photonBeam = new PhotonBeamGraphic( apparatusPanel, _spotlightModel );
    _photonBeam.setBounds( PHOTON_BEAM_BOUNDS );
    apparatusPanel.addGraphic( _photonBeam, PHOTON_BEAM_LAYER );
    
    // Filter control
    _filterControl = new SpectrumControl( apparatusPanel );
    _filterControl.setLocation( FILTER_CONTROL_X, FILTER_CONTROL_Y );
    _filterControl.setMinimum( (int) ColorUtil.MIN_WAVELENGTH ); // XXX remove cast
    _filterControl.setMaximum( (int) ColorUtil.MAX_WAVELENGTH ); // XXX remove cast
    _filterControl.setValue( (int) ColorUtil.MIN_WAVELENGTH );   // XXX remove cast
    _filterControl.setLabel( SimStrings.get("FilterControl.label") );
    apparatusPanel.addGraphic( _filterControl, FILTER_CONTROL_LAYER );
    
    // Filter pipe
    _filterPipe = new FilterPipeGraphic( apparatusPanel );
    _filterPipe.setPosition( FILTER_PIPE_X, FILTER_PIPE_Y );
    apparatusPanel.addGraphic( _filterPipe, FILTER_PIPE_LAYER );
    
    // Wavelength control
    _wavelengthControl = new SpectrumControl( apparatusPanel );
    _wavelengthControl.setLocation( WAVELENGTH_CONTROL_X, WAVELENGTH_CONTROL_Y );
    _wavelengthControl.setMinimum( (int) ColorUtil.MIN_WAVELENGTH ); // XXX remove cast
    _wavelengthControl.setMaximum( (int) ColorUtil.MAX_WAVELENGTH ); // XXX remove cast
    _wavelengthControl.setValue( (int) ColorUtil.MIN_WAVELENGTH );   // XXX remove cast
    _wavelengthControl.setLabel( SimStrings.get("WavelengthControl.label") );
    apparatusPanel.addGraphic( _wavelengthControl, WAVELENGTH_CONTROL_LAYER );
    
    // Wavelength pipe 
    _wavelengthPipe = new WavelengthPipeGraphic( apparatusPanel );
    _wavelengthPipe.setPosition( WAVELENGTH_PIPE_X, WAVELENGTH_PIPE_Y );
    apparatusPanel.addGraphic( _wavelengthPipe, WAVELENGTH_PIPE_LAYER );
    
    // Filter
    _filterGraphic = new FilterGraphic( apparatusPanel, _filterModel );
    _filterGraphic.setPosition( FILTER_X, FILTER_Y );
    apparatusPanel.addGraphic( _filterGraphic, FILTER_LAYER );
    
		//----------------------------------------------------------------------------
		// Observers
    //----------------------------------------------------------------------------
		
    // Models notify their associated views of any updates.
    _personModel.addObserver( personGraphic );   
    _spotlightModel.addObserver( spotlightGraphic );
    _filterModel.addObserver( _filterGraphic );

		//----------------------------------------------------------------------------
		// Listeners
    //----------------------------------------------------------------------------
		
    _controlPanel.addChangeListener( this );
    _filterControl.addChangeListener( this );
    _wavelengthControl.addChangeListener( this );
    
    _photonBeam.addIntensityChangeListener( this );
    
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
		_filterControl.setValue( (int) ColorUtil.MIN_WAVELENGTH );  // XXX remove cast
		_wavelengthControl.setValue( (int) ColorUtil.MIN_WAVELENGTH ); // XXX remove cast
		
		update();
	}
	
  /**
   * Handles a ChangeEvent.
   * 
   * @param event the event
   */
  public void stateChanged( ChangeEvent event )
  {
    update();
  }
  
  /**
   * Handles an IntensityChangeEvent.
   * 
   * @param event the event
   */
  public void intensityChanged( IntensityChangeEvent event )
  {
		update();
  }
  
  /**
   * Updates all model and view components based on some event.
   * Since there are relatively few parameters, it's easier to 
   * just consult and poke everything.
   */
  private void update()
  {
    // Get current state information.
    int bulbType = _controlPanel.getBulbType();
    int beamType = _controlPanel.getBeamType();
    boolean filterEnabled = _controlPanel.getFilterEnabled();
    Color bulbColor = Color.white;
    double bulbWavelength = ColorUtil.WHITE_WAVELENGTH;
    Color filterColor = Color.white;
    double filterWavelength = ColorUtil.WHITE_WAVELENGTH;

    // Update the spotlight.
    if ( bulbType == SingleBulbControlPanel.WHITE_BULB )
    {
      _wavelengthControl.setVisible( false );
      _wavelengthPipe.setVisible( false );
      bulbColor = Color.white;
      bulbWavelength = ColorUtil.WHITE_WAVELENGTH;
    }
    else
    {
      _wavelengthControl.setVisible( true );
      _wavelengthPipe.setVisible( true );
      bulbColor = _wavelengthControl.getColor();
      bulbWavelength = _wavelengthControl.getValue();
    }
    _spotlightModel.setColor( bulbColor );
       
    // Update the beam.
    if ( beamType == SingleBulbControlPanel.SOLID_BEAM )
    {
      _preFilterBeam.setVisible( filterEnabled );
      _postFilterBeam.setVisible( true );
      _photonBeam.setVisible( false );
      // NOTE: Do *not* stop the photon beam, keep it running so that 
      // it's in the proper state when it is made visible.
    }
    else
    {
      _preFilterBeam.setVisible( false );
      _postFilterBeam.setVisible( false );
      _photonBeam.setVisible( true );
    }
    
    // Update the filter.
    _filterControl.setVisible( filterEnabled );
    _filterPipe.setVisible( filterEnabled );
    _filterGraphic.setVisible( filterEnabled );
    if ( filterEnabled )
    {
      filterWavelength = _filterControl.getValue();
      _filterModel.setTransmissionPeak( filterWavelength );
    }
    
    // Calculate the perceived color.
    Color perceivedColor = bulbColor;
    if ( filterEnabled )
    {
      if ( bulbWavelength == ColorUtil.WHITE_WAVELENGTH )
      {
        // With a white bulb, the perceived color is the filter's color.
        perceivedColor = ColorUtil.wavelengthToColor( _filterModel.getTransmissionPeak() );
      }
      else
      {
        // With a colored bulb, the filter passes some percentage of the bulb color.
        // To be physically correct, we set RGBA to zero when the filter passes 0%.
        double percentPassed = _filterModel.calculatePercentPassed( bulbWavelength );
        if ( percentPassed == 0 )
        {
          perceivedColor = new Color(0,0,0,0);
        }
        else
        {
          int alpha = (int)( percentPassed / 100 * 255 );
          perceivedColor = new Color( bulbColor.getRed(), bulbColor.getGreen(), bulbColor.getBlue(), alpha );
        }
      }
    }
    
    // Update displayed colors.
    _preFilterBeam.setColor( bulbColor );
    _postFilterBeam.setColor( perceivedColor );
    _personModel.setColor( perceivedColor );
  }
  
}

/* end of file */