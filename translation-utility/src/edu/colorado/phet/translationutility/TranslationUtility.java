/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

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
        
        String title = ProjectProperties.getTitle();

        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog( title );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.show();
        if ( !initDialog.isContinue() ) {
            System.exit( 0 );
        }
        String jarFileName = initDialog.getJarFileName();
        String targetCountryCode = initDialog.getTargetLanguageCode();
        boolean autoTranslate = initDialog.isAutoTranslateEnabled();
        
        // JAR file manager
        String[] commonProjectNames = ProjectProperties.getCommonProjectNames();
        JarFileManager jarFileManager = new JarFileManager( jarFileName, commonProjectNames );
        
        // open the primary user interface
        JFrame frame = new JFrame( title );
        TranslationPanel translationPanel = new TranslationPanel( frame, jarFileManager, SOURCE_LANGUAGE_CODE, targetCountryCode, autoTranslate );
        frame.setJMenuBar( new TUMenuBar() );
        frame.getContentPane().add( translationPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        fixFrameBounds( frame );
        frame.show();
    }
    
    /*
     * Fixes various problems with the bounds of the main frame.
     */
    private static void fixFrameBounds( JFrame frame ) {
        
        //WORKAROUND: decrease the height to account for Windows task bar
        if ( PhetUtilities.isWindows() ) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int taskBarHeight = 200;
            int overlap = (int) ( frame.getBounds().getHeight() - ( screenSize.getHeight() - taskBarHeight ) );
            if ( overlap > 0 ) {
                frame.setBounds( (int) frame.getBounds().getX(), (int) frame.getBounds().getY(), 
                        (int) frame.getBounds().getWidth(), (int) frame.getBounds().getHeight() - overlap );
            }
        }
        
        //WORKAROUND: increase the width so we don't get a horizontal scrollbar
        frame.setBounds( (int) frame.getBounds().getX(), (int) frame.getBounds().getY(),
                (int) frame.getBounds().getWidth() + 30, (int) frame.getBounds().getHeight() );
        
        // center on the screen
        SwingUtils.centerWindowOnScreen( frame );
    }
}
