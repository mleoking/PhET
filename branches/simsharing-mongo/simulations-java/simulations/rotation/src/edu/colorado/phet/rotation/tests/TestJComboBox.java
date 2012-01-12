// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests;

import java.awt.*;

import javax.swing.*;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Demonstrates the failure of JComboBox when embedded in PSwing.
 */

public class TestJComboBox {
    private JFrame frame;

    public TestJComboBox() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PSwingCanvas pCanvas = new PSwingCanvas();
        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        JComboBox jComboBox1 = new JComboBox( new String[]{"red", "green", "blue"} );
        JComboBox jComboBox2 = new JComboBox( new String[]{"cat", "dog", "squirrel", "anteater"} );
        panel.add( jComboBox1, gridBagConstraints );
        panel.add( jComboBox2, gridBagConstraints );
        PSwing pSwing = new PSwing( panel );
        pSwing.setOffset( 200, 200 );
        pSwing.scale( 2.0 );
        pCanvas.getLayer().addChild( pSwing );
        frame.setContentPane( pCanvas );
        frame.setSize( 400, 400 );
    }

    public static void main( String[] args ) {
        new TestJComboBox().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}