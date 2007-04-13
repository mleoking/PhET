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

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.menu.HelpMenu;
import edu.colorado.phet.opticalquantumcontrol.help.ExplanationDialog;
import edu.colorado.phet.opticalquantumcontrol.module.OQCModule;


/**
 * OQCApplication is the main application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OQCApplication extends PhetApplication {

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
     * 
     * @param args command line arguments
     */
    public OQCApplication( String[] args ) {
        super( args, OQCResources.getConfig(), OQCConstants.FRAME_SETUP );
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
    public static void main( String[] args ) throws IOException {

        // Create the application.
        OQCApplication app = new OQCApplication( args );
        
        // Start the application.
        app.startApplication();
    }
}
