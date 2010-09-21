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

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
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
                    _explanationDialog.setVisible( true );
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

    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new OpticalQuantumControlApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, OQCConstants.PROJECT_NAME );
        appConfig.setFrameSetup( OQCConstants.FRAME_SETUP ); // MoleculeAnimation requires knowledge of the FrameSetup
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
