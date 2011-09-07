// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Displays the area drawn out by an arc (imagine a line connected to the origin rotating from A to B).
 */
public class Sector extends Mesh {

    /**
     * Create a sector using the points from a specified arc.
     *
     * @param arc      Arc to pull points from
     * @param reversed Whether the points should be used in the opposite order. Useful for making 2-sided sectors
     */
    public Sector( PointArc arc, boolean reversed ) {
        // pull vertices from the arc
        float[] arcVertices = arc.getVertices();

        // calculate convenience numbers
        int numTriangles = arcVertices.length / 3 - 1;
        int numEdgeVertices = numTriangles + 1;
        int numVertices = numEdgeVertices + 1;

        float[] vertices = new float[numVertices * 3]; // +1 for origin
        short[] indices = new short[numTriangles * 3];

        vertices[0] = vertices[1] = vertices[2] = 0; // origin point (not in original arc, so added in the front)

        int len = arcVertices.length;
        if ( reversed ) {
            // insert the vertices in the opposite order
            for ( int i = 0; i < len; i += 3 ) {
                int vertexBase = i + 3;
                int arcBase = len - 3 - i;
                vertices[vertexBase] = arcVertices[arcBase];
                vertices[vertexBase + 1] = arcVertices[arcBase + 1];
                vertices[vertexBase + 2] = arcVertices[arcBase + 2];
            }
        }
        else {
            System.arraycopy( arcVertices, 0, vertices, 3, len ); // direct array copy for speed
        }

        // set up the indices correctly for Mode.Triangles // TODO: in the future, use Mode.TriangleStrip?
        for ( int i = 0; i < indices.length; i++ ) {
            indices[i] = (short) ( ( i % 3 + i / 3 ) * ( i % 3 ) * ( 3 - i % 3 ) / 2 ); // an exercise left to the reader
        }

        this.setMode( Mode.Triangles );
        this.setBuffer( VertexBuffer.Type.Position, 3, vertices );
        this.setBuffer( VertexBuffer.Type.Index, 3, indices );
        this.updateBound();
        this.updateCounts();
    }
}
