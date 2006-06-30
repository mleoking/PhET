/** Sam Reid*/
package edu.colorado.phet.cck3.tests;

import edu.colorado.phet.cck3.common.AffineTransformUtil;
import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 8:49:03 AM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class TestTranformUtil {
    static AffineTransform testTrf;
    static double testAngle = 0;

    public static void main( String[] args ) throws IOException {

        final BufferedImage image = ImageLoader.loadBufferedImage( "images/flame.gif" );
        final Rectangle2D.Double src = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );
//        final Rectangle2D.Double dst = new Rectangle2D.Double( 150, 150, 180, 80 );
        final Rectangle2D.Double dst = new Rectangle2D.Double( 150, 150, image.getWidth() / 2, image.getHeight() / 2 );

        final AffineTransform t = new AffineTransform();

        final JPanel panel = new JPanel() {
            protected void paintComponent( Graphics g ) {
                if( testTrf != null ) {
                    super.paintComponent( g );
                    Graphics2D g2 = (Graphics2D)g;
                    g2.drawRenderedImage( image, AffineTransformUtil.getTransform( src, dst, 0 ) );
                    g2.drawRenderedImage( image, testTrf );

                    g2.setColor( Color.blue );
                    g2.draw( dst );
                }
            }
        };
        JFrame jf = new JFrame();
        jf.setSize( 400, 400 );
        jf.setContentPane( panel );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setVisible( true );

        Rectangle r = t.createTransformedShape( src ).getBounds();
        System.out.println( "r = " + r );
        SwingTimerClock timer = new SwingTimerClock( 1, 30, true );
        timer.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                testAngle += Math.PI / 40 * dt;
                System.out.println( "testAngle = " + testAngle );
                testTrf = AffineTransformUtil.getTransform( src, dst, testAngle );
                panel.repaint();
            }
        } );
        timer.start();
    }
}
