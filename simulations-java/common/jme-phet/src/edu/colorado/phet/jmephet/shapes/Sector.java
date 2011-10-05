// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.shapes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * Displays the area drawn out by an arc (imagine a line connected to the origin rotating from A to B).
 */
public class Sector extends Mesh {

    private FloatBuffer positionBuffer;
    private final PointArc arc;
    private final boolean reversed;

    /**
     * Create a sector using the points from a specified arc.
     * // TODO: remove reversed option, since we can make this double-sided!
     *
     * @param arc      Arc to pull points from
     * @param reversed Whether the points should be used in the opposite order. Useful for making 2-sided sectors
     */
    public Sector( PointArc arc, boolean reversed ) {
        this.arc = arc;
        this.reversed = reversed;

        // calculate convenience numbers
        int numTriangles = arc.getNumVertices() - 1;
        int numEdgeVertices = numTriangles + 1;
        int numVertices = numEdgeVertices + 1; // +1 for origin
        int numIndices = numTriangles * 3;

        positionBuffer = BufferUtils.createFloatBuffer( numVertices * 3 );
        ShortBuffer indexBuffer = BufferUtils.createShortBuffer( numIndices );

        setPositions();

        // set up the indices correctly for Mode.Triangles // TODO: in the future, use Mode.TriangleFan!
        for ( int i = 0; i < numIndices; i++ ) {
            indexBuffer.put( (short) ( ( i % 3 + i / 3 ) * ( i % 3 ) * ( 3 - i % 3 ) / 2 ) ); // an exercise left to the reader
        }

        this.setMode( Mode.Triangles );
        this.setBuffer( VertexBuffer.Type.Position, 3, positionBuffer );
        this.setBuffer( VertexBuffer.Type.Index, 3, indexBuffer );
        this.updateBound();
        this.updateCounts();
    }

    private void setPositions() {
        // pull the vertices from the arc
        float[] arcVertices = arc.getVertexData();

        positionBuffer.clear();
        positionBuffer.put( new float[] { 0, 0, 0 } ); // origin point (not in original arc, so added in the front)

        int len = arcVertices.length;
        if ( reversed ) {
            // TODO: remove reversed option, since we can make this double-sided!
            // insert the vertices in the opposite order
            for ( int i = 0; i < len; i += 3 ) {
                int arcBase = len - 3 - i;
                positionBuffer.put( new float[] { arcVertices[arcBase], arcVertices[arcBase + 1], arcVertices[arcBase + 2] } );
            }
        }
        else {
            for ( int i = 0; i < len; i++ ) {
                positionBuffer.put( arcVertices[i] );
            }
        }
    }

    public void updateView() {
        setPositions();

        getBuffer( Type.Position ).updateData( positionBuffer );

        updateBound();
        updateCounts();
    }
}
