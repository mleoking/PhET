// Copyright 2002-2011, University of Colorado
package com.pixelzoom.util;

import java.text.DecimalFormat;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 * Utility for rotating 3D points using a multi-step rotation.
 * Rotation about one axis is performed at a time.
 * This makes it easier to visualize and describe the desired transform.
 * <p/>
 * Used in molecule-polarity to change the initial orientation of atoms in mol2 files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RotatePoints3D {

    public static void main( String[] args ) {

        // put (x,y,z) coordinates of the points you want to rotate here
        Vector3f[] vectors = new Vector3f[] {
                new Vector3f( 0.000000000f, 0.000000000f, 0.471084213f ),
                new Vector3f( 1.705662780f, 0.000000000f, -0.067122716f ),
                new Vector3f( -0.852831390f, -1.477147271f, -0.067122716f ),
                new Vector3f( -0.852831390f, 1.477147271f, -0.067122716f ),
                new Vector3f( 0.000000000f, 0.000000000f, 1.556696597f )
        };

        // describe the rotation steps here, one axis at a time
        Transform[] transforms = new Transform[] {
                new Transform( new Quaternion( new float[] { (float) Math.toRadians( -90 ), 0, 0 } ) ),
                new Transform( new Quaternion( new float[] { 0, (float) Math.toRadians( 10 ), 0 } ) ),
                new Transform( new Quaternion( new float[] { (float) Math.toRadians( 25 ), 0, 0 } ) ),
        };

        // apply transforms and print new coordinates
        DecimalFormat format = new DecimalFormat( "0.000000000" ); // ala Spartan mol2 files
        for ( Vector3f vector : vectors ) {
            Vector3f result = new Vector3f( vector );
            for ( Transform transform : transforms ) {
                result = transform.transformVector( result, null );
            }
            System.out.println( format.format( result.getX() ) + " " +
                                format.format( result.getY() ) + " " +
                                format.format( result.getZ() ) );
        }
    }
}
