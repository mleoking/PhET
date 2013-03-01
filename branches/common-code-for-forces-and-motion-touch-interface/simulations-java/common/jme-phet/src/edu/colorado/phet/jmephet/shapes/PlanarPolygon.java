// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.shapes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import com.jme3.math.Vector2f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * A mesh that is a polygon without holes or crossings (convex or concave) that lives in the z=0 plane
 */
public class PlanarPolygon extends Mesh {
    private FloatBuffer positionBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private ShortBuffer indexBuffer;

    // polygon where z = 0
    public PlanarPolygon( int maxVertexCount ) {
        updateMaxVertexCount( maxVertexCount );

        // since we use the general triangulation method, we just specify triangles here
        setMode( Mode.Triangles );

        setBuffer( VertexBuffer.Type.Position, 3, positionBuffer );
        setBuffer( VertexBuffer.Type.Normal, 3, normalBuffer );
        setBuffer( VertexBuffer.Type.TexCoord, 2, textureBuffer );
        setBuffer( VertexBuffer.Type.Index, 3, indexBuffer );
        updateBound();
        updateCounts();
    }

    // ensure that we can handle at least maxVertexCount vertices
    private void updateMaxVertexCount( int maxVertexCount ) {
        positionBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
        normalBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
        textureBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 2 );
        indexBuffer = BufferUtils.createShortBuffer( maxVertexCount * 3 ); // allow a bit extra index information

        // pre-initialize the normal data
        for ( int i = 0; i < maxVertexCount; i++ ) {
            normalBuffer.put( new float[] { 0, 0, 1 } );
        }
    }

    public PlanarPolygon( int maxVertexCount, Vector2f[] vertices, Vector2f[] textureCoordinates ) {
        this( maxVertexCount );

        setVertices( vertices, textureCoordinates );
    }

    /**
     * Sets the polygonal shape of this node
     *
     * @param vertices           Vertex positions
     * @param textureCoordinates Corresponding vertex texture coordinates
     */
    public void setVertices( Vector2f[] vertices, Vector2f[] textureCoordinates ) {
        if ( vertices.length > positionBuffer.capacity() ) {
            updateMaxVertexCount( Math.max( vertices.length, positionBuffer.capacity() + 20 ) ); // update by at least 20
        }

        positionBuffer.clear();
        textureBuffer.clear();
        indexBuffer.clear();

        // fill the position buffer
        for ( Vector2f vertex : vertices ) {
            positionBuffer.put( new float[] { vertex.x, vertex.y, 0 } );
        }
        positionBuffer.limit( positionBuffer.position() );
        normalBuffer.limit( positionBuffer.position() ); // also set the limit on the normal buffer to the same position

        // fill the texture buffer
        for ( Vector2f textureCoordinate : textureCoordinates ) {
            textureBuffer.put( new float[] { textureCoordinate.x, textureCoordinate.y } );
        }
        textureBuffer.limit( textureBuffer.position() );

        // ignore polygons with less than 3 vertices
        if ( vertices.length >= 3 ) {
            // make a list of polygon points that we can refer to later
            PolygonPoint[] points = new PolygonPoint[vertices.length];
            for ( short i = 0; i < points.length; i++ ) {
                points[i] = new IndexedPolygonPoint( i, vertices[i].getX(), vertices[i].getY() );
            }

            // create and triangulate our polygon
            Polygon polygon = new Polygon( points );
            Poly2Tri.triangulate( polygon );

            // iterate through the triangles, and add their indices into the index buffer
            List<DelaunayTriangle> triangles = polygon.getTriangles();
            for ( DelaunayTriangle triangle : triangles ) {
                // find the indices of the points
                short[] indices = new short[] {
                        ( (IndexedPolygonPoint) triangle.points[0] ).index,
                        ( (IndexedPolygonPoint) triangle.points[1] ).index,
                        ( (IndexedPolygonPoint) triangle.points[2] ).index };

                // add the indices into the index buffer
                indexBuffer.put( indices );
            }
        }
        indexBuffer.limit( indexBuffer.position() );

        // update the underlying vertex buffers
        getBuffer( Type.Position ).updateData( positionBuffer );
        getBuffer( Type.Normal ).updateData( normalBuffer );
        getBuffer( Type.TexCoord ).updateData( textureBuffer );
        getBuffer( Type.Index ).updateData( indexBuffer );

        // update various statistics and counts
        updateBound();
        updateCounts();
    }

    // gives us an extra index on our polygon points that we can use for our index buffer
    private static class IndexedPolygonPoint extends PolygonPoint {
        public final short index;

        public IndexedPolygonPoint( short index, double x, double y ) {
            super( x, y );
            this.index = index;
        }
    }
}
