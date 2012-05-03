// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions;

import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.chemicalreactions.module.ChemicalReactionsModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class ChemicalReactionsApplication extends PiccoloPhetApplication {
    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public ChemicalReactionsApplication( PhetApplicationConfig config ) {
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

        Module makeMoleculeModule = new ChemicalReactionsModule( parentFrame );
        addModule( makeMoleculeModule );
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

    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {

        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, ChemicalReactionsResources.PROJECT_NAME, ChemicalReactionsApplication.class );
    }
}
