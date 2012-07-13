// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.chemicalreactions.module.ChemicalReactionsModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsSimSharing.UserComponents;

/**
 * Main entry point to the simulation
 */
public class ChemicalReactionsApplication extends PiccoloPhetApplication {

    public static final Property<Boolean> SHOW_DEBUG_OVERLAY = new Property<Boolean>( false );
    public static final Property<Boolean> SHOW_DEBUG_REACTION_SHAPES = new Property<Boolean>( false );
    public static final Property<Boolean> ATOM_LABELS_ROTATE = new Property<Boolean>( true );

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
//        addModule( new ChemicalReactionsModule( parentFrame ) );
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

        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleDebugOverlay, "Display debugging overlay", SHOW_DEBUG_OVERLAY ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleDebugReactionShapes, "Display debugging reaction shapes", SHOW_DEBUG_REACTION_SHAPES ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.atomLabelsRotate, "Atom Labels Rotate", ATOM_LABELS_ROTATE ) );

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
