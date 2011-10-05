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

    /**
     * Create a sector using the points from a specified arc.
     *
     * @param arc Arc to pull points from
     */
    public Sector( PointArc arc ) {
        this.arc = arc;

        // calculate convenience numbers
        int numTriangles = arc.getNumVertices() - 1;
        int numEdgeVertices = numTriangles + 1;
        int numVertices = numEdgeVertices + 1; // +1 for origin

        positionBuffer = BufferUtils.createFloatBuffer( numVertices * 3 );
        ShortBuffer indexBuffer = BufferUtils.createShortBuffer( numVertices );

        setPositions();

        // our shape is easy to make with the TriangleFan mode
        for ( int i = 0; i < numVertices; i++ ) {
            indexBuffer.put( (short) i );
        }

        this.setMode( Mode.TriangleFan );
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
        for ( int i = 0; i < len; i++ ) {
            positionBuffer.put( arcVertices[i] );
        }
    }

    public void updateView() {
        setPositions();

        getBuffer( Type.Position ).updateData( positionBuffer );

        updateBound();
        updateCounts();
    }
}
