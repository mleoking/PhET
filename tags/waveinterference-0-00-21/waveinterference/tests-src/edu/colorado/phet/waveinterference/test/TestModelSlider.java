/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.test;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Apr 14, 2006
 * Time: 10:55:42 AM
 * Copyright (c) Apr 14, 2006 by Sam Reid
 */

public class TestModelSlider {
    public TestModelSlider() {
        JFrame frame = new JFrame();
        final ModelSlider modelSlider = new ModelSlider( "Test", "units", 0, 100, 50 );
        VerticalLayoutPanel vlp = new VerticalLayoutPanel();
        frame.setContentPane( vlp );
        vlp.add( modelSlider );
        JButton randomValue = new JButton( "Randomize" );
        randomValue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                modelSlider.setValue( new Random().nextDouble() * 100 );
            }
        } );
        vlp.add( randomValue );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new TestModelSlider().start();
    }

    private void start() {
    }
}
