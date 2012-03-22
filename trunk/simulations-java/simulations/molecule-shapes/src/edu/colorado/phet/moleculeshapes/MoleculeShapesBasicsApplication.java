// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.jmephet.JMEPhetApplication;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesTeacherMenu;
import edu.colorado.phet.moleculeshapes.dev.DeveloperOptions;
import edu.colorado.phet.moleculeshapes.tabs.moleculeshapes.MoleculeShapesTab;
import edu.colorado.phet.moleculeshapes.tabs.realmolecules.RealMoleculesTab;

/**
 * The main application for Molecule Shapes: Basics (with a few things stripped out)
 */
public class MoleculeShapesBasicsApplication extends JMEPhetApplication {

    private static final Property<Boolean> whiteBackground = new Property<Boolean>( false );

    private MoleculeShapesTab tab;

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

        addModule( new MoleculeShapesModule( parentFrame ) {{
            addTab( tab = new MoleculeShapesTab( Strings.MOLECULE__SHAPES__BASICS__TITLE, true ) );
            addTab( new RealMoleculesTab( Strings.REAL__MOLECULES, false, true ) );
            addTab( new RealMoleculesTab( Strings.REAL__MOLECULES, true, true ) );
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

        DeveloperOptions.addDeveloperOptions( developerMenu, frame, tab );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        JMEUtils.initializeJME( args );
        JMEUtils.disableBufferTrackingPerformanceHack();

        MoleculeShapesColor.switchToBasicColors();

        /*
        * If you want to customize your application (look-&-feel, window size, etc)
        * create your own PhetApplicationConfig and use one of the other launchSim methods
        */
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesConstants.MOLECULE_SHAPES_BASICS, MoleculeShapesBasicsApplication.class );
    }

}
