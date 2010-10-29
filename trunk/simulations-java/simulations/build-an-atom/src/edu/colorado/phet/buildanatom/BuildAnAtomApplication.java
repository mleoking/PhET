/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import edu.colorado.phet.buildanatom.developer.ProblemTypeSelectionDialog;
import edu.colorado.phet.buildanatom.modules.buildatom.BuildAnAtomModule;
import edu.colorado.phet.buildanatom.modules.game.GameModule;
import edu.colorado.phet.common.phetcommon.application.Module;
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

    private JCheckBoxMenuItem problemDialogVisableControl;

    ProblemTypeSelectionDialog problemTypeSelectionDialog = ProblemTypeSelectionDialog.getInstance();

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

        Frame parentFrame = getPhetFrame();

        Module firstModule = new BuildAnAtomModule( parentFrame ){{
            getModulePanel().setLogoPanel( null );
        }};
        addModule( firstModule );

        addModule( new GameModule() );
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

        problemDialogVisableControl = new JCheckBoxMenuItem( "Show Problem Type Dialog" );
        developerMenu.add( problemDialogVisableControl );
        problemDialogVisableControl.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setProblemTypeDialogVisible(problemDialogVisableControl.isSelected());
            }
        } );

        // Add a listener that will clear the check mark if the user closes
        // the problem selection dialog directly.
        problemTypeSelectionDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                problemDialogVisableControl.setSelected(false);
            }
        });
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
        new PhetApplicationLauncher().launchSim( args, BuildAnAtomConstants.PROJECT_NAME, BuildAnAtomApplication.class );
    }
}
