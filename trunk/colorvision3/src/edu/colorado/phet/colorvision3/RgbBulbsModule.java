/* RgbBulbModule.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.colorvision3.event.ColorChangeEvent;
import edu.colorado.phet.colorvision3.event.ColorChangeListener;
import edu.colorado.phet.colorvision3.model.Person;
import edu.colorado.phet.colorvision3.model.Spotlight;
import edu.colorado.phet.colorvision3.model.VisibleColor;
import edu.colorado.phet.colorvision3.view.IntensitySlider;
import edu.colorado.phet.colorvision3.view.PersonGraphic;
import edu.colorado.phet.colorvision3.view.PhotonBeamGraphic;
import edu.colorado.phet.colorvision3.view.RgbBulbsControlPanel;
import edu.colorado.phet.colorvision3.view.SpotlightGraphic;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * RgbBulbsModule implements the simulation module that demonstrates how color vision
 * works in the context of three lights (red, green and blue).
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class RgbBulbsModule extends Module implements ChangeListener, ColorChangeListener
{	
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
  private static final double HELP_LAYER = Double.MAX_VALUE;

  // Colors 
	private static final Color APPARATUS_BACKGROUND = Color.black;

	// Locations (screen coordinates, relative to upper left)
	private static final int PERSON_X             = 400;
	private static final int PERSON_Y             =  25;
	private static final int RED_SPOTLIGHT_X      = 120;
	private static final int RED_SPOTLIGHT_Y      = 105;
	private static final int GREEN_SPOTLIGHT_X    = RED_SPOTLIGHT_X;
	private static final int GREEN_SPOTLIGHT_Y    = RED_SPOTLIGHT_Y + 220;
	private static final int BLUE_SPOTLIGHT_X     = RED_SPOTLIGHT_X;
	private static final int BLUE_SPOTLIGHT_Y     = GREEN_SPOTLIGHT_Y + (GREEN_SPOTLIGHT_Y - RED_SPOTLIGHT_Y);
	private static final int RED_SLIDER_X        = 50;
	private static final int RED_SLIDER_Y        = 85;
	private static final int GREEN_SLIDER_X      = RED_SLIDER_X;
	private static final int GREEN_SLIDER_Y      = 260;
	private static final int BLUE_SLIDER_X       = RED_SLIDER_X;
	private static final int BLUE_SLIDER_Y       = 435;
	
	// Angles
	private static final double RED_SPOTLIGHT_ANGLE   = 27.0;
	private static final double GREEN_SPOTLIGHT_ANGLE = 0.0;
	private static final double BLUE_SPOTLIGHT_ANGLE  = -(RED_SPOTLIGHT_ANGLE);

	// Bounds
	private static final Rectangle BEAM_BOUNDS = new Rectangle( 0, 0, PERSON_X + 160, 10000 );
	private static final Dimension INTENSITY_SLIDER_SIZE = new Dimension(20,100);
	
	//----------------------------------------------------------------------------
	// Instance data
  //----------------------------------------------------------------------------
	
	// Models
	private Person _personModel;
	private Spotlight _redSpotlightModel, _blueSpotlightModel, _greenSpotlightModel;
	
	// Views
	private PhotonBeamGraphic _redPhotonBeam, _greenPhotonBeam, _bluePhotonBeam;
	private IntensitySlider _redSlider, _greenSlider, _blueSlider;
	
	//----------------------------------------------------------------------------
	// Constructors
  //----------------------------------------------------------------------------
	
	/**
	 * Sole constructor.
	 * 
	 * @param appModel the application model
	 */
	public RgbBulbsModule( ApplicationModel appModel )
	{
		super( SimStrings.get("RgbBulbsModule.title") );
		
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
		
		//----------------------------------------------------------------------------
		// View
    //----------------------------------------------------------------------------

		// Control Panel
		this.setControlPanel( new RgbBulbsControlPanel( this ) );

		// Apparatus Panel
		ApparatusPanel apparatusPanel = new ApparatusPanel();
		apparatusPanel.setBackground( APPARATUS_BACKGROUND );
		this.setApparatusPanel( apparatusPanel );
			
		// Person graphic
		PersonGraphic personGraphic = 
		  new PersonGraphic( apparatusPanel, PERSON_BACKGROUND_LAYER, PERSON_FOREGROUND_LAYER, _personModel );
		// Do not call apparatusPanel.addGraphic!
    
    // Red spotlight graphic
    SpotlightGraphic redSpotlight = new SpotlightGraphic( apparatusPanel, _redSpotlightModel );
    apparatusPanel.addGraphic( redSpotlight, RED_SPOTLIGHT_LAYER );
 
    // Green spotlight graphic
    SpotlightGraphic greenSpotlight = new SpotlightGraphic( apparatusPanel, _greenSpotlightModel );
    apparatusPanel.addGraphic( greenSpotlight, GREEN_SPOTLIGHT_LAYER );
    
    // Blue spotlight graphic
    SpotlightGraphic blueSpotlight = new SpotlightGraphic( apparatusPanel, _blueSpotlightModel );
    apparatusPanel.addGraphic( blueSpotlight, BLUE_SPOTLIGHT_LAYER );
     
    // Red photon beam
    _redPhotonBeam = new PhotonBeamGraphic( apparatusPanel, _redSpotlightModel );
    _redPhotonBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( _redPhotonBeam, RED_BEAM_LAYER );

    // Green photon beam
    _greenPhotonBeam = new PhotonBeamGraphic( apparatusPanel, _greenSpotlightModel );
    _greenPhotonBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( _greenPhotonBeam, GREEN_BEAM_LAYER );
    
    // Blue photon beam
    _bluePhotonBeam = new PhotonBeamGraphic( apparatusPanel, _blueSpotlightModel );
    _bluePhotonBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( _bluePhotonBeam, BLUE_BEAM_LAYER );

    // Red intensity control
    _redSlider = new IntensitySlider( VisibleColor.RED, JSlider.VERTICAL, INTENSITY_SLIDER_SIZE );
    _redSlider.setLocation( RED_SLIDER_X, RED_SLIDER_Y );
    apparatusPanel.add( _redSlider );
       
    // Green intensity control
    _greenSlider = new IntensitySlider( VisibleColor.GREEN, JSlider.VERTICAL, INTENSITY_SLIDER_SIZE );
    _greenSlider.setLocation( GREEN_SLIDER_X, GREEN_SLIDER_Y );
    apparatusPanel.add( _greenSlider );
 
    // Blue intensity control
    _blueSlider = new IntensitySlider( VisibleColor.BLUE, JSlider.VERTICAL, INTENSITY_SLIDER_SIZE );
    _blueSlider.setLocation( BLUE_SLIDER_X, BLUE_SLIDER_Y );
    apparatusPanel.add( _blueSlider );
    
		//----------------------------------------------------------------------------
		// Observers
    //----------------------------------------------------------------------------

    // Models notify their associated views of any updates.
    _personModel.addObserver( personGraphic );
    
    _redSpotlightModel.addObserver( redSpotlight );
    _redSpotlightModel.addObserver( _redPhotonBeam );
    
    _greenSpotlightModel.addObserver( greenSpotlight );
    _greenSpotlightModel.addObserver( _greenPhotonBeam );
    
    _blueSpotlightModel.addObserver( blueSpotlight );
    _blueSpotlightModel.addObserver( _bluePhotonBeam );
    
		//----------------------------------------------------------------------------
		// Listeners
    //----------------------------------------------------------------------------

    // Photon beams are notified when the simulation clock ticks.
    // Since photon beams are not model elements, we must register for clock ticks explicitly.
    clock.addClockTickListener( _redPhotonBeam );
    clock.addClockTickListener( _greenPhotonBeam );
    clock.addClockTickListener( _bluePhotonBeam );
    
    // Photon beams notify when their perceived intensity changes.
    _redPhotonBeam.addColorChangeListener( this );
    _greenPhotonBeam.addColorChangeListener( this );
    _bluePhotonBeam.addColorChangeListener( this );
    
    // Slider notify when they are moved.
    _redSlider.addChangeListener( this );
    _greenSlider.addChangeListener( this );
    _blueSlider.addChangeListener( this );
    
		//----------------------------------------------------------------------------
		// Help
    //----------------------------------------------------------------------------

		// This module has no Help.
		super.setHelpEnabled( false );
	}
	
	//----------------------------------------------------------------------------
	// Event handling
  //----------------------------------------------------------------------------
	
  /**
   * Handles a ColorChangeEvent, received when a photon hits the person.
   * 
   * @param event the event
   */
  public void colorChanged( ColorChangeEvent event )
  {
    // Ignore the color information in the event, and consult the photon beams.
    // Each beam contributes one color component.
    int red = (int) ((_redPhotonBeam.getPerceivedIntensity() / 100) * 255 );
    int green = (int) ((_greenPhotonBeam.getPerceivedIntensity() / 100) * 255 );
    int blue = (int) ((_bluePhotonBeam.getPerceivedIntensity() / 100) * 255 );
    
    // Scale alpha to match the intensity of the maximum component value.
    int alpha = Math.max( red, Math.max(green, blue) );
    
    // Update the color perceived by the person.
		VisibleColor perceivedColor = new VisibleColor( red, green, blue, alpha );
		_personModel.setColor( perceivedColor ); 
  }

  /**
   * Handles a ChangeEvent, received when a slider is moved.
   *
   * @param event the event
   */
  public void stateChanged( ChangeEvent event )
  {
    if ( event.getSource() == _redSlider )
    {
      _redSpotlightModel.setIntensity( _redSlider.getValue() );
    }
    else if ( event.getSource() == _greenSlider )
    {
      _greenSpotlightModel.setIntensity( _greenSlider.getValue() );
    }
    else if ( event.getSource() == _blueSlider )
    {
      _blueSpotlightModel.setIntensity( _blueSlider.getValue() );
    }
    else
    {
      throw new IllegalArgumentException( "unexpected ChangeEvent from " + event.getSource() );
    }
  }
  
}

/* end of file */