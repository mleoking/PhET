/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.piccolo.PhetTabbedPane;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 7:48:57 AM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class TestPhetTabbedPaneDescending {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Tab Test" );
        final PhetTabbedPane phetTabbedPane = new PhetTabbedPane();
        phetTabbedPane.addTab( "Hello! you guppies", new JLabel( "Hello" ) );
        phetTabbedPane.addTab( "yuppies and peppering", new JLabel( "Hello" ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( phetTabbedPane );
        frame.setSize( 1000, 400 );

        frame.setVisible( true );
    }
}
