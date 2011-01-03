/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom;

import edu.colorado.phet.buildanatom.developer.ProblemTypeSelectionDialog;
import edu.colorado.phet.buildanatom.modules.game.BuildAnAtomGameModule;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.InteractiveIsotopeModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The main application for this simulation.
 */
public class IsotopesAndAtomicMassApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    ProblemTypeSelectionDialog problemTypeSelectionDialog = ProblemTypeSelectionDialog.createInstance( getPhetFrame() );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public IsotopesAndAtomicMassApplication( PhetApplicationConfig config )
    {
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
        addModule( new InteractiveIsotopeModule() );
        addModule( new BuildAnAtomGameModule() );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        // Add developer menu items here.
    }

    /**
     * @param isVisible
     */
    protected void setProblemTypeDialogVisible( boolean isVisible ) {
        problemTypeSelectionDialog.setVisible( isVisible );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, BuildAnAtomConstants.PROJECT_NAME,
                BuildAnAtomConstants.FLAVOR_NAME_ISOTOPES_AND_ATOMIC_MASS, IsotopesAndAtomicMassApplication.class );
    }
}
