/* SingleBulbModule.java */

package edu.colorado.phet.colorvision3;

import java.awt.Color;

import edu.colorado.phet.colorvision3.view.SingleBulbControlPanel;
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
public class SingleBulbModule extends Module
{
  // Colors
  private static Color APPARATUS_BACKGROUND = Color.black;
    
  // Beam types.
  public static final int BEAM_TYPE_SOLID = 0;
  public static final int BEAM_TYPE_PHOTONS = 1;
    
  // Bulb types.
  public static final int BULB_TYPE_WHITE = 2;
  public static final int BULB_TYPE_MONOCHROMATIC = 3;

  /**
  	* Sole constructor.
  	* 
  	* @param appModel the application model
  	*/
	protected SingleBulbModule( ApplicationModel appModel ) 
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

		//----------------------------------------------------------------------------
		// View
    //----------------------------------------------------------------------------

		// Control Panel
		this.setControlPanel( new SingleBulbControlPanel( this ) );
		
		// Apparatus Panel
		ApparatusPanel apparatusPanel = new ApparatusPanel();
		apparatusPanel.setBackground( APPARATUS_BACKGROUND );
		this.setApparatusPanel( apparatusPanel );
		
		//----------------------------------------------------------------------------
		// Connections (Observers and Listeners)
    //----------------------------------------------------------------------------

	}

	/**
	 * Sets the type of beam.
	 * 
	 * @param beamType the beam type, either BEAM_TYPE_SOLID or BEAM_TYPE_PHOTONS
	 */
	public void setBeamType( int beamType )
	{
	  // TODO validate beamType
	  if ( beamType == BEAM_TYPE_SOLID )
	  {
	    System.out.println( "beam=solid"); // TODO handle beam type change
	  }
	  else {
	    System.out.println( "beam=photons"); // TODO handle beam type change
	  }
	}
	
	/**
	 * Sets the type of bulb.
	 * 
	 * @param bulbType the bulb type, either BULB_TYPE_WHITE or BULB_TYPE_MONOCHROMATIC
	 */
	public void setBulbType( int bulbType )
	{
	  // TODO validate bulbType
	  if ( bulbType == BULB_TYPE_WHITE )
	  {
	    System.out.println( "bulb=white"); // TODO handle bulb type change
	  }
	  else 
	  {
	    System.out.println( "bulb=monochromatic"); // TODO handle type change
	  }
	}
	
	/**
	 * Turns the filter on and off.
	 * 
	 * @param enabled true to turn on, false to turn off
	 */
	public void setFilterEnabled( boolean enabled )
	{
	    System.out.println( "filter=" + (enabled ? "on" : "off") ); // TODO handle filter enable
	}
	
}

/* end of file */