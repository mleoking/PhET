/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.spline;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 26, 2003
 * Time: 1:41:26 PM
 * Copyright (c) Jul 26, 2003 by Sam Reid
 */
public class FloatPolygon {
    ArrayList pts = new ArrayList();

    public float getX( int i ) {
        return pointAt( i ).x;
    }

    public float getY( int i ) {
        return pointAt( i ).y;
    }

    private Point2D.Float pointAt( int i ) {
        return (Point2D.Float)pts.get( i );
    }

    public void addPoint( float x, float y ) {
        pts.add( new Point2D.Float( x, y ) );
    }

    public int numPoints() {
        return pts.size();
    }

    public float[] getXPoints() {
        float[] f = new float[pts.size()];
        for( int i = 0; i < f.length; i++ ) {
            f[i] = getX( i );
        }
        return f;
    }

    public float[] getYPoints() {
        float[] f = new float[pts.size()];
        for( int i = 0; i < f.length; i++ ) {
            f[i] = getY( i );
        }
        return f;
    }
}
