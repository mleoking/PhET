// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.wireframe;

/**
 * Vertex3D is a mutable point in 3D space where 2 or more lines meet.
 */
public class Vertex3D {

    private float _x;
    private float _y;
    private float _z;

    public Vertex3D( float x, float y, float z ) {
        setLocation( x, y, z );
    }
    
    public Vertex3D() {
        this( 0, 0, 0 );
    }

    public void setLocation( float x, float y, float z ) {
        _x = x;
        _y = y;
        _z = z;
    }
    
    public float getX() {
        return _x;
    }
    
    public float getY() {
        return _y;
    }
    
    public float getZ() {
        return _z;
    }
}