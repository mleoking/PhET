// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.test;

import javax.swing.*;

/**
 * @author Sam Reid
 */
public class TestBoxLayout {
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new JPanel() {{
                setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
                add( new JLabel( "A" ) );
                add( new JLabel( "B" ) );
            }} );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            pack();
        }}.setVisible( true );
    }
}
