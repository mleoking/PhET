/* RgbBulbModule.java */

package edu.colorado.phet.colorvision3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JSlider;

import edu.colorado.phet.colorvision3.event.IntensityChangeEvent;
import edu.colorado.phet.colorvision3.event.IntensityChangeListener;
import edu.colorado.phet.colorvision3.model.Person2D;
import edu.colorado.phet.colorvision3.model.Spotlight2D;
import edu.colorado.phet.colorvision3.view.IntensityControl;
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
public class RgbBulbsModule extends Module implements IntensityChangeListener
{	
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
	private static final int RED_CONTROL_X        = 50;
	private static final int RED_CONTROL_Y        = 85;
	private static final int GREEN_CONTROL_X      = RED_CONTROL_X;
	private static final int GREEN_CONTROL_Y      = 260;
	private static final int BLUE_CONTROL_X       = RED_CONTROL_X;
	private static final int BLUE_CONTROL_Y       = 435;
	
	// Angles
	private static final double RED_SPOTLIGHT_ANGLE   = 27.0;
	private static final double GREEN_SPOTLIGHT_ANGLE = 0.0;
	private static final double BLUE_SPOTLIGHT_ANGLE  = -(RED_SPOTLIGHT_ANGLE);

	// Bounds
	private static final Rectangle BEAM_BOUNDS = new Rectangle( 0, 0, PERSON_X + 160, 10000 );
	private static final Dimension INTENSITY_CONTROL_SIZE = new Dimension(20,100);
	
	// Models
	private Person2D _personModel;
	
	// Views
	private PhotonBeamGraphic _redBeam, _greenBeam, _blueBeam;
	
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
		_personModel = new Person2D();
		_personModel.setLocation( PERSON_X, PERSON_Y );
		
		// Red Spotlight model
		Spotlight2D redModel = new Spotlight2D();
		redModel.setColor( Color.red );
		redModel.setIntensity( Spotlight2D.INTENSITY_MIN );
		redModel.setLocation( RED_SPOTLIGHT_X, RED_SPOTLIGHT_Y );
		redModel.setDirection( RED_SPOTLIGHT_ANGLE );
	
		// Green Spotlight model
		Spotlight2D greenModel = new Spotlight2D();
		greenModel.setColor( Color.green );
		greenModel.setIntensity( Spotlight2D.INTENSITY_MIN );
		greenModel.setLocation( GREEN_SPOTLIGHT_X, GREEN_SPOTLIGHT_Y );
		greenModel.setDirection( GREEN_SPOTLIGHT_ANGLE );
		
		// Blue Spotlight model
		Spotlight2D blueModel = new Spotlight2D();
		blueModel.setColor( Color.blue );
		blueModel.setIntensity( Spotlight2D.INTENSITY_MIN );
		blueModel.setLocation( BLUE_SPOTLIGHT_X, BLUE_SPOTLIGHT_Y );
		blueModel.setDirection( BLUE_SPOTLIGHT_ANGLE );
		
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
    SpotlightGraphic redSpotlight = new SpotlightGraphic( apparatusPanel, redModel );
    apparatusPanel.addGraphic( redSpotlight, RED_SPOTLIGHT_LAYER );
 
    // Green spotlight graphic
    SpotlightGraphic greenSpotlight = new SpotlightGraphic( apparatusPanel, greenModel );
    apparatusPanel.addGraphic( greenSpotlight, GREEN_SPOTLIGHT_LAYER );
    
    // Blue spotlight graphic
    SpotlightGraphic blueSpotlight = new SpotlightGraphic( apparatusPanel, blueModel );
    apparatusPanel.addGraphic( blueSpotlight, BLUE_SPOTLIGHT_LAYER );
     
    // Red photon beam
    _redBeam = new PhotonBeamGraphic( apparatusPanel, redModel );
    _redBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( _redBeam, RED_BEAM_LAYER );

    // Green photon beam
    _greenBeam = new PhotonBeamGraphic( apparatusPanel, greenModel );
    _greenBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( _greenBeam, GREEN_BEAM_LAYER );
    
    // Blue photon beam
    _blueBeam = new PhotonBeamGraphic( apparatusPanel, blueModel );
    _blueBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( _blueBeam, BLUE_BEAM_LAYER );

    // Red intensity control
    IntensityControl redControl = new IntensityControl( redModel, JSlider.VERTICAL, INTENSITY_CONTROL_SIZE );
    redControl.setLocation( RED_CONTROL_X, RED_CONTROL_Y );
    apparatusPanel.add( redControl );
       
    // Green intensity control
    IntensityControl greenControl = new IntensityControl( greenModel, JSlider.VERTICAL, INTENSITY_CONTROL_SIZE );
    greenControl.setLocation( GREEN_CONTROL_X, GREEN_CONTROL_Y );
    apparatusPanel.add( greenControl );
 
    // Blue intensity control
    IntensityControl blueControl = new IntensityControl( blueModel, JSlider.VERTICAL, INTENSITY_CONTROL_SIZE );
    blueControl.setLocation( BLUE_CONTROL_X, BLUE_CONTROL_Y );
    apparatusPanel.add( blueControl );
    
		//----------------------------------------------------------------------------
		// Observers
    //----------------------------------------------------------------------------

    // Models notify their associated views of any updates.
    _personModel.addObserver( personGraphic );
    
    redModel.addObserver( redSpotlight );
    redModel.addObserver( _redBeam );
    
    greenModel.addObserver( greenSpotlight );
    greenModel.addObserver( _greenBeam );
    
    blueModel.addObserver( blueSpotlight );
    blueModel.addObserver( _blueBeam );
    
		//----------------------------------------------------------------------------
		// Listeners
    //----------------------------------------------------------------------------

    // Photon beams are notified when the simulation clock ticks.
    // Since photon beams are not model elements, we must register for clock ticks explicitly.
    clock.addClockTickListener( _redBeam );
    clock.addClockTickListener( _greenBeam );
    clock.addClockTickListener( _blueBeam );
    
    // Photon beams notify the RgbBulbsController when their perceived intensity changes.
    _redBeam.addIntensityChangeListener( this );
    _greenBeam.addIntensityChangeListener( this );
    _blueBeam.addIntensityChangeListener( this );
    
		//----------------------------------------------------------------------------
		// Help
    //----------------------------------------------------------------------------

		// This module has no Help.
		super.setHelpEnabled( false );
	}
	
  /**
   * Handles an IntensityChangeEvent.
   * The new perceived color is calculated and set.
   * 
   * @param event the event
   */
  public void intensityChanged( IntensityChangeEvent event )
  {
		Color color = getPerceivedColor();
		_personModel.setColor( color ); 
  }

  /**
   * Gets the perceived color produced by the combination of photon beams.
   * Each photon beam contributes one color component (RGB).
   * Alpha is scaled to match the intensity of the maximum component value.
   * 
   * @return the perceived color
   */
  private Color getPerceivedColor()
  {
    double maxIntensity = Spotlight2D.INTENSITY_MAX;
    
    // Each beam contributes one color component.
    int red = (int) ((_redBeam.getPerceivedIntensity() / maxIntensity) * 255 );
    int green = (int) ((_greenBeam.getPerceivedIntensity() / maxIntensity) * 255 );
    int blue = (int) ((_blueBeam.getPerceivedIntensity() / maxIntensity) * 255 );
    int alpha = Math.max( red, Math.max(green, blue) );

		return new Color( red, green, blue, alpha );
  }
  
}

/* end of file */