//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.buildamolecule.module.AbstractBuildAMoleculeModule;
import edu.colorado.phet.buildamolecule.module.CollectMultipleModule;
import edu.colorado.phet.buildamolecule.module.LargerMoleculesModule;
import edu.colorado.phet.buildamolecule.module.MakeMoleculeModule;
import edu.colorado.phet.buildamolecule.tests.MoleculeTableDialog;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBoxMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.UserComponent;

/**
 * The main application for this simulation.
 */
public class BuildAMoleculeApplication extends PiccoloPhetApplication {

    /**
     * Shows a cursor that indicates a bond will be split when the mouse is over, and on a click it will break the bond
     */
    public static final Property<Boolean> allowBondBreaking = new Property<Boolean>( true );


    /*---------------------------------------------------------------------------*
    * audio
    *----------------------------------------------------------------------------*/

    private static final GameAudioPlayer audioPlayer = new GameAudioPlayer( true ) {{
        init();
    }};
    public static final Property<Boolean> soundEnabled = new Property<Boolean>( true );

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public BuildAMoleculeApplication( PhetApplicationConfig config ) {
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

        Module makeMoleculeModule = new MakeMoleculeModule( parentFrame );
        addModule( makeMoleculeModule );

        Module collectMultipleModule = new CollectMultipleModule( parentFrame );
        addModule( collectMultipleModule );

        Module largerMolecules = new LargerMoleculesModule( parentFrame );
        addModule( largerMolecules );
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

        optionsMenu.add( new JMenuItem( BuildAMoleculeStrings.RESET_CURRENT_TAB ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    getActiveModule().reset();
                    SimSharingManager.sendUserMessage( UserComponent.resetCurrentTab, UserComponentTypes.menuItem, UserActions.activated );
                }
            } );
        }} );

        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...
        developerMenu.add( new JMenuItem( "Show Table of Molecules" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new MoleculeTableDialog( getPhetFrame() ).setVisible( true );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Regenerate model" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (AbstractBuildAMoleculeModule) getActiveModule() ).addGeneratedCollection();
                }
            } );
        }} );
        developerMenu.add( new PropertyCheckBoxMenuItem( "Enable bond breaking", allowBondBreaking ) );
        developerMenu.add( new JMenuItem( "Change Filled Collection Box Color" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    new JDialog( frame ) {{
                        setTitle( "Build a Molecule Colors" );
                        setResizable( false );

                        final ColorControl control = new ColorControl( frame, "box highlight:", BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_HIGHLIGHT.get() );
                        control.addChangeListener( new ChangeListener() {
                            public void stateChanged( ChangeEvent e ) {
                                BuildAMoleculeConstants.MOLECULE_COLLECTION_BOX_HIGHLIGHT.set( control.getColor() );
                            }
                        } );

                        setContentPane( control );
                        pack();
                        SwingUtils.centerInParent( this );
                    }}.setVisible( true );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Trigger complete dialog" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (AbstractBuildAMoleculeModule) getActiveModule() ).getCanvas().getCurrentCollection().allCollectionBoxesFilled.set( true );
                }
            } );
        }} );
        developerMenu.add( new JMenuItem( "Load additional data for profiling" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    MoleculeList.testLoadingForProfiling();
                }
            } );
        }} );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        MoleculeList.startInitialization();

        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, BuildAMoleculeConstants.PROJECT_NAME, BuildAMoleculeApplication.class );
    }

    public static void playCollectionBoxFilledSound() {
        if ( soundEnabled.get() ) {
            audioPlayer.correctAnswer();
        }
    }
}
