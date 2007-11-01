/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * TranslationUtility is the main class for the translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationUtility extends JFrame {
    
    private static final String NAME = "PhET Simulation Translator";
    private static final String VERSION = "0.00.01";
    private static final String SVN_REVISION = "18366";
    
    private static final String TITLE = NAME + " : " + VERSION + " (" + SVN_REVISION + ")";
    
    private static final String SOURCE_COUNTRY_CODE = "en"; // English
    private static final boolean DEBUG_COMMAND_OUTPUT = true;

    private TranslationUtility() {}
    
    public static void main( String[] args ) {
        InitializationDialog initDialog = new InitializationDialog( TITLE );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.show();
        if ( initDialog.isContinue() ) {
            Command.setDebugOutputEnabled( DEBUG_COMMAND_OUTPUT );
            String jarFileName = initDialog.getJarFileName();
            String targetCountryCode = initDialog.getTargetCountryCode();
            JarFileManager jarFileManager = new JarFileManager( jarFileName );
            TranslationPanel translationPanel = new TranslationPanel( jarFileManager, SOURCE_COUNTRY_CODE, targetCountryCode );
            JFrame frame = new JFrame( TITLE );
            SwingUtils.centerWindowOnScreen( frame );
            frame.getContentPane().add( translationPanel );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.pack();
            frame.show();
        }
        else {
            System.exit( 0 );
        }
    }
}
