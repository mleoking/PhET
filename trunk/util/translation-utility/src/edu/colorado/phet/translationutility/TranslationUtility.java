/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.File;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.simulations.ISimulation;
import edu.colorado.phet.translationutility.simulations.SimulationFactory;
import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;
import edu.colorado.phet.translationutility.userinterface.InitializationDialog;
import edu.colorado.phet.translationutility.userinterface.MainFrame;
import edu.colorado.phet.translationutility.util.ExceptionHandler;
import edu.colorado.phet.translationutility.util.TULogger;

/**
 * TranslationUtility is the main class for the translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationUtility extends JFrame {
    
    // NOTE: untested for languages other than English
    private static final Locale SOURCE_LOCALE = TUConstants.ENGLISH_LOCALE;
    
    private TranslationUtility() {}
    
    public static void start() {
        
        TULogger.log( "Translation Utility: version " + TUResources.getVersion() );
        
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
        TULogger.log( "TranslationUtility: jar=" + jarFileName );
        TULogger.log( "TranslationUtility: language=" + targetLocale.toString() );
        
        // create a Simulation
        ISimulation simulation = null;
        try {
            simulation = SimulationFactory.createSimulation( jarFileName );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        TULogger.log( "TranslationUtility: simulation type is " + simulation.getClass().getName() );
        
        // save "submitted" files in the same directory as the JAR
        String saveDirName = new File( jarFileName ).getParent();
        if ( saveDirName == null || saveDirName.length() == 0 ) {
            saveDirName = ".";
        }
        
        // open the primary user interface
        JFrame mainFrame = new MainFrame( simulation, SOURCE_LOCALE, targetLocale, saveDirName );
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mainFrame.setVisible( true );
    }

    public static void main( final String[] args ) {
        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread.
         */
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                TULogger.setEnabled( StringUtil.contains( args, "-log" ) );
                TranslationUtility.start();
            }
        } );
    }
}
