/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;


public class ButtonPanel extends JPanel {

    public ButtonPanel( final JarFileManager jarFileManager, final String countryCode, final TranslationPanel translationPanel ) {
        
        JButton runButton = new JButton( "Run simulation" );
        runButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                Properties targetProperties = translationPanel.getTargetProperties();
                jarFileManager.writeTargetProperties( targetProperties, countryCode );
                jarFileManager.runJarFile( countryCode );
            }
        } );
        
        JButton submitButton = new JButton( "Submit translation...");
        submitButton.setEnabled( false );//XXX
        
        JButton helpButton = new JButton( "Help..." );
        helpButton.setEnabled( false );//XXX
        
        JPanel buttonPanel = new JPanel( new GridLayout( 1, 5 ) );
        buttonPanel.add( runButton );
        buttonPanel.add( submitButton );
        buttonPanel.add( helpButton );

        add( buttonPanel );
    }
}
