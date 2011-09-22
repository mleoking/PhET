// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.util;

import java.text.DecimalFormat;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 * Utility for rotating 3D points from a mol2 file, using a multi-step rotation.
 * Rotation about one axis is performed at a time.
 * This makes it easier to visualize and describe the desired transform.
 * <p/>
 * This utility was used in molecule-polarity to change the initial orientation
 * of atoms in mol2 files. It may be needed for future maintenance on that sim.
 * It was not located under molecule-polarity/src because that sim has no other
 * dependencies on JME.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RotateMol2 {

    public static void main( String[] args ) {

        // manually put (x,y,z) coordinates of the points you want to rotate here
        //TODO would be nice to read the mol2 file, instead of having to copy-paste coordinates
        Vector3f[] vectors = new Vector3f[] {
                new Vector3f( 0.000000000f, 0.000000000f, 0.338383767f ),
                new Vector3f( 1.257790333f, 0.000000000f, -0.129111197f ),
                new Vector3f( -0.628895167f, -1.089278363f, -0.129111197f ),
                new Vector3f( -0.628895167f, 1.089278363f, -0.129111197f ),
                new Vector3f( 0.000000000f, 0.000000000f, 1.431213754f )
        };

        // describe the rotation steps here, one axis at a time
        Transform[] transforms = new Transform[] {
                new Transform( new Quaternion( new float[] { (float) Math.toRadians( -90 ), 0, 0 } ) ),
                new Transform( new Quaternion( new float[] { 0, (float) Math.toRadians( 10 ), 0 } ) ),
                new Transform( new Quaternion( new float[] { (float) Math.toRadians( 25 ), 0, 0 } ) ),
        };

        // apply transforms and print new coordinates, use these to modify mol2 file manually
        //TODO would be nice to write out the modified mol2 file, instead of having to copy-paste this output
        DecimalFormat format = new DecimalFormat( "0.000000000" );
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
