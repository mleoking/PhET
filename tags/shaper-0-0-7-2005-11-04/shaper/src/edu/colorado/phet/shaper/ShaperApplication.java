/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.DebugMenu;
import edu.colorado.phet.common.view.components.menu.HelpMenu;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.module.ShaperModule;


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
            String title, String description, String version, AbstractClock clock,
            boolean useClockControlPanel, FrameSetup frameSetup )
    {
        super( args, title, description, version, clock, useClockControlPanel, frameSetup );
        initModules( clock );  
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
    private void initModules( AbstractClock clock ) {
        _shaperModule = new ShaperModule( clock );
        setModules( new Module[] { _shaperModule } );
        setInitialModule( _shaperModule );
    }
    
    //----------------------------------------------------------------------------
    // Menubar
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the menubar.
     */
    private void initMenubar() {
     
        // Debug menu extensions
        DebugMenu debugMenu = getPhetFrame().getDebugMenu();
        if ( debugMenu != null ) {
            //XXX Add debug menu items here.
        }
        
        // Help menu extensions
        HelpMenu helpMenu = getPhetFrame().getHelpMenu();
        if ( helpMenu != null ) {
            
            // Explanation...
            JMenuItem explanationItem = new JMenuItem( SimStrings.get( "HelpMenu.explanation" ) );
            explanationItem.setMnemonic( SimStrings.get( "HelpMenu.explanation.mnemonic" ).charAt( 0 ) );
            explanationItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                   handleExplanation();
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

    /*
     * Opens the "Explanation" dialog, which shows what a real experimental apparatus
     * looks like.  The dialog is not reused, and we make sure that only one such
     * dialog can be open at a time.
     */
    private void handleExplanation() {
        if ( _explanationDialog == null ) {
            
            // Load the image file
            ImageIcon image = null;
            try {
                image = new ImageIcon( ImageLoader.loadBufferedImage( ShaperConstants.EXPLANATION_IMAGE ) );
            }
            catch ( IOException ioe ) {
                image = null;
                ioe.printStackTrace();
            }
            
            // Put the image in a non-modal, non-resizable dialog.
            if ( image != null ) {
                _explanationDialog = new JDialog( getPhetFrame(), false /* nonmodal */);
                // Put the image in the dialog and resize to fit
                JLabel label = new JLabel( image );
                _explanationDialog.getContentPane().add( label );
                _explanationDialog.setSize( new Dimension( image.getIconWidth(), image.getIconHeight() ) );
                _explanationDialog.setResizable( false );
                // Center the dialog on the screen
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                int w = _explanationDialog.getSize().width;
                int h = _explanationDialog.getSize().height;
                int x = ( dim.width - w ) / 2;
                int y = ( dim.height - h ) / 2;
                _explanationDialog.setLocation( x, y );
                // Release the dialog reference when the dialog is closed
                _explanationDialog.addWindowListener( new WindowAdapter() {
                    public void windowClosing( WindowEvent e ) {
                        _explanationDialog = null;
                    }
                } );
                // Show the dialog
                _explanationDialog.show();
            }
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
        
        // Clock
        double timeStep = ShaperConstants.CLOCK_TIME_STEP;
        int waitTime = ( 1000 / ShaperConstants.CLOCK_FRAME_RATE ); // milliseconds
        boolean isFixed = ShaperConstants.CLOCK_TIME_STEP_IS_CONSTANT;
        AbstractClock clock = new SwingTimerClock( timeStep, waitTime, isFixed );
        boolean useClockControlPanel = false;
        
        // Frame setup
        int width = ShaperConstants.APP_FRAME_WIDTH;
        int height = ShaperConstants.APP_FRAME_HEIGHT;
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );
        
        // Create the application.
        ShaperApplication app = new ShaperApplication( args,
                 title, description, version, clock, useClockControlPanel, frameSetup );
        
        // Start the application.
        app.startApplication();
    }
}
