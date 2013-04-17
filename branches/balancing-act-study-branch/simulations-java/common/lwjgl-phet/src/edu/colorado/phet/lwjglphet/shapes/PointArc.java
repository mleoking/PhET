// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.shapes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.QuaternionF;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.nodes.GLNode;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;

/**
 * Displays an arc between two directions at a specific radius.
 * Assumes that the center of the arc is at the (local) origin
 */
public class PointArc extends GLNode {

    private boolean approximateSemicircle;

    private Vector3F midpoint;
    private FloatBuffer positionBuffer;
    private ShortBuffer indexBuffer;
    private final float radius;
    private int numFloats;
    private int numVertices;

    /**
     * Creates an Arc
     *
     * @param startDir        Unit vector for one end of the arc
     * @param endDir          Unit vector for the other end of the arc
     * @param radius          How far from the origin should the arc be? (Radius of the circle that the arc is part of)
     * @param numSegments     How many line segments should the arc be approximated by
     * @param lastMidpointDir The direction that the arc will point out in if it is determined to be too close to 180 degrees
     */
    public PointArc( Vector3F startDir, Vector3F endDir, float radius, int numSegments, Vector3F lastMidpointDir ) {
        this.radius = radius;
        numVertices = numSegments + 1;
        numFloats = numVertices * 3;
        int numIndices = numSegments + 1;

        positionBuffer = BufferUtils.createFloatBuffer( numFloats );
        indexBuffer = BufferUtils.createShortBuffer( numIndices );

        setPositions( startDir, endDir, lastMidpointDir );

        // initialize the index buffer (only needed once)
        for ( short i = 0; i < numIndices; i++ ) {
            indexBuffer.put( i );
        }
    }


    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        positionBuffer.rewind();
        indexBuffer.rewind();

        glEnableClientState( GL_VERTEX_ARRAY );

        glVertexPointer( 3, 0, positionBuffer );
        glDrawElements( GL_LINE_STRIP, indexBuffer );

        glDisableClientState( GL_VERTEX_ARRAY );
    }

    // updates the arc. see the constructor for documentation on arguments
    private void setPositions( Vector3F startDir, Vector3F endDir, Vector3F lastMidpointDir ) {
        positionBuffer.clear();

        approximateSemicircle = isApproximateSemicircle( startDir, endDir );

        if ( approximateSemicircle ) {
            // basically construct a an approximate semicircle based on the semicircleDir, so that our semicircle doesn't vary dramatically

            if ( lastMidpointDir == null ) {
                lastMidpointDir = new Vector3F( 0, 1, 0 );
            }

            // find a vector that is as orthogonal to both directions as possible
            Vector3F badCross = startDir.cross( lastMidpointDir ).plus( lastMidpointDir.cross( endDir ) );
            Vector3F averageCross = badCross.magnitude() > 0 ? badCross.normalized() : new Vector3F( 0, 0, 1 );

            // find a vector that gives us a balance between startDir and endDir (so our semicircle will balance out at the endpoints)
            Vector3F averagePointDir = startDir.minus( endDir ).normalized();

            // (basis vector 1) make that average point planar to our arc surface
            Vector3F planarPointDir = averagePointDir.minus( averagePointDir.times( averageCross.dot( averagePointDir ) ) ).normalized();

            // (basis vector 2) find a new midpoint direction that is planar to our arc surface
            Vector3F planarMidpointDir = averageCross.cross( planarPointDir ).normalized();

            // now make our semicircle around with our two orthonormal basis vectors
            for ( int i = 0; i < numFloats; i += 3 ) {
                float ratio = ( (float) i ) / ( numFloats - 3 ); // zero to 1

                // 0 should be near startDir, 0.5 (pi/2) should be near semicircleDir, 1 (pi) should be near endDir
                float angle = (float) ( ratio * Math.PI );

                Vector3F position = planarPointDir.times( (float) Math.cos( angle ) )
                        .plus( planarMidpointDir.times( (float) Math.sin( angle ) ) )
                        .times( radius );

                positionBuffer.put( new float[]{position.x, position.y, position.z} );
            }

            midpoint = planarMidpointDir.times( radius );
        }
        else {
            // figure out the quaternion rotation from start to end
            QuaternionF startToEnd = QuaternionF.getRotationQuaternion( startDir, endDir );

            for ( int i = 0; i < numFloats; i += 3 ) {
                float ratio = ( (float) i ) / ( numFloats - 3 ); // zero to 1

                // spherical linear interpolation (slerp) from start to end
                Vector3F position = new QuaternionF().slerp( new QuaternionF(), startToEnd, ratio ).times( startDir ).times( radius );

                positionBuffer.put( new float[]{position.x, position.y, position.z} );
            }

            midpoint = Vector3F.slerp( startDir, endDir, 0.5f ).times( radius );
        }
    }

    public Vector3F getMidpoint() {
        return midpoint;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public boolean isApproximateSemicircle() {
        return approximateSemicircle;
    }

    public float[] getVertexData() {
        // TODO: potentially just return a buffer in the future?
        float[] result = new float[positionBuffer.limit()];
        for ( int i = 0; i < positionBuffer.limit(); i++ ) {
            result[i] = positionBuffer.get( i );
        }
        return result;
    }

    public static boolean isApproximateSemicircle( Vector3F startDir, Vector3F endDir ) {
        return Math.acos( startDir.dot( endDir ) ) >= 3.12414;
    }

    public void updateView( Vector3F startDir, Vector3F endDir, Vector3F lastMidpointDir ) {
        setPositions( startDir, endDir, lastMidpointDir );
    }
}
