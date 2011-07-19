// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

/**
 * Author: Sam Reid
 * Aug 21, 2007, 4:10:02 PM
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.Timer;

import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

public class TestPSwingRepaintManager {
    private JFrame frame = new JFrame( getClass().getName().substring( getClass().getName().lastIndexOf( '.' ) + 1 ) );
    private JTextField component = new JTextField( "hello" + System.currentTimeMillis() );
    private Timer timer = new Timer( 30, new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            updateGraphics();
        }
    } );
    private PPath path = new PPath( new Rectangle2D.Double( 0, 0, 50, 50 ) );
    private PSwingCanvas contentPane = new PSwingCanvas();

    public TestPSwingRepaintManager() {
        contentPane.addKeyListener( new PDebugKeyHandler() );


        PSwing textField = new PSwing( component );
        contentPane.getLayer().addChild( textField );

        contentPane.getLayer().addChild( path );

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
    }

    private void updateGraphics() {
        component.setText( System.currentTimeMillis() + "" );
        path.setOffset( 500, 500 );
        double frequency = 0.5;
        path.setRotation( Math.sin( System.currentTimeMillis() / 1000.0 * frequency ) );
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
        contentPane.requestFocus();
    }

    public static void main( String[] args ) {
        new TestPSwingRepaintManager().start();
    }
}
