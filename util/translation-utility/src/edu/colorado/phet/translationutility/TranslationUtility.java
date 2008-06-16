/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.File;

import javax.swing.JFrame;

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
    
    //NOTE: not tested with any source language code except "en"
    private static final String SOURCE_LANGUAGE_CODE = "en";

    /* not intended for instantiation */
    private TranslationUtility() {}
    
    /**
     * main
     * @param args
     */
    public static void main( String[] args ) {
        
        // check for a more recent version on the server
        UpdateManager.checkForUpdate();
        
        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog();
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.setVisible( true );
        if ( !initDialog.isContinue() ) {
            System.exit( 0 );
        }
        String jarFileName = initDialog.getJarFileName();
        String targetLanguageCode = initDialog.getTargetLanguageCode();
        
        // create a Simulation
        Simulation simulation = null;
        try {
            simulation = SimulationFactory.createSimulation( jarFileName );
        }
        catch ( SimulationException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        
        // determine where to save files
        String saveDirName = new File( jarFileName ).getParent();
        if ( saveDirName == null || saveDirName.length() == 0 ) {
            saveDirName = ".";
        }
        
        // open the primary user interface
        JFrame mainFrame = new MainFrame( simulation, SOURCE_LANGUAGE_CODE, targetLanguageCode, saveDirName );
        mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        mainFrame.setVisible( true );
    }
}
