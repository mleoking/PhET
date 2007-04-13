package edu.colorado.phet.energyskatepark.model.physics;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 3, 2007
 * Time: 11:55:22 PM
 * Copyright (c) Mar 3, 2007 by Sam Reid
 */

public class CubicSpline2D extends ControlPointParametricFunction2D {
    private CubicSpline x;
    private CubicSpline y;

    private ArrayList listeners = new ArrayList();

    public CubicSpline2D( Point2D[] pts ) {
        super( pts );
        update();
    }

    public Object clone() {
        CubicSpline2D clone = (CubicSpline2D)super.clone();
        try {
            clone.x = (CubicSpline)this.x.clone();
            clone.y = (CubicSpline)this.y.clone();
            return clone;
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public boolean equals( Object obj ) {
        if( !super.equals( obj ) ) {
            return false;
        }
        else {
            if( obj instanceof CubicSpline2D ) {
                CubicSpline2D cubicSpline2D = (CubicSpline2D)obj;
                return cubicSpline2D.x.equals( this.x ) && cubicSpline2D.y.equals( this.y );
            }
            else {
                return false;
            }
        }
    }

    public void setControlPoints( Point2D[] pts ) {
        super.setControlPoints( pts );
        update();
    }

    public void translateControlPoint( int i, double dx, double dy ) {
        super.translateControlPoint( i, dx, dy );
        update();
    }

    public void removeControlPoint( int index ) {
        super.removeControlPoint( index );
        update();
    }

    public Point2D getControlPoint( int i ) {
        Point2D[] pts = getControlPoints();
        return new Point2D.Double( pts[i].getX(), pts[i].getY() );
    }

    public void setControlPoint( int i, Point2D pt ) {
        super.setControlPoint( i, pt );
        update();
    }


    public void translateControlPoints( double dx, double dy ) {
        super.translateControlPoints( dx, dy );
        update();
    }

    private void update() {
        Point2D[] pts = getControlPoints();
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

    public String toStringSerialization() {
        Point2D[] pts = getControlPoints();
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
