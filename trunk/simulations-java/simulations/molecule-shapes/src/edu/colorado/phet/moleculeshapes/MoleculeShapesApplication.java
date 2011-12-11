// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.jmephet.JMEPhetApplication;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.TeachersMenu;
import edu.colorado.phet.moleculeshapes.dev.DeveloperOptions;
import edu.colorado.phet.moleculeshapes.tabs.moleculeshapes.MoleculeShapesTab;
import edu.colorado.phet.moleculeshapes.tabs.realmolecules.RealMoleculesTab;

/**
 * The main application for Molecule Shapes
 */
public class MoleculeShapesApplication extends JMEPhetApplication {

    private MoleculeShapesTab tab1;
    private RealMoleculesTab tab2;

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

        addModule( new MoleculeShapesModule( parentFrame ) {{
            addTab( tab1 = new MoleculeShapesTab( Strings.MOLECULE__SHAPES__TITLE, false ) );
            addTab( tab2 = new RealMoleculesTab( Strings.REAL__MOLECULES, false, false ) );
            addTab( new RealMoleculesTab( Strings.REAL__MOLECULES, true, false ) );
//        addModule( new DebugModule( parentFrame, "Debug" ) );
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
        frame.addMenu( new TeachersMenu() );

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...

        DeveloperOptions.addDeveloperOptions( developerMenu, frame, tab1 );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        JMEUtils.initializeJME( args );
        JMEUtils.disableBufferTrackingPerformanceHack();

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesConstants.MOLECULE_SHAPES, MoleculeShapesApplication.class );
    }

}
