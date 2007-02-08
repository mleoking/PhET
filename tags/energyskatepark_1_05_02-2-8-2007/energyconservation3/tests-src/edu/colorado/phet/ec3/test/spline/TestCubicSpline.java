package edu.colorado.phet.ec3.test.spline;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.ec3.model.spline.NatCubicSpline2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 3:13:02 PM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class TestCubicSpline {
    private JFrame frame;
    private Timer timer;
    private double x = 0;
    private NatCubicSpline2D natCubicSpline2D;

    public TestCubicSpline() {
        natCubicSpline2D = new NatCubicSpline2D( new Point2D[]{
                new Point2D.Double( 100, 100 ),
                new Point2D.Double( 200, 100 ),
                new Point2D.Double( 200, 200 ),
                new Point2D.Double( 400, 400 )
        } );
        frame = new JFrame();
        final JPanel panel = new JPanel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint( Color.black );
                GeneralPath path = toGeneralPath( natCubicSpline2D );
                g2.draw( path );
                finishPaint( g2 );
                g2.setPaint( Color.blue );
                g2.fillRect( 100, 100, 2, 2 );
                g2.setPaint( Color.green );
                g2.fillRect( 200, 100, 2, 2 );
                g2.setPaint( Color.yellow );
                g2.fillRect( 200, 200, 2, 2 );
            }
        };
        frame.setContentPane( panel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
                panel.invalidate();
                panel.revalidate();
                panel.repaint();
            }
        } );
    }

    private GeneralPath toGeneralPath( NatCubicSpline2D cubicSpline ) {
        DoubleGeneralPath generalPath = new DoubleGeneralPath( cubicSpline.evaluate( 0 ) );
        double dx = 0.01;
        for( double x = dx; x <= cubicSpline.getLength(); x += dx ) {
            generalPath.lineTo( cubicSpline.evaluate( x ) );
        }
        return generalPath.getGeneralPath();
    }

    private void update() {
        x += 0.01;
        if( x > natCubicSpline2D.getLength() ) {
            x = 0;
        }
    }

    private void finishPaint( Graphics2D g2 ) {
        Point2D pt = natCubicSpline2D.evaluate( x );
        g2.setPaint( Color.red );
        g2.fill( new Rectangle2D.Double( pt.getX(), pt.getY(), 5, 5 ) );
//        System.out.println( "x=" + x + ", pt = " + pt );
    }

    public static void main( String[] args ) {
        new TestCubicSpline().start();
    }

    private void start() {
        frame.show();
        timer.start();
    }
}
