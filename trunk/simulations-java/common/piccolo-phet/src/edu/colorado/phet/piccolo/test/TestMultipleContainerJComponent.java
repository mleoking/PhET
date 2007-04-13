/* Copyright 2007, University of Colorado */
package edu.colorado.phet.piccolo.test;

import javax.swing.*;

public class TestMultipleContainerJComponent {
    private JFrame frame1;
    private JFrame frame2;
    private JButton button;

    public TestMultipleContainerJComponent() {
        frame1 = new JFrame( "Frame 1" );
        frame2 = new JFrame( "Frame 2" );
        button = new JButton( "Hello" );
        frame1.setContentPane( button );
        frame2.setContentPane( button );
        frame1.pack();
        frame2.pack();
    }

    public static void main( String[] args ) {
        new TestMultipleContainerJComponent().start();
    }

    private void start() {
        frame1.setVisible( true );
        frame2.setVisible( true );
    }
}
