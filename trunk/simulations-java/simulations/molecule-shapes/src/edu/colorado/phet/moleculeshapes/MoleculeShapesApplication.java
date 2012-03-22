// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.Frame;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.jmephet.JMEPhetApplication;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.UserComponents;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesTeacherMenu;
import edu.colorado.phet.moleculeshapes.dev.DeveloperOptions;
import edu.colorado.phet.moleculeshapes.tabs.moleculeshapes.MoleculeShapesTab;
import edu.colorado.phet.moleculeshapes.tabs.realmolecules.RealMoleculesTab;

/**
 * The main application for Molecule Shapes
 */
public class MoleculeShapesApplication extends JMEPhetApplication {

    private MoleculeShapesTab tab1;
    private RealMoleculesTab tab2;
    private RealMoleculesTab tab3;

    public static final Property<Boolean> tab2Visible = new Property<Boolean>( false );
    public static final Property<Boolean> tab3Visible = new Property<Boolean>( false );
    public static final Property<Boolean> showRealMoleculeRadioButtons = new Property<Boolean>( true );
    private static final Property<Boolean> whiteBackground = new Property<Boolean>( false );
    private PhetApplicationConfig config;

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public MoleculeShapesApplication( PhetApplicationConfig config ) {
        super( config );
        this.config = config;
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

            if ( config.hasCommandLineArg( "use.combobox" ) ) {
                addTab( tab2 = new RealMoleculesTab( Strings.REAL__MOLECULES, false, false ) );
            }
            if ( config.hasCommandLineArg( "use.kits" ) ) {
                addTab( tab3 = new RealMoleculesTab( Strings.REAL__MOLECULES, true, false ) );
            }


//            tab2Visible.addObserver( new SimpleObserver() {
//                                         public void update() {
//                                             if ( tab2Visible.get() ) {
//                                                 addTab( tab2 );
//                                             }
//                                             else {
//                                                 removeTab( tab2 );
//                                             }
//                                         }
//                                     }, false );
//
//            tab3Visible.addObserver( new SimpleObserver() {
//                                         public void update() {
//                                             if ( tab3Visible.get() ) {
//                                                 addTab( tab3 );
//                                             }
//                                             else {
//                                                 removeTab( tab3 );
//                                             }
//                                         }
//                                     }, false );
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
            if ( tab2 != null || tab3 != null ) {
                add( new PropertyCheckBoxMenuItem( UserComponents.showAllLonePairsCheckBox, Strings.CONTROL__SHOW_ALL_LONE_PAIRS, ( tab2 == null ? tab3 : tab2 ).showAllLonePairs ) );
            }
        }} );

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...

        DeveloperOptions.addDeveloperOptions( developerMenu, frame, tab1 );

        developerMenu.add( new JSeparator() );
//        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleDropDownTab, "Show drop-down 2nd tab", tab2Visible ) );
//        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleKitTab, "Show kit 2nd tab", tab3Visible ) );
        developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggleRealModelButtonsVisible, "Show Real/Model radio buttons", showRealMoleculeRadioButtons ) );
        if ( tab2 != null || tab3 != null ) {
            developerMenu.add( new PropertyCheckBoxMenuItem( UserComponents.devToggle2ndTabBondAngles, "Show 2nd tab bond angles", ( tab2 == null ? tab3 : tab2 ).showBondAngles ) );
        }
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
