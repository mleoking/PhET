package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * Jul 17, 2007, 12:15:12 AM
 */
public class CircularRegression {
    JFrame frame = new JFrame();
    private PhetPPath circlePath;
    private PNode pointLayer = new PNode();
    private Timer timer;


    public CircularRegression() {
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final PhetPCanvas pane = new PhetPCanvas();
        pane.getLayer().addChild( pointLayer );
        pane.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                addPoint( e.getPoint() );
            }

            public void mouseReleased( MouseEvent e ) {
                pointLayer.removeAllChildren();
                updateCircle();
            }
        } );
        pane.addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                addPoint( e.getPoint() );
            }
        } );
        frame.setContentPane( pane );
        circlePath = new PhetPPath( new BasicStroke( 2 ), Color.red );
        pane.getLayer().addChild( circlePath );
    }

    private void addPoint( Point pt ) {
        PhetPPath path = new PhetPPath( new Ellipse2D.Double( pt.getX() - 2, pt.getY() - 2,
                                                              4, 4 ), Color.blue, new BasicStroke( 1 ), Color.black );
        pointLayer.addChild( path );
        updateCircle();
    }

    static class Circle {
        double x;
        double y;
        double r;

        public Circle( double x, double y, double r ) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        public Shape toEllipse() {
            return new Ellipse2D.Double( x - r, y - r, r * 2, r * 2 );
        }
    }

    int time = 0;

    private void updateCircle() {
        //To change body of created methods use File | Settings | File Templates.
        if( timer != null ) {
            timer.stop();
            timer = null;
        }
        time = 0;
        timer = new Timer( 5, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Circle circle = CircularRegression.getCircle( getPoints(), time );
                circlePath.setPathTo( circle.toEllipse() );
                time++;
                if( time >= 10000 ) {
                    timer.stop();
                    timer = null;
                }
            }
        } );
        timer.start();
    }

    private static Circle getCircle( Point2D[] points, int numIt ) {
//        Circle initialState = new Circle(points[points.length-1].getX(), points[points.length-1].getY(), 50);
//        double[] state = new double[]{points[points.length - 1].getX(), points[points.length - 1].getY(), 50};
        double[] state = new double[]{avgX( points ), avgY( points ), 100};// points[points.length - 1].getY(), 50};
        for( int i = 0; i < numIt; i++ ) {
            state = update( state, points );
        }
        return new Circle( state[0], state[1], state[2] );
//        return initialState;
    }

    private static double avgX( Point2D[] points ) {
        double sum = 0;
        for( int i = 0; i < points.length; i++ ) {
            Point2D point = points[i];
            sum += point.getX();
        }
        return sum / points.length;
    }

    private static double avgY( Point2D[] points ) {
        double sum = 0;
        for( int i = 0; i < points.length; i++ ) {
            Point2D point = points[i];
            sum += point.getY();
        }
        return sum / points.length;
    }

    private static double[] update( double[] state, Point2D[] points ) {
        double[] state2 = new double[state.length];
        for( int i = 0; i < state2.length; i++ ) {
            state2[i] = state[i] + alpha * numgrad( i, state, points );

        }
//        System.out.println("state2 = " + state2[2]);
        return state2;
    }

    static double alpha = 0.01;
    private static double epsilon = 1E-4;

    private static double numgrad( int index, double[] state, Point2D[] points ) {
        state[index] += epsilon;
        double err1 = getError( state, points );
        state[index] += -2 * epsilon;
        double err2 = getError( state, points );
        state[index] += epsilon;
        return ( err2 - err1 ) / ( 2 * epsilon );
    }

    private static double getError( double[] state, Point2D[] points ) {
        double sum = 0;
        for( int i = 0; i < points.length; i++ ) {
            sum += distance(state,points[i]);
        }
        return sum;
        
    }

    private static double distance( double[] state, Point2D point ) {
        double distToCenter = point.distance( state[0], state[1] );
        if ( distToCenter >state[2]){
            return distToCenter-state[2];
        }else{
            return state[2]-distToCenter;
        }
    }

    private static double grad( int index, double[] state, Point2D[] points ) {
        double sum = 0;
        if( index == 0 ) {
            for( int i = 0; i < points.length; i++ ) {
                sum += -1 * ( points[i].getX() - state[0] ) * ( points[i].distance( state[0], state[1] ) );
            }
        }
        else if( index == 1 ) {
            for( int i = 0; i < points.length; i++ ) {
                sum += -1 * ( points[i].getY() - state[1] ) * ( points[i].distance( state[0], state[1] ) );
            }
        }
        else if( index == 2 ) {
            for( int i = 0; i < points.length; i++ ) {
                sum += -1;//*points.length;// * (points[i].getX() - state[0]) * (points[i].distance(state[0], state[1]));
            }
        }
        return sum;
    }

    private Point2D[] getPoints() {
        Point2D[] pts = new Point2D[pointLayer.getChildrenCount()];
        for( int i = 0; i < pts.length; i++ ) {
            pts[i] = pointLayer.getChild( i ).getFullBounds().getCenter2D();
        }
        return pts;
    }

    public static void main( String[] args ) {
        new CircularRegression().start();
    }

    private void start() {
        frame.setSize( 800, 600 );
        frame.setVisible( true );
        frame.getContentPane().requestFocus();

    }
}
