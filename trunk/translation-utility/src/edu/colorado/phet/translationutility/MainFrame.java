/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * MainFrame is the applications main window.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MainFrame extends JFrame {

    public MainFrame( String title, String jarFileName, String sourceLanguageCode, String targetLanguageCode, boolean autoTranslate ) {
        super( title );
        
        // JAR file manager
        String[] commonProjectNames = ProjectProperties.getCommonProjectNames();
        JarFileManager jarFileManager = new JarFileManager( jarFileName, commonProjectNames );
        
        // open the primary user interface
        TranslationPanel translationPanel = new TranslationPanel( this, jarFileManager, sourceLanguageCode, targetLanguageCode, autoTranslate );
        setJMenuBar( new TUMenuBar() );
        getContentPane().add( translationPanel );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        pack();
        fixFrameBounds();
    }
    
    /*
     * Fixes various problems with the bounds of the main frame.
     */
    private void fixFrameBounds() {
        
        //WORKAROUND: decrease the height to account for Windows task bar
        if ( PhetUtilities.isWindows() ) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            final int taskBarHeight = 200;
            int overlap = (int) ( getBounds().getHeight() - ( screenSize.getHeight() - taskBarHeight ) );
            if ( overlap > 0 ) {
                setBounds( (int) getBounds().getX(), (int) getBounds().getY(), 
                        (int) getBounds().getWidth(), (int) getBounds().getHeight() - overlap );
            }
        }
        
        //WORKAROUND: increase the width so we don't get a horizontal scrollbar
        setBounds( (int) getBounds().getX(), (int) getBounds().getY(),
                (int) getBounds().getWidth() + 30, (int) getBounds().getHeight() );
        
        // center on the screen
        SwingUtils.centerWindowOnScreen( this );
    }
}
