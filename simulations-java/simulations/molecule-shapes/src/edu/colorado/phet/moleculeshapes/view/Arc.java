// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Displays an arc between two directions at a specific radius. Used JME3 Curve code as a template
 */
public class Arc extends Mesh {
    public Arc( Vector3f startDir, Vector3f endDir, float radius, int numSegments ) {
        int numVertices = numSegments + 1;

        // figure out the quaternion rotation from start to end
        Quaternion startToEnd = getRotationQuaternion( startDir, endDir );

        float[] array = new float[numVertices * 3];
        short[] indices = new short[numSegments * 2];

        int i;

        for ( i = 0; i < array.length; i += 3 ) {
            float ratio = ( (float) i ) / ( array.length - 3 ); // zero to 1

            // spherical linear interpolation (slerp) from start to end
            Vector3f position = new Quaternion().slerp( Quaternion.IDENTITY, startToEnd, ratio ).mult( startDir ).mult( radius );

            array[i] = position.x;
            array[i + 1] = position.y;
            array[i + 2] = position.z;
        }

        i = 0;
        int k = 0;
        for ( int j = 0; j < numSegments; j++ ) {
            k = j;
            indices[i] = (short) k;
            i++;
            k++;
            indices[i] = (short) k;
            i++;
        }

        this.setMode( Mesh.Mode.Lines );
        this.setBuffer( VertexBuffer.Type.Position, 3, array );
        this.setBuffer( VertexBuffer.Type.Index, 2, indices );//(spline.getControlPoints().size() - 1) * nbSubSegments * 2
        this.updateBound();
        this.updateCounts();
    }

    public static Quaternion getRotationQuaternion( Vector3f startDir, Vector3f endDir ) {
        Matrix3f rotationMatrix = new Matrix3f();
        LonePairNode.fromStartEndVectors( rotationMatrix, MoleculeJMEApplication.vectorConversion( startDir ), MoleculeJMEApplication.vectorConversion( endDir ) );
        return new Quaternion().fromRotationMatrix( rotationMatrix );
    }

    public static Vector3f slerp( Vector3f start, Vector3f end, float ratio ) {
        // assumes normalized. TODO doc
        return new Quaternion().slerp( Quaternion.IDENTITY, getRotationQuaternion( start, end ), ratio ).mult( start );
    }
}
