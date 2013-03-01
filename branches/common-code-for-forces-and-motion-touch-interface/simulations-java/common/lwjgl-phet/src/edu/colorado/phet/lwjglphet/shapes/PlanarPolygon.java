// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.shapes;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.nodes.GLNode;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;

/**
 * A mesh that is a polygon without holes or crossings (convex or concave) that lives in the z=0 plane
 */
public class PlanarPolygon extends GLNode {
    private FloatBuffer positionBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private ShortBuffer indexBuffer;

    // polygon where z = 0
    public PlanarPolygon( int maxVertexCount ) {
        updateMaxVertexCount( maxVertexCount );
    }

    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        positionBuffer.rewind();
        normalBuffer.rewind();
        textureBuffer.rewind();
        indexBuffer.rewind();

        glEnableClientState( GL_VERTEX_ARRAY );
        if ( options.shouldSendTexture() ) {
            glEnableClientState( GL_TEXTURE_COORD_ARRAY );
            glTexCoordPointer( 2, 0, textureBuffer );
        }
        if ( options.shouldSendNormals() ) {
            glEnableClientState( GL11.GL_NORMAL_ARRAY );
            glNormalPointer( 3, normalBuffer );
        }
        glVertexPointer( 3, 0, positionBuffer );
        glDrawElements( GL_TRIANGLES, indexBuffer );
        glDisableClientState( GL_VERTEX_ARRAY );
        if ( options.shouldSendTexture() ) { glDisableClientState( GL_TEXTURE_COORD_ARRAY );}
        if ( options.shouldSendNormals() ) {glDisableClientState( GL11.GL_NORMAL_ARRAY );}
    }

    // ensure that we can handle at least maxVertexCount vertices
    protected void updateMaxVertexCount( int maxVertexCount ) {
        positionBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
        normalBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
        textureBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 2 );

        // TODO: remove the extra "* 2" here, since it ideally shouldn't be necessary? investigate the tesselation code
        indexBuffer = BufferUtils.createShortBuffer( maxVertexCount * 3 * 2 ); // allow a bit extra index information

        // pre-initialize the normal data
        for ( int i = 0; i < maxVertexCount; i++ ) {
            normalBuffer.put( new float[]{0, 0, 1} );
        }
    }

    public PlanarPolygon( int maxVertexCount, Vector2F[] vertices, Vector2F[] textureCoordinates ) {
        this( maxVertexCount );

        setVertices( vertices, textureCoordinates );
    }

    /**
     * Sets the polygonal shape of this node
     *
     * @param vertices           Vertex positions
     * @param textureCoordinates Corresponding vertex texture coordinates
     */
    public void setVertices( Vector2F[] vertices, Vector2F[] textureCoordinates ) {
        if ( vertices.length > positionBuffer.capacity() ) {
            updateMaxVertexCount( Math.max( vertices.length, positionBuffer.capacity() + 20 ) ); // update by at least 20
        }

        positionBuffer.clear();
        textureBuffer.clear();
        indexBuffer.clear();

        // fill the position buffer
        for ( Vector2F vertex : vertices ) {
            positionBuffer.put( new float[]{vertex.x, vertex.y, 0} );
        }
        positionBuffer.limit( positionBuffer.position() );
        normalBuffer.limit( positionBuffer.position() ); // also set the limit on the normal buffer to the same position

        // fill the texture buffer
        for ( Vector2F textureCoordinate : textureCoordinates ) {
            textureBuffer.put( new float[]{textureCoordinate.x, textureCoordinate.y} );
        }
        textureBuffer.limit( textureBuffer.position() );

        // ignore polygons with less than 3 vertices
        if ( vertices.length >= 3 ) {
            // TODO: currently we ditch identical vertices that are one after another. find better way or ditch the tesselation code?
            List<PolygonPoint> pointList = new ArrayList<PolygonPoint>( vertices.length );
            float lastX = 0;
            float lastY = 0;
            for ( short i = 0; i < vertices.length; i++ ) {
                float x = vertices[i].getX();
                float y = vertices[i].getY();
                if ( i > 0 && x == lastX && y == lastY ) {
                    continue;
                }
                pointList.add( new IndexedPolygonPoint( i, x, y ) );
                lastX = x;
                lastY = y;
            }

            // make a list of polygon points that we can refer to later
            PolygonPoint[] points = pointList.toArray( new PolygonPoint[pointList.size()] );

            // create and triangulate our polygon
            Polygon polygon = new Polygon( points );
            try {
                Poly2Tri.triangulate( polygon );
            }
            catch( Exception e ) {
                System.out.println( "triangulation failure:" );
                for ( PolygonPoint point : points ) {
                    System.out.println( "{" + point.getX() + ", " + point.getY() + "}," );
                }
                System.out.flush();
                throw new RuntimeException( "polygon failure", e );
            }

            // iterate through the triangles, and add their indices into the index buffer
            List<DelaunayTriangle> triangles = polygon.getTriangles();
            for ( DelaunayTriangle triangle : triangles ) {
                if ( !( triangle.points[0] instanceof IndexedPolygonPoint ) ||
                     !( triangle.points[1] instanceof IndexedPolygonPoint ) ||
                     !( triangle.points[2] instanceof IndexedPolygonPoint ) ) {
                    continue;
                }
                // find the indices of the points
                short[] indices = new short[]{
                        ( (IndexedPolygonPoint) triangle.points[0] ).index,
                        ( (IndexedPolygonPoint) triangle.points[1] ).index,
                        ( (IndexedPolygonPoint) triangle.points[2] ).index};

                // add the indices into the index buffer
                indexBuffer.put( indices );
            }
        }
        indexBuffer.limit( indexBuffer.position() );
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
