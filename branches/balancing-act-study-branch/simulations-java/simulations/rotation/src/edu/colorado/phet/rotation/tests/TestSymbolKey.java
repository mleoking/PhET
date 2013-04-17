// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:05:51 PM
 *
 */

import javax.swing.*;

import edu.colorado.phet.rotation.controls.SymbolKey;

public class TestSymbolKey {
    private JFrame frame;

    public TestSymbolKey() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( new SymbolKey() );
    }

    public static void main( String[] args ) {
        new TestSymbolKey().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
