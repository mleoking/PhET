/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * TranslationUtility is the main class for the translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationUtility extends JFrame {
    
    private static final String SOURCE_COUNTRY_CODE = "en"; // English

    /* not intended for instantiation */
    private TranslationUtility() {}
    
    public static void main( String[] args ) {
        
        String title = ProjectProperties.getTitle();

        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog( title );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.show();
        if ( !initDialog.isContinue() ) {
            System.exit( 0 );
        }

        // open the primary user interface
        String jarFileName = initDialog.getJarFileName();
        String targetCountryCode = initDialog.getTargetCountryCode();
        String[] commonProjectNames = ProjectProperties.getCommonProjectNames();
        JarFileManager jarFileManager = new JarFileManager( jarFileName, commonProjectNames );

        boolean autoTranslate = initDialog.isAutoTranslateEnabled();
        TranslationPanel translationPanel = new TranslationPanel( jarFileManager, SOURCE_COUNTRY_CODE, targetCountryCode, autoTranslate );

        JFrame frame = new JFrame( title );
        frame.setJMenuBar( new TUMenuBar() );
        frame.getContentPane().add( translationPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        //WORKAROUND: increase the width so we don't get a horizontal scrollbar
        frame.setBounds( (int) frame.getBounds().getX(), (int) frame.getBounds().getY(), (int) frame.getBounds().getWidth() + 30, (int) frame.getBounds().getHeight() );
        frame.show();
    }
}
