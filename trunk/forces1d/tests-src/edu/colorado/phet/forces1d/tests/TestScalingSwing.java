/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.tests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Feb 2, 2005
 * Time: 1:04:43 AM
 * Copyright (c) Feb 2, 2005 by Sam Reid
 */

public class TestScalingSwing {
    private static JPanel contentPane;

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        final JButton button = new JButton( "Hello" ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                double scale = contentPane.getWidth() / 600.0;
                g2.scale( scale, scale );
                super.paintComponent( g );
                g2.scale( 1 / scale, 1 / scale );
            }

            protected void paintBorder( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                double scale = contentPane.getWidth() / 600.0;
                g2.scale( scale, scale );
                super.paintBorder( g2 );
                g2.scale( 1 / scale, 1 / scale );
            }
        };

        contentPane = new JPanel( null );
        contentPane.add( button );
        contentPane.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                double x = contentPane.getWidth() / 2;
                double y = contentPane.getHeight() / 2;
                button.reshape( (int)x, (int)y, button.getPreferredSize().width, button.getPreferredSize().height );
            }
        } );
        button.reshape( 200, 200, button.getPreferredSize().width, button.getPreferredSize().height );
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 800 );
        frame.show();

        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    Thread.sleep( 100 );
                    contentPane.paintImmediately( 0, 0, contentPane.getWidth(), contentPane.getHeight() );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
    }
}
