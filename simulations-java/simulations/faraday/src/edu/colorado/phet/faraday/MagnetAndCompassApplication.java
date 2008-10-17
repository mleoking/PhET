/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.faraday.control.menu.OptionsMenu;
import edu.colorado.phet.faraday.module.BarMagnetModule;

/**
 * MagnetsAndCompassApplication is the main application
 * for the "Magnet and Compass" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnetAndCompassApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public MagnetAndCompassApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    private void initModules() {
        BarMagnetModule barMagnetModule = new BarMagnetModule( true /* wiggleMeEnabled */ );
        barMagnetModule.setClockControlPanelVisible( false );
        addModule( barMagnetModule );
    }

    /**
     * Initializes the menubar.
     */
    private void initMenubar() {
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------
    
    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MagnetAndCompassApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, FaradayConstants.PROJECT_NAME, FaradayConstants.FLAVOR_MAGNET_AND_COMPASS );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}