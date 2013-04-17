// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.shapes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.nodes.GLNode;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;

/**
 * Displays the area drawn out by an arc (imagine a line connected to the origin rotating from A to B).
 */
public class Sector extends GLNode {

    private FloatBuffer positionBuffer;
    private ShortBuffer indexBuffer;
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
        indexBuffer = BufferUtils.createShortBuffer( numVertices );

        setPositions();

        // our shape is easy to make with the TriangleFan mode
        for ( int i = 0; i < numVertices; i++ ) {
            indexBuffer.put( (short) i );
        }
    }

    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        positionBuffer.rewind();
        indexBuffer.rewind();

        glEnableClientState( GL_VERTEX_ARRAY );

        glVertexPointer( 3, 0, positionBuffer );
        glDrawElements( GL_TRIANGLE_FAN, indexBuffer );

        glDisableClientState( GL_VERTEX_ARRAY );
    }

    private void setPositions() {
        // pull the vertices from the arc
        float[] arcVertices = arc.getVertexData();

        positionBuffer.clear();
        positionBuffer.put( new float[] { 0, 0, 0 } ); // origin point (not in original arc, so added in the front)

        for ( float vertex : arcVertices ) {
            positionBuffer.put( vertex );
        }
    }

    public void updateView() {
        setPositions();
    }
}
