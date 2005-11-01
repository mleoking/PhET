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
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TestFastPaintInteraction {
    private static int imX = 300;
    private static int imY = 300;

    public static void main( String[] args ) throws InterruptedException, IOException {

        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BorderLayout() );

        final ApparatusPanel ap = new TestBufferedAP.BufferedApparatusPanel();
        ap.addGraphicsSetup( new BasicGraphicsSetup() );
        JFrame frame = new JFrame( "Test Fast Paint" );
        frame.setContentPane( contentPane );
        contentPane.add( ap, BorderLayout.CENTER );

        final JCheckBox checkBox = new JCheckBox( "Test Fast Paint", false );

        contentPane.add( checkBox, BorderLayout.NORTH );

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

        final FastPaintImageGraphic fastPaintImageGraphic = new FastPaintImageGraphic( ImageLoader.loadBufferedImage( "images/help-item-icon.gif" ), ap );
        fastPaintImageGraphic.setLocation( imX,
                                           imY );
        fastShapes.add( fastPaintImageGraphic );


        DefaultInteractiveGraphic defaultInteractiveGraphic = new DefaultInteractiveGraphic( fastPaintImageGraphic );
        defaultInteractiveGraphic.addCursorHandBehavior();
        defaultInteractiveGraphic.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                imX += dx;
                imY += dy;
                fastPaintImageGraphic.setLocation( imX, imY );
                if( !checkBox.isSelected() ) {
                    ap.repaint();
                }

            }
        } );

        ap.addGraphic( defaultInteractiveGraphic );

        final FastPaintImageGraphic fastPaintImageGraphic2 = new FastPaintImageGraphic( ImageLoader.loadBufferedImage( "images/help-item-icon.gif" ), ap );
        fastPaintImageGraphic.setLocation( imX,
                                           imY );
        ap.addGraphic( fastPaintImageGraphic2 );
        Runnable r = new Runnable() {
            public void run() {
                int x = 0;
                int y = 0;
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                        FastPaintImageGraphic g = fastPaintImageGraphic2;
                        //                        Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
                        //                        g.setFillPaint( color );
                        g.setLocation( new Point( x++ % 600, ( y += 2 ) % 600 ) );
                        if( !checkBox.isSelected() ) {
                            ap.repaint();
                        }
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
