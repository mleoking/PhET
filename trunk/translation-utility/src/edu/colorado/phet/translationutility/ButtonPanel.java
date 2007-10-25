/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;


public class ButtonPanel extends JPanel {

    public ButtonPanel() {
        
        JButton runButton = new JButton( "Run simulation" );
        JButton submitButton = new JButton( "Submit translation...");
        JButton helpButton = new JButton( "Help..." );
        
        JPanel buttonPanel = new JPanel( new GridLayout( 1, 5 ) );
        buttonPanel.add( runButton );
        buttonPanel.add( submitButton );
        buttonPanel.add( helpButton );

        add( buttonPanel );
    }
}
