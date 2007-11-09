/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.text.MessageFormat;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * TranslationUtility is the main class for the translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationUtility extends JFrame {
    
    private static final boolean DEBUG_COMMAND_OUTPUT = false;
    
    private static final String SOURCE_COUNTRY_CODE = "en"; // English

    /* not intended for instantiation */
    private TranslationUtility() {}
    
    public static void main( String[] args ) {
        
        String title =  ProjectProperties.getTitle();
        
        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog( title );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.show();
        
        // open the primary user interface
        if ( initDialog.isContinue() ) {
            
            Command.setDebugOutputEnabled( DEBUG_COMMAND_OUTPUT );
            
            String jarFileName = initDialog.getJarFileName();
            String targetCountryCode = initDialog.getTargetCountryCode();
            String[] commonProjectNames = ProjectProperties.getCommonProjectNames();
            JarFileManager jarFileManager = new JarFileManager( jarFileName, commonProjectNames );
            
            TranslationPanel translationPanel = new TranslationPanel( jarFileManager, SOURCE_COUNTRY_CODE, targetCountryCode );
            
            JFrame frame = new JFrame( title );
            frame.getContentPane().add( translationPanel );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.pack();
            //WORKAROUND: increase the width so we don't get a horizontal scrollbar
            frame.setBounds( (int)frame.getBounds().getX(), (int)frame.getBounds().getY(), (int)frame.getBounds().getWidth() + 30, (int)frame.getBounds().getHeight() );
            frame.show();
        }
        else {
            System.exit( 0 );
        }
    }
}
