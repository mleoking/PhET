// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Displays the area drawn out by an arc (imagine a line connected to the origin rotating from A to B)
 */
public class Sector extends Mesh {
    public Sector( Vector3f startDir, Vector3f endDir, float radius, int numTriangles ) {
        int numEdgeVertices = numTriangles + 1;
        int numVertices = numEdgeVertices + 1;

        // figure out the quaternion rotation from start to end
        Quaternion startToEnd = Arc.getRotationQuaternion( startDir, endDir );

        float[] array = new float[numVertices * 3]; // +1 for origin
        short[] indices = new short[numTriangles * 2 + 1];

        array[0] = array[1] = array[2] = 0; // origin

        int i;
        for ( i = 3; i < array.length - 3; i += 3 ) {
            float ratio = ( (float) i - 3 ) / ( array.length - 6 ); // zero to 1

            // spherical linear interpolation (slerp) from start to end
            Vector3f position = new Quaternion().slerp( Quaternion.IDENTITY, startToEnd, ratio ).mult( startDir ).mult( radius );

            array[i] = position.x;
            array[i + 1] = position.y;
            array[i + 2] = position.z;
        }

        indices[0] = (short) 0;
        i = 1;
        int k;
        for ( int j = 0; j < numTriangles; j++ ) {
            k = j + 1;
            indices[i] = (short) k;
            i++;
            k++;
            indices[i] = (short) k;
            i++;
        }

        this.setMode( Mode.TriangleFan );
        this.setBuffer( VertexBuffer.Type.Position, 3, array );
        this.setBuffer( VertexBuffer.Type.Index, 2, indices );//(spline.getControlPoints().size() - 1) * nbSubSegments * 2
        this.updateBound();
        this.updateCounts();
    }
}
