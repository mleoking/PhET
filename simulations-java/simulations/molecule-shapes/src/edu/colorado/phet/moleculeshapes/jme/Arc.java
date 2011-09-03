// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

/**
 * Displays an arc between two directions at a specific radius. Used JME3 Curve code as a template.
 * Assumes that the center of the arc is at the (local) origin
 */
public class Arc extends Mesh {
    /**
     * Creates an Arc
     *
     * @param startDir    Unit vector for one end of the arc
     * @param endDir      Unit vector for the other end of the arc
     * @param radius      How far from the origin should the arc be? (Radius of the circle that the arc is part of)
     * @param numSegments How many line segments should the arc be approximated by
     */
    public Arc( Vector3f startDir, Vector3f endDir, float radius, int numSegments ) {
        int numVertices = numSegments + 1;

        // figure out the quaternion rotation from start to end
        Quaternion startToEnd = JmeUtils.getRotationQuaternion( startDir, endDir );

        float[] vertices = new float[numVertices * 3];
        short[] indices = new short[numSegments + 1];

        int i;

        for ( i = 0; i < vertices.length; i += 3 ) {
            float ratio = ( (float) i ) / ( vertices.length - 3 ); // zero to 1

            // spherical linear interpolation (slerp) from start to end
            Vector3f position = new Quaternion().slerp( Quaternion.IDENTITY, startToEnd, ratio ).mult( startDir ).mult( radius );

            vertices[i] = position.x;
            vertices[i + 1] = position.y;
            vertices[i + 2] = position.z;
        }

        for ( i = 0; i < indices.length; i++ ) {
            indices[i] = (short) i;
        }

        this.setMode( Mode.LineStrip );
        this.setBuffer( VertexBuffer.Type.Position, 3, vertices );
        this.setBuffer( VertexBuffer.Type.Index, 2, indices );//(spline.getControlPoints().size() - 1) * nbSubSegments * 2
        this.updateBound();
        this.updateCounts();
    }
}
