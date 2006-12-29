package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:05:51 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

import edu.colorado.phet.rotation.controls.SymbolKey;

import javax.swing.*;

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
