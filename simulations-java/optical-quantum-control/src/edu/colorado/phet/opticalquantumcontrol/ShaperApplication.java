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
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticalquantumcontrol.help.ExplanationDialog;
import edu.colorado.phet.opticalquantumcontrol.module.ShaperModule;


/**
 * ShaperApplication is the main application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ShaperModule _shaperModule;
    private JDialog _explanationDialog;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param args command line arguments
     * @param title
     * @param description
     * @param version
     * @param clock
     * @param useClockControlPanel
     * @param frameSetup
     */
    public ShaperApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
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
        _shaperModule = new ShaperModule();
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
            JMenuItem explanationItem = new JMenuItem( SimStrings.get( "HelpMenu.explanation" ) );
            explanationItem.setMnemonic( SimStrings.get( "HelpMenu.explanation.mnemonic" ).charAt( 0 ) );
            explanationItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _explanationDialog = new ExplanationDialog( getPhetFrame() );
                    _explanationDialog.show();
                }
            } );
            helpMenu.add( explanationItem );
            
            // Cheat...
            JMenuItem cheatItem = new JMenuItem( SimStrings.get( "HelpMenu.cheat" ) );
            cheatItem.setMnemonic( SimStrings.get( "HelpMenu.cheat.mnemonic" ).charAt( 0 ) );
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

        // Initialize localization.
        SimStrings.init( args, ShaperConstants.LOCALIZATION_BUNDLE_BASENAME );
        
        // Title, etc.
        String title = SimStrings.get( "ShaperApplication.title" );
        String description = SimStrings.get( "ShaperApplication.description" );
        String version = Version.NUMBER;
        
        // Frame setup
        int width = ShaperConstants.APP_FRAME_WIDTH;
        int height = ShaperConstants.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );
        
        // Create the application.
        ShaperApplication app = new ShaperApplication( args, title, description, version, frameSetup );
        
        // Start the application.
        app.startApplication();
    }
}
