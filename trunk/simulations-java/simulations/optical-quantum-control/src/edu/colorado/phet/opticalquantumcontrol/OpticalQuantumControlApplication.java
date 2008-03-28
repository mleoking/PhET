/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.menu.HelpMenu;
import edu.colorado.phet.opticalquantumcontrol.help.ExplanationDialog;
import edu.colorado.phet.opticalquantumcontrol.module.OQCModule;


/**
 * OpticalQuantumControlApplication is the main application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OpticalQuantumControlApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private OQCModule _shaperModule;
    private JDialog _explanationDialog;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public OpticalQuantumControlApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Modules
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     *
     * @param clock
     */
    private void initModules() {
        _shaperModule = new OQCModule();
        addModule( _shaperModule );
    }

    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // Help menu extensions
        HelpMenu helpMenu = getPhetFrame().getHelpMenu();
        if ( helpMenu != null ) {

            // Explanation...
            JMenuItem explanationItem = new JMenuItem( OQCResources.MENU_EXPLANATION );
            explanationItem.setMnemonic( OQCResources.MENU_EXPLANATION_MNEMONIC );
            explanationItem.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    _explanationDialog = new ExplanationDialog( getPhetFrame() );
                    _explanationDialog.show();
                }
            } );
            helpMenu.add( explanationItem );

            // Cheat...
            JMenuItem cheatItem = new JMenuItem( OQCResources.MENU_CHEAT );
            cheatItem.setMnemonic( OQCResources.MENU_CHEAT_MNEMONIC );
            cheatItem.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    _shaperModule.setCheatEnabled( true );
                }
            } );
            helpMenu.add( cheatItem );
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    public static void main( final String[] args ) throws IOException {

        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                PhetApplicationConfig config = new PhetApplicationConfig( args, OQCConstants.FRAME_SETUP, OQCResources.getResourceLoader() );

                // Create the application.
                OpticalQuantumControlApplication app = new OpticalQuantumControlApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
