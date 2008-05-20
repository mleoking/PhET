package edu.umd.cs.piccolox.pswing.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Created by: Sam
 * May 20, 2008 at 12:36:51 PM
 */
public class TestPSwingBounds {
    private static Random random = new Random();

    public static void main( String[] args ) {
        JFrame frame = new JFrame( TestPSwingBounds.class.getName() );
        PSwingCanvas contentPane = new PSwingCanvas();

        JPanel component = new JPanel();
        final JLabel jLabel = new JLabel( "sample text" );
        component.add( jLabel );
        final PSwing pSwing = new PSwing( component );
        contentPane.getLayer().addChild( pSwing );

        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "bounds before: "+pSwing.getFullBounds() );
                jLabel.setText( jLabel.getText() + " " + random.nextInt( 10 ) );
                System.out.println( "bounds after: "+pSwing.getFullBounds() );
            }
        } );
        timer.start();

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }
}
