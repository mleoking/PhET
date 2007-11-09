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
            
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu( TUResources.getString( "menu.file" ) );
            fileMenu.setMnemonic( TUResources.getChar( "menu.file.mnemonic", 'F' ) );
            menuBar.add( fileMenu );
            JMenuItem exitMenuItem = new JMenuItem( TUResources.getString( "menu.item.exit" ), TUResources.getChar( "menu.item.exit.mnemonic", 'x' ) );
            fileMenu.add( exitMenuItem );
            exitMenuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.exit( 0 );
                }
            });
            
            JFrame frame = new JFrame( title );
            frame.setJMenuBar( menuBar );
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
