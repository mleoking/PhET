// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.spline;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 12:09:59 PM
 */
public abstract class ControlPointParametricFunction2D extends ParametricFunction2D {
    private SerializablePoint2D[] pts;

    public ControlPointParametricFunction2D( SerializablePoint2D[] pts ) {
        this.pts = pts;
    }

    public boolean equals( Object obj ) {
        if( obj instanceof ControlPointParametricFunction2D ) {
            ControlPointParametricFunction2D c = (ControlPointParametricFunction2D)obj;
            return Arrays.equals( pts, c.pts );
        }
        else {
            return false;
        }
    }

    public void setControlPoints( SerializablePoint2D[] pts ) {
        this.pts = pts;
    }

    public SerializablePoint2D[] getControlPoints() {
        SerializablePoint2D[] a = new SerializablePoint2D[pts.length];
        for( int i = 0; i < a.length; i++ ) {
            a[i] = new SerializablePoint2D( pts[i].getX(), pts[i].getY() );
        }
        return a;
    }

    public void setControlPoint( int i, SerializablePoint2D pt ) {
        pts[i] = new SerializablePoint2D( pt.getX(), pt.getY() );
    }

    public int getNumControlPoints() {
        return pts.length;
    }


    public void translateControlPoints( double dx, double dy ) {
        for( int i = 0; i < pts.length; i++ ) {
            SerializablePoint2D pt = pts[i];
            pt.setLocation( pt.getX() + dx, pt.getY() + dy );
        }
    }

    public void translateControlPoint( int index, double width, double height ) {
        pts[index].setLocation( pts[index].getX() + width, pts[index].getY() + height );
    }

    public void removeControlPoint( int index ) {
        ArrayList list = new ArrayList( Arrays.asList( pts ) );
        list.remove( index );
        setControlPoints( (SerializablePoint2D[])list.toArray( new SerializablePoint2D[list.size()] ) );
    }

    public int numControlPoints() {
        return pts.length;
    }

    public SerializablePoint2D controlPointAt( int i ) {
        return pts[i];
    }
}
