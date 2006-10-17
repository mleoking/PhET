package edu.colorado.phet.ec3.test.spline;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.ec3.model.spline.CubicSpline;

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
    private CubicSpline cubicSpline;
    private double x = 0;

    public TestCubicSpline() {
        cubicSpline = new CubicSpline( 10 );
        cubicSpline.addControlPoint( 100, 100 );
        cubicSpline.addControlPoint( 200, 100 );
        cubicSpline.addControlPoint( 200, 200 );

        frame = new JFrame();
        final JPanel panel = new JPanel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint( Color.black );
                GeneralPath path = toGeneralPath( cubicSpline );
//                g2.draw( cubicSpline.getInterpolationPath() );
                g2.draw( path );
                finishPaint( g2 );
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

    private GeneralPath toGeneralPath( CubicSpline cubicSpline ) {
        DoubleGeneralPath generalPath = new DoubleGeneralPath( cubicSpline.evaluateAnalytical( 0 ) );
        double dx = 0.01;
        for( double x = dx; x <= 2.0; x += dx ) {
            generalPath.lineTo( cubicSpline.evaluateAnalytical( x ) );
        }
        return generalPath.getGeneralPath();
    }

    private void update() {
        x += 0.01;
    }

    private void finishPaint( Graphics2D g2 ) {
        Point2D pt = cubicSpline.evaluateAnalytical( x );
        g2.setPaint( Color.red );
        g2.fill( new Rectangle2D.Double( pt.getX(), pt.getY(), 5, 5 ) );
        System.out.println( "x=" + x + ", pt = " + pt );
    }

    public static void main( String[] args ) {
        new TestCubicSpline().start();
    }

    private void start() {
        frame.show();
        timer.start();
    }
}
