/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

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
    
    //XXX these values should eventually be passed in via command line args
    private static final String JAR_FILE_NAME = "optical-tweezers.jar"; // this JAR must be in your preset working directory
    private static final String TARGET_COUNTRY_CODE = "fr";
    
    private static final boolean DEBUG_COMMAND_OUTPUT = false;

    private TranslationUtility() {}
    
    public static void main( String[] args ) {
        Command.setDebugOutputEnabled( DEBUG_COMMAND_OUTPUT );
        JarFileManager jarFileManager = new JarFileManager( JAR_FILE_NAME );
        TranslationPanel translationPanel = new TranslationPanel( jarFileManager, TARGET_COUNTRY_CODE );
        JFrame frame = new JFrame( TITLE );
        frame.getContentPane().add( translationPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.show();
    }
}
