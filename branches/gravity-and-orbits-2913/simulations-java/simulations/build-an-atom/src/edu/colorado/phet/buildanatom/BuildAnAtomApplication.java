// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.buildanatom.developer.DeveloperConfiguration;
import edu.colorado.phet.buildanatom.developer.ProblemTypeSelectionDialog;
import edu.colorado.phet.buildanatom.modules.buildatom.BuildAnAtomModule;
import edu.colorado.phet.buildanatom.modules.game.BuildAnAtomGameModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The main application for this simulation.
 */
public class BuildAnAtomApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private JCheckBoxMenuItem problemDialogVisibleControl;

    ProblemTypeSelectionDialog problemTypeSelectionDialog = ProblemTypeSelectionDialog.createInstance( getPhetFrame() );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public BuildAnAtomApplication( PhetApplicationConfig config )
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
        addModule( new BuildAnAtomModule() );
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
        JMenu developerMenu = frame.getDeveloperMenu();

        problemDialogVisibleControl = new JCheckBoxMenuItem( "Show Problem Type Dialog" );
        developerMenu.add( problemDialogVisibleControl );
        problemDialogVisibleControl.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setProblemTypeDialogVisible( problemDialogVisibleControl.isSelected());
            }
        } );

        // Add a listener that will clear the check mark if the user closes
        // the problem selection dialog directly.
        problemTypeSelectionDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                problemDialogVisibleControl.setSelected(false);
            }
        });

        // Add an item for controlling whether unstable nuclei are animated.
        final JCheckBoxMenuItem animateNucleusCheckBox = new JCheckBoxMenuItem( "Animate Unstable Nucleus" ) {{
            setSelected( DeveloperConfiguration.ANIMATE_UNSTABLE_NUCLEUS_PROPERTY.get() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    DeveloperConfiguration.ANIMATE_UNSTABLE_NUCLEUS_PROPERTY.set( isSelected() );
                }
            } );
        }};

        developerMenu.add( animateNucleusCheckBox );
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
                BuildAnAtomConstants.FLAVOR_NAME_BUILD_AN_ATOM, BuildAnAtomApplication.class );
    }
}
