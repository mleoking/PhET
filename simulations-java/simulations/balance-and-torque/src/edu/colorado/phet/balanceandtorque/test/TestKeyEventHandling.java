// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.test;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

//REVIEW Consider deleting this, I usually keep only the tests that have some long-term value.

/**
 * Test app for key handling.
 */
public class TestKeyEventHandling {

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 400, 300 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        canvas.getLayer().addChild( new PhetPPath( new Rectangle2D.Double( 100, 100, 10, 10 ), Color.PINK ) );

        canvas.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void keyTyped( PInputEvent event ) {
                System.out.println( "1 - keyTyped" );
            }

            @Override public void keyPressed( PInputEvent event ) {
                System.out.println( "1 - keyPressed" );
            }

            @Override public void keyReleased( PInputEvent event ) {
                System.out.println( "1 - keyReleased" );
            }

            @Override public void mouseClicked( PInputEvent event ) {
                System.out.println( "1 - mouse clicked" );
            }
        } );

        canvas.addKeyListener( new KeyAdapter() {
            @Override public void keyTyped( KeyEvent event ) {
                System.out.println( "2 - keyTyped" );
                System.out.println( "event.getKeyChar() = " + event.getKeyChar() );
                System.out.println( "event.getKeyChar() in hex = " + Integer.toHexString( event.getKeyChar() ) );
                System.out.println( "event.getID() = " + event.getID() );
                System.out.println( "event.getComponent() = " + event.getComponent() );
                System.out.println( "event.getKeyCode() in hex = " + Integer.toHexString( event.getKeyCode() ) );
                System.out.println( "event.getKeyCode() = " + event.getKeyCode() );
                System.out.println( "event.getKeyCode() in hex = " + Integer.toHexString( event.getKeyCode() ) );
            }

            @Override public void keyPressed( KeyEvent e ) {
                System.out.println( "2 - keyPressed" );
            }

            @Override public void keyReleased( KeyEvent e ) {
                System.out.println( "2 - keyReleased" );
            }
        } );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
