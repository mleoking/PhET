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
 * MainFrame is the main frame for the application.
 * It contains a scrollable panel of translatable strings at the top, action buttons at the bottom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MainFrame extends JFrame {

    public MainFrame( String title, String jarFileName, String targetCountryCode ) {
        super( title );
        
        JarFileManager jarFileManager = new JarFileManager( jarFileName );
        
        TranslationPanel translationsPanel = new TranslationPanel( jarFileManager, targetCountryCode );
        JScrollPane scrollPane = new JScrollPane( translationsPanel );
        ButtonPanel buttonPanel = new ButtonPanel( jarFileManager, targetCountryCode, translationsPanel );
        
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( buttonPanel, BorderLayout.CENTER );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( scrollPane, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );
        
        getContentPane().add( mainPanel );
        
        pack();
    }
}
