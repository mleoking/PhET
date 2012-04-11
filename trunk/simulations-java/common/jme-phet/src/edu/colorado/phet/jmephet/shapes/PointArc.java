// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.shapes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import edu.colorado.phet.jmephet.JMEUtils;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * Displays an arc between two directions at a specific radius. Used JME3 Curve code as a template.
 * Assumes that the center of the arc is at the (local) origin
 */
public class PointArc extends Mesh {

    private boolean approximateSemicircle;

    private Vector3f midpoint;
    private FloatBuffer positionBuffer;
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
    public PointArc( Vector3f startDir, Vector3f endDir, float radius, int numSegments, Vector3f lastMidpointDir ) {
        this.radius = radius;
        numVertices = numSegments + 1;
        numFloats = numVertices * 3;
        int numIndices = numSegments + 1;

        positionBuffer = BufferUtils.createFloatBuffer( numFloats );
        ShortBuffer indexBuffer = BufferUtils.createShortBuffer( numIndices );

        setPositions( startDir, endDir, lastMidpointDir );

        for ( short i = 0; i < numIndices; i++ ) {
            indexBuffer.put( i );
        }

        this.setMode( Mode.LineStrip );
        this.setBuffer( VertexBuffer.Type.Position, 3, positionBuffer );
        this.setBuffer( VertexBuffer.Type.Index, 2, indexBuffer );
        this.updateBound();
        this.updateCounts();
    }

    // updates the arc. see the constructor for documentation on arguments
    private void setPositions( Vector3f startDir, Vector3f endDir, Vector3f lastMidpointDir ) {
        positionBuffer.clear();

        approximateSemicircle = isApproximateSemicircle( startDir, endDir );

        if ( approximateSemicircle ) {
            // basically construct a an approximate semicircle based on the semicircleDir, so that our semicircle doesn't vary dramatically

            if ( lastMidpointDir == null ) {
                lastMidpointDir = new Vector3f( 0, 1, 0 );
            }

            // find a vector that is as orthogonal to both directions as possible
            Vector3f averageCross = startDir.cross( lastMidpointDir ).add( lastMidpointDir.cross( endDir ) ).normalizeLocal();

            // find a vector that gives us a balance between startDir and endDir (so our semicircle will balance out at the endpoints)
            Vector3f averagePointDir = startDir.subtract( endDir ).normalizeLocal();

            // (basis vector 1) make that average point planar to our arc surface
            Vector3f planarPointDir = averagePointDir.subtract( averagePointDir.mult( averageCross.dot( averagePointDir ) ) ).normalizeLocal();

            // (basis vector 2) find a new midpoint direction that is planar to our arc surface
            Vector3f planarMidpointDir = averageCross.cross( planarPointDir ).normalizeLocal();

            // now make our semicircle around with our two orthonormal basis vectors
            for ( int i = 0; i < numFloats; i += 3 ) {
                float ratio = ( (float) i ) / ( numFloats - 3 ); // zero to 1

                // 0 should be near startDir, 0.5 (pi/2) should be near semicircleDir, 1 (pi) should be near endDir
                float angle = ratio * FastMath.PI;

                Vector3f position = planarPointDir.mult( FastMath.cos( angle ) ).add( planarMidpointDir.mult( FastMath.sin( angle ) ) ).mult( radius );

                positionBuffer.put( new float[] { position.x, position.y, position.z } );
            }

            midpoint = planarMidpointDir.mult( radius );
        }
        else {
            // figure out the quaternion rotation from start to end
            Quaternion startToEnd = JMEUtils.getRotationQuaternion( startDir, endDir );

            for ( int i = 0; i < numFloats; i += 3 ) {
                float ratio = ( (float) i ) / ( numFloats - 3 ); // zero to 1

                // spherical linear interpolation (slerp) from start to end
                Vector3f position = new Quaternion().slerp( Quaternion.IDENTITY, startToEnd, ratio ).mult( startDir ).mult( radius );

                positionBuffer.put( new float[] { position.x, position.y, position.z } );
            }

            midpoint = JMEUtils.slerp( startDir, endDir, 0.5f ).mult( radius );
        }
    }

    public Vector3f getMidpoint() {
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

    public static boolean isApproximateSemicircle( Vector3f startDir, Vector3f endDir ) {
        return FastMath.acos( startDir.dot( endDir ) ) >= 3.12414;
    }

    public void updateView( Vector3f startDir, Vector3f endDir, Vector3f lastMidpointDir ) {
        setPositions( startDir, endDir, lastMidpointDir );

        getBuffer( Type.Position ).updateData( positionBuffer );

        updateBound();
        updateCounts();
    }
}
