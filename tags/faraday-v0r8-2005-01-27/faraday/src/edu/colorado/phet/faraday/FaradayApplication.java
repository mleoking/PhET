/* Copyright 2004, University of Colorado */

/*
 * CVS Info - 
 * Filename : $Source$
 * Branch : $Name$ 
 * Modified by : $Author$ 
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * FaradayApplication is the main application for the PhET
 * "Faraday's Law" simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     * @throws IOException
     */
    public FaradayApplication( FaradayApplicationModel appModel ) throws IOException {
        super( appModel );
        assert( appModel != null );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point for the PhET Color Vision application.
     * 
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        
        // Initialize localization.
        {
            // Get the default locale from property javaws.locale.
            String applicationLocale = System.getProperty( "javaws.locale" );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                Locale.setDefault( new Locale( applicationLocale ) );
            }

            // Override default locale using "user.language=" command line
            // argument.
            String argsKey = "user.language=";
            if( args.length > 0 && args[0].startsWith( argsKey ) ) {
                String locale = args[0].substring( argsKey.length(), args[0].length() );
                Locale.setDefault( new Locale( locale ) );
            }

            // Initialize simulation strings using resource bundle for the
            // locale.
            SimStrings.setStrings( FaradayConfig.LOCALIZATION_BUNDLE_BASENAME );
        }

        // Get stuff needed to initialize the application model.
        String title = SimStrings.get( "FaradayApplication.title" );
        String description = SimStrings.get( "FaradayApplication.description" );
        String version = SimStrings.get( "FaradayApplication.version" );
        int width = FaradayConfig.APP_FRAME_WIDTH;
        int height = FaradayConfig.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Create the application model.
        FaradayApplicationModel appModel = new FaradayApplicationModel( title, description, version, frameSetup );

        // Create and start the application.
        PhetApplication app = null;
        try {
            app = new FaradayApplication( appModel );
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
            System.exit( 1 );
        }
        app.startApplication();
    }
}