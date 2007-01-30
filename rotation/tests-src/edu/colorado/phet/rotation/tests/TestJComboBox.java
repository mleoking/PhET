package edu.colorado.phet.rotation.tests;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 30, 2007
 * Time: 11:23:23 AM
 * Copyright (c) Jan 30, 2007 by Sam Reid
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
        pCanvas.getLayer().addChild( new PSwing( pCanvas, panel ) );
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