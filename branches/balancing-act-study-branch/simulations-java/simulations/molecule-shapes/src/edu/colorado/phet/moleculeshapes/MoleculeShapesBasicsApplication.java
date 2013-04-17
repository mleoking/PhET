// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLPhetApplication;
import edu.colorado.phet.lwjglphet.StartupUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesTeacherMenu;
import edu.colorado.phet.moleculeshapes.dev.DeveloperOptions;
import edu.colorado.phet.moleculeshapes.tabs.moleculeshapes.MoleculeShapesTab;
import edu.colorado.phet.moleculeshapes.tabs.realmolecules.RealMoleculesTab;

/**
 * The main application for Molecule Shapes: Basics (with a few things stripped out)
 */
public class MoleculeShapesBasicsApplication extends LWJGLPhetApplication {

    private static final Property<Boolean> whiteBackground = new Property<Boolean>( false );

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public MoleculeShapesBasicsApplication( PhetApplicationConfig config ) {
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

        final LWJGLCanvas canvas = LWJGLCanvas.getCanvasInstance( parentFrame );

        addModule( new PhetTabbedPane.TabbedModule( canvas ) {{
            addTab( new MoleculeShapesTab( canvas, Strings.MOLECULE__SHAPES__BASICS__TITLE, true ) );
            addTab( new RealMoleculesTab( canvas, Strings.REAL__MOLECULES, false, true ) );
        }} );

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

        // Teacher's menu
        frame.addMenu( new MoleculeShapesTeacherMenu( whiteBackground ) );

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...

        DeveloperOptions.addDeveloperOptions( developerMenu, frame );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        try {
            StartupUtils.setupLibraries();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        MoleculeShapesColor.switchToBasicColors();

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesConstants.MOLECULE_SHAPES_BASICS, MoleculeShapesBasicsApplication.class );
    }

}
