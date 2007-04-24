package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.energyskatepark.model.SPoint2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 12:09:59 PM
 */
public abstract class ControlPointParametricFunction2D extends ParametricFunction2D {
    private SPoint2D[] pts;

    public ControlPointParametricFunction2D( SPoint2D[] pts ) {
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

    public Object clone() {
        try {
            ControlPointParametricFunction2D cppf2d = (ControlPointParametricFunction2D)super.clone();
            cppf2d.pts = new SPoint2D[pts.length];
            for( int i = 0; i < pts.length; i++ ) {
                cppf2d.pts[i] = (SPoint2D)pts[i].clone();
            }
            return cppf2d;
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public void setControlPoints( SPoint2D[] pts ) {
        this.pts = pts;
    }

    public SPoint2D[] getControlPoints() {
        SPoint2D[] a = new SPoint2D[pts.length];
        for( int i = 0; i < a.length; i++ ) {
            a[i] = new SPoint2D( pts[i].getX(), pts[i].getY() );
        }
        return a;
    }

    public void setControlPoint( int i, SPoint2D pt ) {
        pts[i] = new SPoint2D( pt.getX(), pt.getY() );
    }

    public int getNumControlPoints() {
        return pts.length;
    }


    public void translateControlPoints( double dx, double dy ) {
        for( int i = 0; i < pts.length; i++ ) {
            SPoint2D pt = pts[i];
            pt.setLocation( pt.getX() + dx, pt.getY() + dy );
        }
    }

    public void translateControlPoint( int index, double width, double height ) {
        pts[index].setLocation( pts[index].getX() + width, pts[index].getY() + height );
    }

    public void removeControlPoint( int index ) {
        ArrayList list = new ArrayList( Arrays.asList( pts ) );
        list.remove( index );
        setControlPoints( (SPoint2D[])list.toArray( new SPoint2D[list.size()] ) );
    }

    public int numControlPoints() {
        return pts.length;
    }

    public SPoint2D controlPointAt( int i ) {
        return pts[i];
    }
}
