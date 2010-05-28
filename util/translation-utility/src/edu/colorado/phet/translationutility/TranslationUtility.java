/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.simulations.ISimulation;
import edu.colorado.phet.translationutility.simulations.SimulationFactory;
import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;
import edu.colorado.phet.translationutility.userinterface.InitializationDialog;
import edu.colorado.phet.translationutility.userinterface.MainFrame;
import edu.colorado.phet.translationutility.util.ExceptionHandler;

/**
 * TranslationUtility is the main class for the translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationUtility extends JFrame {
    
    // NOTE: untested for languages other than English
    private static final Locale SOURCE_LOCALE = TUConstants.ENGLISH_LOCALE;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger( TranslationUtility.class );
    
    private TranslationUtility() {}
    
    public static void start() {
        
        logger.debug( "version {}", TUResources.getVersion() );
        logger.debug( TUResources.getOSVersion() + ", " + TUResources.getJREVersion() );
        logger.debug( "started {}", new Date().toString() );
        
        // check for a more recent version on the server
        UpdateManager.checkForUpdate();
        
        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog( SOURCE_LOCALE );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.setVisible( true );
        if ( !initDialog.isContinue() ) {
            System.exit( 0 );
        }
        String jarFileName = initDialog.getJarFileName();
        Locale targetLocale = initDialog.getTargetLocale();
        logger.debug( "jar={}", jarFileName );
        logger.debug( "targetLocale={}", targetLocale.toString() );
        
        // create a Simulation
        ISimulation simulation = null;
        try {
            simulation = SimulationFactory.createSimulation( jarFileName );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        logger.debug( "simulation type is {}", simulation.getClass().getName() );
        
        String jarDirName = new File( jarFileName ).getParent();
        if ( jarDirName == null || jarDirName.length() == 0 ) {
            jarDirName = ".";
        }
        
        // primary user interface
        final MainFrame mainFrame = new MainFrame( simulation, SOURCE_LOCALE, targetLocale, jarDirName );
        mainFrame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

        // confirm exit if there are unsaved changes
        mainFrame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent event ) {
                if ( mainFrame.hasUnsavedChanges() ) {
                    String message = HTMLUtils.toHTMLString( TUStrings.UNSAVED_CHANGES_MESSAGE + "<br><br>" + TUStrings.CONFIRM_EXIT );
                    int response = JOptionPane.showConfirmDialog( mainFrame, message, TUStrings.CONFIRM_TITLE, JOptionPane.YES_NO_OPTION );
                    if ( response == JOptionPane.YES_OPTION ) {
                        mainFrame.dispose();
                    }
                }
                else {
                    mainFrame.dispose();
                }
            }
        } );

        mainFrame.setVisible( true );
    }

    public static void main( final String[] args ) {
        if( StringUtil.contains( args, "-log" ) ) {
            Logger.getLogger( "edu.colorado.phet.translationutility" ).setLevel( Level.DEBUG );
        }
        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread.
         */
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                TranslationUtility.start();
            }
        } );
    }
}
