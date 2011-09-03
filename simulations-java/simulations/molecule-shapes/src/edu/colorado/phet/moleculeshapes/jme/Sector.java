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
        Quaternion startToEnd = JmeUtils.getRotationQuaternion( startDir, endDir );

        float[] vertices = new float[numVertices * 3]; // +1 for origin
        short[] indices = new short[numTriangles * 3];

        vertices[0] = vertices[1] = vertices[2] = 0; // origin

        for ( int edgeIndex = 0; edgeIndex < numEdgeVertices; edgeIndex++ ) {
            float ratio = ( (float) edgeIndex ) / ( numEdgeVertices - 1 ); // zero to 1

            // spherical linear interpolation (slerp) from start to end
            Vector3f position = new Quaternion().slerp( Quaternion.IDENTITY, startToEnd, ratio ).mult( startDir ).mult( radius );

            int baseIndex = ( edgeIndex + 1 ) * 3; // allow one for the zeros (origin)
            vertices[baseIndex] = position.x;
            vertices[baseIndex + 1] = position.y;
            vertices[baseIndex + 2] = position.z;
        }

        for ( int i = 0; i < indices.length; i++ ) {
            indices[i] = (short) ( ( i % 3 + i / 3 ) * ( i % 3 ) * ( 3 - i % 3 ) / 2 ); // an exercise left to the reader
        }

        this.setMode( Mode.Triangles ); // TODO: get Mode.TriangleStrip working?
        this.setBuffer( VertexBuffer.Type.Position, 3, vertices );
        this.setBuffer( VertexBuffer.Type.Index, 3, indices );//(spline.getControlPoints().size() - 1) * nbSubSegments * 2
        this.updateBound();
        this.updateCounts();
    }
}
