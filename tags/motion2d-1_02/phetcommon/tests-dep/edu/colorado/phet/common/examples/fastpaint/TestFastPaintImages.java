/**
 * Class: TestFastPaintShapes
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common.examples.fastpaint;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.fastpaint.FastPaintImageGraphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TestFastPaintImages {
    public static void main( String[] args ) throws InterruptedException, IOException {
        final ApparatusPanel ap = new ApparatusPanel();
        ap.addGraphicsSetup( new BasicGraphicsSetup() );
        JFrame frame = new JFrame( "Test Fast Paint" );
        frame.setContentPane( ap );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );


        Shape r2 = new Rectangle( 200, 60, 80, 90 );
        ShapeGraphic g = new ShapeGraphic( r2, Color.red );

        ap.addGraphic( g );
        g.setFillPaint( Color.green );
        final ArrayList fastShapes = new ArrayList();
        final Random random = new Random();
        for( int i = 0; i < 2000; i++ ) {
            //            Shape shape = new Rectangle( random.nextInt( 600 ), random.nextInt( 600 ), 3, 3 );
            //            Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
            //            FastPaintShapeGraphic fastPaintShapeGraphic = new FastPaintShapeGraphic( shape, color, ap );
            FastPaintImageGraphic fastPaintImageGraphic = new FastPaintImageGraphic( ImageLoader.loadBufferedImage( "images/Phet-logo-48x48.gif" ), ap );
            fastPaintImageGraphic.setLocation( random.nextInt( 600 ),
                                               random.nextInt( 600 ) );
            fastShapes.add( fastPaintImageGraphic );
            ap.addGraphic( fastPaintImageGraphic );
        }

        Runnable r = new Runnable() {
            public void run() {
                int x = 0;
                int y = 0;
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                        FastPaintImageGraphic g = (FastPaintImageGraphic)fastShapes.get( fastShapes.size() - 1 );
                        //                        Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
                        //                        g.setFillPaint( color );
                        g.setLocation( new Point( x++ % 600, ( y += 2 ) % 600 ) );
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
