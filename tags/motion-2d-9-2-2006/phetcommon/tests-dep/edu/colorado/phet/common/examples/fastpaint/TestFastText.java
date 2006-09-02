/**
 * Class: TestFastPaintShapes
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common.examples.fastpaint;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.fastpaint.FastPaintTextGraphic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TestFastText {
    public static void main( String[] args ) throws InterruptedException {
        final ApparatusPanel ap = new ApparatusPanel();
        ap.addGraphicsSetup( new BasicGraphicsSetup() );
        JFrame frame = new JFrame( "Test Fast Paint" );
        frame.setContentPane( ap );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        Font font = new Font( "Lucida Sans", Font.PLAIN, 12 );

        final ArrayList fastShapes = new ArrayList();
        final Random random = new Random();
        for( int i = 0; i < 1000; i++ ) {
            FastPaintTextGraphic ft = new FastPaintTextGraphic( "Foo!", font,
                                                                random.nextInt( 600 ),
                                                                random.nextInt( 600 ),
                                                                ap );

            fastShapes.add( ft );
            ap.addGraphic( ft );
        }
        FastPaintTextGraphic ft = (FastPaintTextGraphic)fastShapes.get( 0 );
        ft.setFont( new Font( "Lucida Sans", Font.BOLD, 32 ) );
        ft.setPaint( Color.red );

        Runnable r = new Runnable() {
            public void run() {
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                        int elm = random.nextInt( fastShapes.size() );
                        FastPaintTextGraphic ft = (FastPaintTextGraphic)fastShapes.get( 0 );
                        Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
                        //                        ft.setPaint( color );
                        ft.setLocation( ( ft.getLocation().getX() + 2 ) % 600,
                                        ( ft.getLocation().getY() + 2 ) % 600 );
                        //                        ap.repaint();
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread( r );
        t.start();

        frame.setVisible( true );
    }
}
