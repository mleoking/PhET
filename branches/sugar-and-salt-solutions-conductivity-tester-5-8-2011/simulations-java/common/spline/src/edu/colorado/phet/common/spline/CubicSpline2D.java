// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.spline;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

/**
 * User: Sam Reid
 * Date: Mar 3, 2007
 * Time: 11:55:22 PM
 */

public class CubicSpline2D extends ControlPointParametricFunction2D {
    private CubicSpline x;
    private CubicSpline y;

    private ArrayList listeners = new ArrayList();

    public CubicSpline2D( SerializablePoint2D[] pts ) {
        super( pts );
        update();
    }

    public boolean equals( Object obj ) {
        if ( !super.equals( obj ) ) {
            return false;
        }
        else {
            if ( obj instanceof CubicSpline2D ) {
                CubicSpline2D cubicSpline2D = (CubicSpline2D) obj;
                return cubicSpline2D.x.equals( this.x ) && cubicSpline2D.y.equals( this.y );
            }
            else {
                return false;
            }
        }
    }

    public void setControlPoints( SerializablePoint2D[] pts ) {
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

    public SerializablePoint2D getControlPoint( int i ) {
        SerializablePoint2D[] pts = getControlPoints();
        return new SerializablePoint2D( pts[i].getX(), pts[i].getY() );
    }

    public void setControlPoint( int i, SerializablePoint2D pt ) {
        super.setControlPoint( i, pt );
        update();
    }


    public void translateControlPoints( double dx, double dy ) {
        if ( dx != 0 || dy != 0 ) {
            super.translateControlPoints( dx, dy );
            update();
            notifyTrackChanged();
        }
    }

    private void update() {
        SerializablePoint2D[] pts = getControlPoints();
        double[] s = new double[pts.length];
        double[] x = new double[pts.length];
        double[] y = new double[pts.length];
        for ( int i = 0; i < pts.length; i++ ) {
            s[i] = ( 1.0 / ( pts.length - 1 ) ) * i;//*2.0;
            x[i] = pts[i].getX();
            y[i] = pts[i].getY();
        }
        s[s.length - 1] = Math.round( s[s.length - 1] );//to be exact, in case of roundoff error
        this.x = CubicSpline.interpolate( s, x );
        this.y = CubicSpline.interpolate( s, y );
        notifyTrackChanged();
    }

    public String toString() {
        return toStringSerialization();
    }

    public String toStringSerialization() {
        SerializablePoint2D[] pts = getControlPoints();
        String a = "new SPoint2D[]{";
        for ( int i = 0; i < pts.length; i++ ) {
            a += "new SPoint2D(" + pts[i].getX() + ", " + pts[i].getY() + ")";
            if ( i < pts.length - 1 ) {
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

    private void notifyTrackChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.trackChanged();
        }
    }


    public SerializablePoint2D evaluate( double alpha ) {
        return new SerializablePoint2D( x.evaluate( alpha ), y.evaluate( alpha ) );
    }

    public static void main( String[] args ) {
        ParametricFunction2D parametricFunction2D = new CubicSpline2D( new SerializablePoint2D[] {
                new SerializablePoint2D( 0, 0 ),
                new SerializablePoint2D( 1, 1 ),
                new SerializablePoint2D( 2, 0 )
        } );
        for ( double s = 0.0; s < 1.0; s += 0.1 ) {
            SerializablePoint2D at = parametricFunction2D.evaluate( s );
            System.out.println( "s = " + s + ", at=" + at );
        }
        double delta = parametricFunction2D.getMetricDelta( 0, 1 );
        System.out.println( "Spline length=" + delta );

        double fracDist = parametricFunction2D.getFractionalDistance( 0, delta );
        System.out.println( "fracDist = " + fracDist );

    }

}
