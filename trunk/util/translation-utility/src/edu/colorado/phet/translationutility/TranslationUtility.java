// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.simulations.Simulation;
import edu.colorado.phet.translationutility.simulations.SimulationFactory;
import edu.colorado.phet.translationutility.simulations.Simulation.SimulationException;
import edu.colorado.phet.translationutility.userinterface.InitializationDialog;
import edu.colorado.phet.translationutility.userinterface.MainFrame;
import edu.colorado.phet.translationutility.util.ExceptionHandler;

/**
 * TranslationUtility is the main class for the translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationUtility extends JFrame {

    private static final Logger LOGGER = Logger.getLogger( TranslationUtility.class.getName() );

    private TranslationUtility() {
    }

    public static void start() {

        LOGGER.info( "version = " + TUResources.getVersion() );
        LOGGER.info( "os = " + TUResources.getOSVersion() );
        LOGGER.info( "java = " + TUResources.getJREVersion() );

        // check for a more recent version on the server
        UpdateManager.checkForUpdate();

        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog( TUConstants.SOURCE_LOCALE );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.setVisible( true );
        if ( !initDialog.isContinue() ) {
            System.exit( 0 );
        }
        String jarFileName = initDialog.getJarFileName();
        Locale targetLocale = initDialog.getTargetLocale();
        LOGGER.info( "jar=" + jarFileName );
        LOGGER.info( "targetLocale=" + targetLocale.toString() );

        // create a Simulation
        Simulation simulation = null;
        try {
            simulation = SimulationFactory.createSimulation( jarFileName );
        }
        catch( SimulationException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        LOGGER.info( "simulation type is " + simulation.getClass().getName() );

        String jarDirName = new File( jarFileName ).getParent();
        if ( jarDirName == null || jarDirName.length() == 0 ) {
            jarDirName = ".";
        }

        // primary user interface
        final MainFrame mainFrame = new MainFrame( simulation, TUConstants.SOURCE_LOCALE, targetLocale, jarDirName );
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
        if ( StringUtil.contains( args, "-log" ) ) {
            LoggingUtils.enableAllLogging( TranslationUtility.class.getPackage().getName() );
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
