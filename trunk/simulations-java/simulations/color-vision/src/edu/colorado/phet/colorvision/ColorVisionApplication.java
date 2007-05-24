/* Copyright 2004, University of Colorado */

/*
 * CVS Info - 
 * Filename : $Source$
 * Branch : $Name$ 
 * Modified by : $Author$ 
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision;

import java.awt.Color;
import java.util.Properties;

import edu.colorado.phet.colorvision.phetcommon.application.PhetApplication;
import edu.colorado.phet.colorvision.phetcommon.util.PropertiesLoader;
import edu.colorado.phet.colorvision.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.colorvision.view.BoundsOutliner;

/**
 * ColorVisionApplication is the main application for the PhET Color Vision
 * simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorVisionApplication extends PhetApplication {

    // The background color for the application's main Frame.
    private static final Color BACKGROUND = new Color( 148, 166, 158 );

    // Whether to turn on rendering of PhetGraphic bounds
    private static final boolean BOUNDS_OUTLINE_ENABLED = false; // DEBUG

    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public ColorVisionApplication( ColorVisionApplicationModel appModel ) {

        super( appModel );
    }

    /**
     * Main entry point for the PhET Color Vision application.
     * 
     * @param args command line arguments
     */
    public static void main( String[] args ) {

        BoundsOutliner.setEnabled( BOUNDS_OUTLINE_ENABLED ); // DEBUG

        // Initialize localization.
        SimStrings.getInstance().init( args, ColorVisionConstants.SIM_STRINGS_NAME );
        SimStrings.getInstance().addStrings( ColorVisionConstants.COMMON_STRINGS_NAME );

        // Load simulation properties file
        Properties simulationProperties = PropertiesLoader.loadProperties( ColorVisionConstants.SIM_PROPERTIES_NAME );
        
        // Get stuff needed to initialize the application model.
        String title = SimStrings.get( "color-vision.name" );
        String description = SimStrings.get( "color-vision.description" );
        String version = PhetApplication.getVersionString( simulationProperties );
        int width = ColorVisionConstants.APP_FRAME_WIDTH;
        int height = ColorVisionConstants.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

        // Create the application model.
        ColorVisionApplicationModel appModel = new ColorVisionApplicationModel( title, description, version, frameSetup );
        
        // Create and start the application.
        PhetApplication app = new ColorVisionApplication( appModel );
        app.setSimulationProperties( simulationProperties );
        app.getApplicationView().getPhetFrame().setBackground( BACKGROUND );
        app.startApplication();
    }
}