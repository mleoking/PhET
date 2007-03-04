package edu.colorado.phet.ec3.test.phys1d;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 3, 2007
 * Time: 11:55:22 PM
 * Copyright (c) Mar 3, 2007 by Sam Reid
 */

public class CubicSpline2D extends ParametricFunction2D {
    private CubicSpline x;
    private CubicSpline y;
    private Point2D[] pts;
    private ArrayList listeners = new ArrayList();

    public CubicSpline2D( Point2D[] pts ) {
        this.pts = pts;
        update();
    }

    public void setControlPoints( Point2D[] pts ) {
        this.pts = pts;
        update();
    }

    public void translateControlPoint( int i, double dx, double dy ) {
        setControlPoint( i, new Point2D.Double( pts[i].getX() + dx, pts[i].getY() + dy ) );
    }

    public Point2D getControlPoint( int i ) {
        return new Point2D.Double( pts[i].getX(), pts[i].getY() );
    }

    public void setControlPoint( int i, Point2D pt ) {
        pts[i] = new Point2D.Double( pt.getX(), pt.getY() );
        update();
    }

    private void update() {
        double[] s = new double[pts.length];
        double[] x = new double[pts.length];
        double[] y = new double[pts.length];
        for( int i = 0; i < pts.length; i++ ) {
            s[i] = ( 1.0 / ( pts.length - 1 ) ) * i;//*2.0;
            x[i] = pts[i].getX();
            y[i] = pts[i].getY();
        }
        s[s.length - 1] = Math.round( s[s.length - 1] );//to be exact, in case of roundoff error
        this.x = CubicSpline.interpolate( s, x );
        this.y = CubicSpline.interpolate( s, y );
        notifyTrackChanged();
    }

    public int getNumControlPoints() {
        return pts.length;
    }

    public String toStringSerialization() {
        String a = "new Point2D.Double[]{";
        for( int i = 0; i < pts.length; i++ ) {
            a += "new Point2D.Double(" + pts[i].getX() + ", " + pts[i].getY() + ")";
            if( i < pts.length - 1 ) {
                a += ", ";
            }
        }
        return a + "};";
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyTrackChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.trackChanged();
        }
    }

    public Point2D[] getControlPoints() {
        Point2D[] a = new Point2D[pts.length];
        for( int i = 0; i < a.length; i++ ) {
            a[i] = new Point2D.Double( pts[i].getX(), pts[i].getY() );
        }
        return a;
    }

    public Point2D evaluate( double alpha ) {
        return new Point2D.Double( x.evaluate( alpha ), y.evaluate( alpha ) );
    }

    public static void main( String[] args ) {
        ParametricFunction2D parametricFunction2D = new CubicSpline2D( new Point2D[]{
                new Point2D.Double( 0, 0 ),
                new Point2D.Double( 1, 1 ),
                new Point2D.Double( 2, 0 )
        } );
        for( double s = 0.0; s < 1.0; s += 0.1 ) {
            Point2D at = parametricFunction2D.evaluate( s );
            System.out.println( "s = " + s + ", at=" + at );
        }
        double delta = parametricFunction2D.getMetricDelta( 0, 1 );
        System.out.println( "Spline length=" + delta );

        double fracDist = parametricFunction2D.getFractionalDistance( 0, delta );
        System.out.println( "fracDist = " + fracDist );

    }

}
