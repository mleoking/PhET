/**
 * Class: TestFastPaintShapes
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 19, 2004
 */
package edu.colorado.phet.common_cck.examples.fastpaint;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TestFastPaintShapes {
    public static void main( String[] args ) throws InterruptedException {
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
        for( int i = 0; i < 10000; i++ ) {
            Shape shape = new Rectangle( random.nextInt( 600 ), random.nextInt( 600 ), 10, 10 );
            Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
            FastPaintShapeGraphic fastPaintShapeGraphic = new FastPaintShapeGraphic( shape, color, ap );
            fastShapes.add( fastPaintShapeGraphic );
            ap.addGraphic( fastPaintShapeGraphic );
        }

        Runnable r = new Runnable() {
            public void run() {
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                        int elm = random.nextInt( fastShapes.size() );
                        FastPaintShapeGraphic g = (FastPaintShapeGraphic)fastShapes.get( elm );
                        Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
                        g.setFillPaint( color );
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
