/* RgbBulbModule.java */

package edu.colorado.phet.colorvision3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JSlider;

import edu.colorado.phet.colorvision3.controller.RgbBulbsController;
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
public class RgbBulbsModule extends Module
{	
	// Rendering layers
  private static final double PERSON_LAYER = 1;
  private static final double RED_BEAM_LAYER = 2;
  private static final double GREEN_BEAM_LAYER = 3;
  private static final double BLUE_BEAM_LAYER = 4;
  private static final double RED_SPOTLIGHT_LAYER = 5;
  private static final double GREEN_SPOTLIGHT_LAYER = 6;
  private static final double BLUE_SPOTLIGHT_LAYER = 7;
  private static final double RGB_BULBS_HELP_LAYER = Double.MAX_VALUE;

  // Colors 
	private static final Color APPARATUS_BACKGROUND = Color.black;

	// Locations (screen coordinates, relative to upper left)
	private static final int PERSON_X             = 400;
	private static final int PERSON_Y             =  25;
	private static final int RED_SPOTLIGHT_X      = 210;
	private static final int RED_SPOTLIGHT_Y      = 165;
	private static final int GREEN_SPOTLIGHT_X    = RED_SPOTLIGHT_X;
	private static final int GREEN_SPOTLIGHT_Y    = RED_SPOTLIGHT_Y + 160;
	private static final int BLUE_SPOTLIGHT_X     = RED_SPOTLIGHT_X;
	private static final int BLUE_SPOTLIGHT_Y     = GREEN_SPOTLIGHT_Y + (GREEN_SPOTLIGHT_Y - RED_SPOTLIGHT_Y);
	private static final int RED_CONTROL_X        = 50;
	private static final int RED_CONTROL_Y        = 85;
	private static final int GREEN_CONTROL_X      = RED_CONTROL_X;
	private static final int GREEN_CONTROL_Y      = 260;
	private static final int BLUE_CONTROL_X       = RED_CONTROL_X;
	private static final int BLUE_CONTROL_Y       = 435;
	
	// Angles
	public static final double RED_SPOTLIGHT_ANGLE   = 24.0;
	public static final double GREEN_SPOTLIGHT_ANGLE = 0.0;
	public static final double BLUE_SPOTLIGHT_ANGLE  = -(RED_SPOTLIGHT_ANGLE);

	// Bounds
	private static final Rectangle BEAM_BOUNDS = new Rectangle( 0, 0, PERSON_X + 160, 10000 );
	public static final Dimension INTENSITY_CONTROL_SIZE = new Dimension(20,100);
	
	/**
	 * Sole constructor.
	 * 
	 * @param appModel the application model
	 */
	protected RgbBulbsModule( ApplicationModel appModel )
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
		Person2D personModel = new Person2D();
		personModel.setLocation( PERSON_X, PERSON_Y );
		
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
		PersonGraphic person = new PersonGraphic( apparatusPanel, personModel );
		apparatusPanel.addGraphic( person, PERSON_LAYER );
    
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
    PhotonBeamGraphic redBeam = new PhotonBeamGraphic( apparatusPanel, redModel );
    redBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( redBeam, RED_BEAM_LAYER );

    // Green photon beam
    PhotonBeamGraphic greenBeam = new PhotonBeamGraphic( apparatusPanel, greenModel );
    greenBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( greenBeam, GREEN_BEAM_LAYER );
    
    // Blue photon beam
    PhotonBeamGraphic blueBeam = new PhotonBeamGraphic( apparatusPanel, blueModel );
    blueBeam.setBounds( BEAM_BOUNDS );
    apparatusPanel.addGraphic( blueBeam, BLUE_BEAM_LAYER );

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
		// Controller
    //----------------------------------------------------------------------------

    // Module controller
    RgbBulbsController controller = new RgbBulbsController( redBeam, greenBeam, blueBeam, personModel );
    
		//----------------------------------------------------------------------------
		// Observers
    //----------------------------------------------------------------------------

    // Models notify their associated views of any updates.
    personModel.addObserver( person );
    
    redModel.addObserver( redSpotlight );
    redModel.addObserver( redBeam );
    
    greenModel.addObserver( greenSpotlight );
    greenModel.addObserver( greenBeam );
    
    blueModel.addObserver( blueSpotlight );
    blueModel.addObserver( blueBeam );
    
		//----------------------------------------------------------------------------
		// Listeners
    //----------------------------------------------------------------------------

    // Photon beams are notified when the simulation clock ticks.
    // Since photon beams are not model elements, we must register for clock ticks explicitly.
    clock.addClockTickListener( redBeam );
    clock.addClockTickListener( greenBeam );
    clock.addClockTickListener( blueBeam );
    
    // Photon beams notify the RgbBulbsController when their perceived intensity changes.
    redBeam.addIntensityChangeListener( controller );
    greenBeam.addIntensityChangeListener( controller );
    blueBeam.addIntensityChangeListener( controller );
    
		//----------------------------------------------------------------------------
		// Help
    //----------------------------------------------------------------------------

		// This module has no Help.
		super.setHelpEnabled( false );
	}
}

/* end of file */