// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision;

import java.awt.*;

import edu.colorado.phet.colorvision.view.BoundsOutliner;
import edu.colorado.phet.common.phetcommon.application.*;
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
     * @param config the application model
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

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, ColorVisionConstants.PROJECT_NAME );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}