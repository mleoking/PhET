/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.pswing;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 11, 2005
 * Time: 12:15:55 PM
 * Copyright (c) Jul 11, 2005 by Sam Reid
 */

public class TestPSwingSimple {
    public static void main( String[] args ) {
        PSwingCanvas pCanvas = new PSwingCanvas();
        final PText pText = new PText( "PText" );
        pCanvas.getLayer().addChild( pText );
        JFrame frame = new JFrame( "Test Piccolo" );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 600, 800 );
        frame.setVisible( true );
        pCanvas.setPanEventHandler( null );
        JButton jButton = new JButton( "Test Button!" );
        jButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "TestZSwing.actionPerformed!!!!!!!!!!!!!!*********************" );
            }
        } );
        final PSwing pSwing = new PSwing( pCanvas, jButton );
        pCanvas.getLayer().addChild( pSwing );
        pSwing.repaint();

        // A Slider
        JSlider slider = new JSlider();
        PSwing pSlider = new PSwing( pCanvas, slider );
        pSlider.translate( 200, 200 );
        pCanvas.getLayer().addChild( pSlider );

        // Revalidate and repaint
        pCanvas.revalidate();
        pCanvas.repaint();
    }

}
