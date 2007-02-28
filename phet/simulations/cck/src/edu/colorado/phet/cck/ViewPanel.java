/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 7, 2003
 * Time: 12:33:37 PM
 * Copyright (c) Dec 7, 2003 by Sam Reid
 */
public class ViewPanel extends JPanel {
    public ViewPanel( final CCK2Module module ) {

        Font jrbFont = new Font( "Lucida Sans", Font.BOLD, 14 );
        JRadioButton lifelike = new JRadioButton( "Lifelike", true );
        lifelike.setFont( jrbFont );
        JRadioButton schematic = new JRadioButton( "Schematic" );
        schematic.setFont( jrbFont );
        JPanel viewPanel = this;
        viewPanel.setBorder( BorderFactory.createTitledBorder( "View" ) );
        viewPanel.setLayout( new BoxLayout( viewPanel, BoxLayout.Y_AXIS ) );
        lifelike.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setLifelikeView();
            }
        } );
        schematic.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSchematicView();
            }
        } );
        ButtonGroup bg = new ButtonGroup();
        bg.add( lifelike );
        bg.add( schematic );
        viewPanel.add( lifelike );
        viewPanel.add( schematic );
    }
}
