package edu.colorado.phet.ec3.test.spline;

import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.ec3.model.spline.NatCubicSpline2D;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 3:13:02 PM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class TestCubicSplineDistance {
    private JFrame frame;
    private Timer timer;
    private double x = 0;
    private NatCubicSpline2D natCubicSpline2D;
    private PNode character;
    private PhetPPath mousecharacter;

    public TestCubicSplineDistance() {
        natCubicSpline2D = new NatCubicSpline2D( new Point2D[]{
                new Point2D.Double( 100, 100 ),
                new Point2D.Double( 200, 100 ),
                new Point2D.Double( 200, 200 ),
                new Point2D.Double( 400, 400 )
        } );
        frame = new JFrame();
        final PhetPCanvas pCanvas = new PhetPCanvas();
        pCanvas.addScreenChild( new NatCubicSplineGraphic( natCubicSpline2D ) );

        frame.setContentPane( pCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );

        character = new PhetPPath( new Rectangle( 0, 0, 2, 2 ), Color.red );
        pCanvas.addScreenChild( character );

        mousecharacter = new PhetPPath( new Rectangle( 0, 0, 4, 4 ), Color.green );
        pCanvas.addScreenChild( mousecharacter );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
                pCanvas.repaint();
            }
        } );
        pCanvas.addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
            }

            public void mouseMoved( MouseEvent e ) {
                mousecharacter.setOffset( natCubicSpline2D.evaluate( getDistAlongSpline( e.getPoint() ) ) );
                update();
            }
        } );
    }

    private double getDistAlongSpline( Point2D pt ) {
        double best = Double.POSITIVE_INFINITY;
        double scalar = 0.0;
        int N = 1000;
        for( int i = 0; i < N; i++ ) {
            double alongSpline = i / ( (double)N ) * natCubicSpline2D.getLength();
            Point2D loc = natCubicSpline2D.evaluate( alongSpline );
            double dist = pt.distance( loc );
            if( dist < best ) {
                best = dist;
                scalar = alongSpline;
            }
        }
        System.out.println( "found best=" + scalar + ", score=" + best );
        return scalar;
    }

    static class NatCubicSplineGraphic extends PhetPNode {
        private NatCubicSpline2D natCubicSpline2D;

        public NatCubicSplineGraphic( NatCubicSpline2D natCubicSpline2D ) {
            this.natCubicSpline2D = natCubicSpline2D;
            for( int i = 0; i < natCubicSpline2D.getControlPoints().length; i++ ) {
                Point2D pt = natCubicSpline2D.getControlPoints()[i];
                addChild( new PhetPPath( new Rectangle2D.Double( pt.getX(), pt.getY(), 2, 2 ), Color.blue ) );
            }
            addChild( new PhetPPath( toGeneralPath( natCubicSpline2D ), new BasicStroke( 2 ), Color.yellow ) );
        }
    }

    private static GeneralPath toGeneralPath( NatCubicSpline2D cubicSpline ) {
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
        character.setOffset( natCubicSpline2D.evaluate( x ) );
    }

    public static void main( String[] args ) {
        new TestCubicSplineDistance().start();
    }

    private void start() {
        frame.show();
        timer.start();
    }
}
