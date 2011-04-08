// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14676 $
 * Date modified : $Date:2007-04-17 02:58:50 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 7:48:57 AM
 */

public class TestPhetTabbedPane {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Tab Test" );
        final PhetTabbedPane phetTabbedPane = new PhetTabbedPane();
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();

        JLabel c = new JLabel( "Hello" );
        verticalLayoutPanel.add( c );
        final JCheckBox comp = new JCheckBox( "Logo Visible", phetTabbedPane.getLogoVisible() );
        comp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                phetTabbedPane.setLogoVisible( comp.isSelected() );
            }
        } );
        verticalLayoutPanel.add( comp );
        phetTabbedPane.addTab( "Hello!", verticalLayoutPanel );
        final JSlider slider = new JSlider( 6, 60, 10 );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "slider.getValue() = " + slider.getValue() );
                phetTabbedPane.setTabFont( new PhetFont( Font.BOLD, slider.getValue() ) );
            }
        } );
//        phetTabbedPane.addTab( "<html>Font<br>Size</html>", slider );

        final JColorChooser colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent evt ) {
                ColorSelectionModel model = colorChooser.getSelectionModel();
                // Get the new color value
                Color newColor = model.getSelectedColor();
                phetTabbedPane.setSelectedTabColor( newColor );
            }
        } );
        phetTabbedPane.addTab( "Tab Colors", colorChooser );
        JButton content = new JButton( "button" );
        phetTabbedPane.addTab( "Large Button", content );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( phetTabbedPane );
        frame.setSize( 1000, 400 );

        frame.setVisible( true );
    }
}
