/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.text.MessageFormat;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * TranslationUtility is the main class for the translation utility.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationUtility extends JFrame {
    
    private static final String TITLE_FORMAT = "{0} : {1} {2}";
    private static final String SOURCE_COUNTRY_CODE = "en"; // English
    private static final boolean DEBUG_COMMAND_OUTPUT = false;

    /* not intended for instantiation */
    private TranslationUtility() {}
    
    public static void main( String[] args ) {
        
        // form that title string, including version info
        PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.NoOp(), TUResources.getResourceLoader() );
        String[] titleFormatArgs = { 
                TUResources.getString( "translation-utility.name" ),
                TUResources.getString( "translation-utility.name" ),
                config.getVersion().formatForAboutDialog()
        };
        String title =  MessageFormat.format( TITLE_FORMAT, titleFormatArgs );
        
        // prompt the user for initialization info
        InitializationDialog initDialog = new InitializationDialog( title );
        SwingUtils.centerWindowOnScreen( initDialog );
        initDialog.show();
        
        // open the primary user interface
        if ( initDialog.isContinue() ) {
            Command.setDebugOutputEnabled( DEBUG_COMMAND_OUTPUT );
            String jarFileName = initDialog.getJarFileName();
            String targetCountryCode = initDialog.getTargetCountryCode();
            JarFileManager jarFileManager = new JarFileManager( jarFileName );
            TranslationPanel translationPanel = new TranslationPanel( jarFileManager, SOURCE_COUNTRY_CODE, targetCountryCode );
            JFrame frame = new JFrame( title );
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
