// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.test;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import javax.swing.*;

public class FullscreenSwingTest implements MouseMotionListener {

    public static void main( String[] args ) {
        FullscreenSwingTest test = new FullscreenSwingTest();
        test.run();
    }

    JFrame frame;
    PopupDialog dialog;

    public FullscreenSwingTest() {
        frame = new JFrame();
        frame.setUndecorated( true );
        frame.setResizable( false );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setIgnoreRepaint( true );
        frame.addMouseMotionListener( this );

//        DisplayMode mode = new DisplayMode(800, 600, 16, 75);
//
//        GraphicsEnvironment environment =
//            GraphicsEnvironment.getLocalGraphicsEnvironment();
//
//        GraphicsDevice device = environment.getDefaultScreenDevice();
//        device.setFullScreenWindow(frame);
//        device.setDisplayMode(mode);

//        frame.createBufferStrategy(2);
        frame.setVisible( true );
        frame.setSize( 800, 600 );
    }

    public void run() {
        boolean done = false;

        while ( !done ) {
            BufferStrategy strategy = frame.getBufferStrategy();
            Graphics g = strategy.getDrawGraphics();

            g.setColor( Color.black );
            g.fillRect( 0, 0, 800, 600 );
            g.setColor( Color.white );
            g.fillRect( 350, 250, 100, 100 );

            g.dispose();
            strategy.show();
            Toolkit.getDefaultToolkit().sync();

            // --- Dialog doesn't need that Graphics ---
            if ( dialog != null ) {
                dialog.render( null );
            }

            try {
                Thread.sleep( 200 );
            }
            catch ( InterruptedException e ) {
            }
        }


        frame.setVisible( false );
        frame.dispose();
    }

    class PopupDialog extends JDialog {

        public PopupDialog( Frame parent ) {
            super( parent, "" );
            setUndecorated( true );
            setResizable( false );
            setIgnoreRepaint( true );

            JPanel pane = new JPanel();
            pane.setIgnoreRepaint( true );

            JButton bBla = new JButton( "bla" );
            bBla.setIgnoreRepaint( true );
            pane.add( bBla );

            JButton bQuit = new JButton( "quit" );
            bQuit.setIgnoreRepaint( true );
            pane.add( bQuit );

            getContentPane().add( pane );

            pack();
            setLocation( 350, 250 );
            setVisible( true );

        }

        public void render( Graphics g ) {
            repaint();
        }

        public void quit() {
            setVisible( false );
            dispose();
        }

    }

    public void mouseMoved( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();

        if ( x > 350 && x < 450 && y > 250 && y < 350 ) {
            if ( dialog == null ) {
                dialog = new PopupDialog( frame );
            }
        }
        else if ( dialog != null ) {
            dialog.quit();
            dialog = null;
        }
    }

    public void mouseDragged( MouseEvent e ) {
    }

}
