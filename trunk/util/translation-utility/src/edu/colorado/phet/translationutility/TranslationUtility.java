/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
    private static final String SOURCE_LANGUAGE_CODE = TUConstants.ENGLISH_LANGUAGE_CODE;
    
    private TranslationUtility() {}
    
    public static void start() {
        
        // check for a more recent version on the server
        UpdateManager.checkForUpdate();
        
        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog( SOURCE_LANGUAGE_CODE );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.setVisible( true );
        if ( !initDialog.isContinue() ) {
            System.exit( 0 );
        }
        String jarFileName = initDialog.getJarFileName();
        String targetLanguageCode = initDialog.getTargetLanguageCode();
        
        // create a Simulation
        ISimulation simulation = null;
        try {
            simulation = SimulationFactory.createSimulation( jarFileName );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        
        // save "submitted" files in the same directory as the JAR
        String saveDirName = new File( jarFileName ).getParent();
        if ( saveDirName == null || saveDirName.length() == 0 ) {
            saveDirName = ".";
        }
        
        // open the primary user interface
        JFrame mainFrame = new MainFrame( simulation, SOURCE_LANGUAGE_CODE, targetLanguageCode, saveDirName );
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mainFrame.setVisible( true );
    }

    public static void main( String[] args ) {
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
