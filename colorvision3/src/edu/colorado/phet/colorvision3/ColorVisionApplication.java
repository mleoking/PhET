/* ColorVisionApplication.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;

import java.util.Locale;

/**
 * ColorVisionApplication is the main application for the PhET Color Vision simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class ColorVisionApplication extends PhetApplication
{
  /**
   * Sole constructor.
   * 
   * @param appModel the application model
   */
	public ColorVisionApplication( ColorVisionApplicationModel appModel )
	{
		super( appModel );
	}

	/**
	 * Main entry point for the PhET Color Vision application.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
	  // Initialize localization.
	  {
	    // Get the default locale from property javaws.locale.
	    String applicationLocale = System.getProperty( "javaws.locale" );
	    if ( applicationLocale != null && !applicationLocale.equals( "" ) )
	    {
	      Locale.setDefault( new Locale( applicationLocale ) );
	    }
	    
	    // Override default locale using "user.language=" command line argument.
	    String argsKey = "user.language=";
	    if ( args.length > 0 && args[0].startsWith( argsKey ) )
	    {
	      String locale = args[0].substring( argsKey.length(), args[0].length() );
	      Locale.setDefault( new Locale( locale ) );
	    }
	    
	    // Initialize simulation strings using resource bundle for the locale.
	    SimStrings.setStrings( ColorVisionConfig.LOCALIZATION_BUNDLE_BASENAME );
	  }
	    
	  // Get stuff needed to initialize the application model.
		String title = SimStrings.get( "ColorVisionApplication.title" );
		String description = SimStrings.get( "ColorVisionApplication.description" );
		String version = SimStrings.get( "ColorVisionApplication.version" );
		int width = ColorVisionConfig.APP_FRAME_WIDTH;
		int height = ColorVisionConfig.APP_FRAME_HEIGHT;
		FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

		// Create the application model.
		ColorVisionApplicationModel appModel = 
		    new ColorVisionApplicationModel( title, description, version, frameSetup );
		
		// Create and start the application.
		PhetApplication app = new ColorVisionApplication( appModel );
		app.startApplication();
	}
}

/* end of file */