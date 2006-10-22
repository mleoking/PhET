/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.menu.HelpMenu;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.module.HAModule;
import edu.colorado.phet.hydrogenatom.util.ArgUtils;
import edu.colorado.phet.hydrogenatom.view.LegendPanel.LegendDialog;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

/**
 * HAApplication
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Provide this program argument to enable developer-only features.
    private static final String DEVELOPER_ARG = "-dev";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JDialog _legendDialog;
    private HAModule _module;
    
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
     * @param frameSetup
     */
    public HAApplication( String[] args, 
            String title, String description, String version, FrameSetup frameSetup )
    {
        super( args, title, description, version, frameSetup );
        initModules();
        initMenubar( args );
    }
    
    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the modules.
     */
    private void initModules() {
        _module = new HAModule();
        addModule( _module );
    }
    
    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();
        
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        frame.addMenu( optionsMenu );

        // Developer menu
        if ( ArgUtils.contains( args, DEVELOPER_ARG ) ) {
            DeveloperMenu developerMenu = new DeveloperMenu( _module );
            getPhetFrame().addMenu( developerMenu );
        }
        
        // Help menu additions
        {
            HelpMenu helpMenu = frame.getHelpMenu();
            
            JMenuItem legendMenuItem = new JMenuItem( SimStrings.get( "menu.help.legend" ) );
            legendMenuItem.setMnemonic( SimStrings.getChar( "menu.help.legend.mnemonic" ) );
            legendMenuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleLegendDialog();
                }
            } );
            helpMenu.add( legendMenuItem );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * If the legend dialog exists, pop it to the front.
     * Otherwise create a new one and show it.
     * When the legend dialog is closed, it removes itself.
     */
    private void handleLegendDialog() {
        if ( _legendDialog != null ) {
            _legendDialog.toFront();
        }
        else {
            PhetFrame frame = getPhetFrame();
            _legendDialog = new LegendDialog( frame );
            _legendDialog.addWindowListener( new WindowAdapter() {
                public void windowClosed( WindowEvent event ) {
                    _legendDialog = null;
                }
            } );
            _legendDialog.show();
        }
    }
    
    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     * 
     * @param args command line arguments
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public static void main( final String[] args ) {

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

                // Initialize look-and-feel
                PhetLookAndFeel laf = new PhetLookAndFeel();
                laf.initLookAndFeel();

                // Initialize localization.
                SimStrings.init( args, HAConstants.LOCALIZATION_BUNDLE_BASENAME );

                // Title, etc.
                String title = SimStrings.get( "HAApplication.title" );
                String description = SimStrings.get( "HAApplication.description" );
                String version = HAVersion.NUMBER;

                // Frame setup
                int width = HAConstants.APP_FRAME_SIZE.width;
                int height = HAConstants.APP_FRAME_SIZE.height;
                FrameSetup frameSetup = new FrameSetup.CenteredWithSize( width, height );

                // Create the application.
                HAApplication app = new HAApplication( args, title, description, version, frameSetup );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
