/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.piccolo.PhetTabbedPane;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 7:48:57 AM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class TestPhetTabbedPane {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Tab Test" );
        final PhetTabbedPane phetTabbedPane = new PhetTabbedPane();
        phetTabbedPane.addTab( "Hello!", new JLabel( "Hello" ) );
        final JSlider slider = new JSlider( 6, 60, 10 );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( "slider.getValue() = " + slider.getValue() );
                phetTabbedPane.setTabFont( new Font( "Lucida Sans", Font.BOLD, slider.getValue() ) );
            }
        } );
        phetTabbedPane.addTab( "<html>Font<br>Size</html>", slider );

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
