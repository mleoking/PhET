// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.faraday;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.faraday.control.menu.FaradayOptionsMenu;
import edu.colorado.phet.faraday.module.BarMagnetModule;
import edu.colorado.phet.faraday.module.ElectromagnetModule;

/**
 * MagnetsAndElectromagnetsApplication is the main application 
 * for the "Magnets and Electromagnets" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnetsAndElectromagnetsApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public MagnetsAndElectromagnetsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    private void initModules() {
        
        BarMagnetModule barMagnetModule = new BarMagnetModule( true /* wiggleMeEnabled */);
        addModule( barMagnetModule );
        
        ElectromagnetModule electromagnetModule = new ElectromagnetModule();
        addModule( electromagnetModule );
    }

    /**
     * Initializes the menubar.
     */
    private void initMenubar() {
        // Options menu
        FaradayOptionsMenu optionsMenu = new FaradayOptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MagnetsAndElectromagnetsApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, FaradayConstants.PROJECT_NAME, FaradayConstants.FLAVOR_MAGNETS_AND_ELECTROMAGNETS );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}