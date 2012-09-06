// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLPhetApplication;
import edu.colorado.phet.lwjglphet.StartupUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesTeacherMenu;
import edu.colorado.phet.moleculeshapes.dev.DeveloperOptions;
import edu.colorado.phet.moleculeshapes.tabs.moleculeshapes.MoleculeShapesTab;
import edu.colorado.phet.moleculeshapes.tabs.realmolecules.RealMoleculesTab;

/**
 * The main application for Molecule Shapes
 */
public class MoleculeShapesApplication extends LWJGLPhetApplication {

    private MoleculeShapesTab tab1;
    private RealMoleculesTab tab2;

    public static final Property<Boolean> showRealMoleculeRadioButtons = new Property<Boolean>( true );
    private static final Property<Boolean> whiteBackground = new Property<Boolean>( false );

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public MoleculeShapesApplication( PhetApplicationConfig config ) {
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
            addTab( tab1 = new MoleculeShapesTab( canvas, Strings.MOLECULE__SHAPES__TITLE, false ) );
            addTab( tab2 = new RealMoleculesTab( canvas, Strings.REAL__MOLECULES, false, false ) );
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
        frame.addMenu( new MoleculeShapesTeacherMenu( whiteBackground ) {{
            if ( tab2 != null ) {
                add( new PropertyCheckBoxMenuItem( UserComponents.showAllLonePairsCheckBox, Strings.CONTROL__SHOW_ALL_LONE_PAIRS, tab2.showAllLonePairs ) );
            }
        }} );

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...

        DeveloperOptions.addDeveloperOptions( developerMenu, frame );

        developerMenu.add( new JSeparator() );
//        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleDropDownTab, "Show drop-down 2nd tab", tab2Visible ) );
//        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleKitTab, "Show kit 2nd tab", tab3Visible ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleRealModelButtonsVisible, "Show Real/Model radio buttons", showRealMoleculeRadioButtons ) );
        if ( tab2 != null ) {
            developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggle2ndTabBondAngles, "Show 2nd tab bond angles", tab2.showBondAngles ) );
        }
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

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesConstants.MOLECULE_SHAPES, MoleculeShapesApplication.class );
    }

}
