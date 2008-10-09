/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.colorvision;

import java.awt.Color;

import edu.colorado.phet.colorvision.view.BoundsOutliner;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * ColorVisionApplication is the main application for the Color Vision simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorVisionApplication extends PiccoloPhetApplication {

    // The background color for the application's main Frame.
    private static final Color BACKGROUND = new Color( 148, 166, 158 );

    // Whether to turn on rendering of PhetGraphic bounds
    private static final boolean BOUNDS_OUTLINE_ENABLED = false; // DEBUG

    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public ColorVisionApplication( PhetApplicationConfig config ) {
        super( config );

        Module rgbBulbsModule = new RgbBulbsModule();
        addModule( rgbBulbsModule );
        Module singleBulbModule = new SingleBulbModule();
        addModule( singleBulbModule );
        
        getPhetFrame().setBackground( BACKGROUND );
    }

    public static void main( final String[] args ) {
        
        BoundsOutliner.setEnabled( BOUNDS_OUTLINE_ENABLED ); // DEBUG
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new ColorVisionApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, ColorVisionConstants.PROJECT_NAME );
        appConfig.launchSim();
    }
}