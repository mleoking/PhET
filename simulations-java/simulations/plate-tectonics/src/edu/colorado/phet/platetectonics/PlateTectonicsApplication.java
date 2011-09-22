// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;


import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.platetectonics.modules.DoublePlateModule;
import edu.colorado.phet.platetectonics.modules.SinglePlateModule;

public class PlateTectonicsApplication extends PiccoloPhetApplication {

    private SinglePlateModule singlePlateModule;
    private DoublePlateModule doublePlateModule;

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public PlateTectonicsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {

        Frame parentFrame = getPhetFrame();

        addModule( singlePlateModule = new SinglePlateModule( parentFrame ) );
        addModule( doublePlateModule = new DoublePlateModule( parentFrame ) );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // Create main frame.
        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu

        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...

        developerMenu.add( new JSeparator() );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        JMEUtils.initializeJME( args );

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, PlateTectonicsConstants.PROJECT_NAME, PlateTectonicsApplication.class );
    }
}
