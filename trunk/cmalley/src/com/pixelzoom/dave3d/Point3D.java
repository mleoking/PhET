/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package com.pixelzoom.dave3d;

/**
 * Point3D is a point in 3-D space.
 */
public class Point3D {

    private double x, y, z;

    public Point3D() {
        this( 0, 0, 0 );
    }
    
    public Point3D( double x, double y, double z ) {
        setLocation( x, y, z );
    }

    public void setLocation( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String toString() {
        return getClass().getName() + "=[" + x + "," + y + "," + z + "]";
    }
}
