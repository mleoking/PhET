// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.shapes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.Bounds3F;
import edu.colorado.phet.common.phetcommon.math.Triangle3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.lwjglphet.utils.GLDisplayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * Reads in (currently a subset of) .obj files (Wavefront format) into a mesh that can be rendered
 */
public class ObjMesh {

    private FloatBuffer positionBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private IntBuffer indexBuffer;

    private boolean hasNormals = false;
    private boolean hasTextures = false;

    private GLDisplayList displayList;

    private List<Triangle3F> triangles = new ArrayList<Triangle3F>();
    private Bounds3F boundingBox;

    public ObjMesh( InputStream inputStream ) throws IOException {

        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );

        List<Vector3F> vertices = new ArrayList<Vector3F>();
        List<Vector3F> normals = new ArrayList<Vector3F>();
        List<Vector2F> uvs = new ArrayList<Vector2F>();
        List<List<Group>> tris = new ArrayList<List<Group>>();

        Map<Integer, Integer> textureMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> normalMap = new HashMap<Integer, Integer>();

        String str;
        while ( ( str = reader.readLine() ) != null ) {
            StringTokenizer tokenizer = new StringTokenizer( str, " " );

            // grab the first token (if it exists)
            if ( !tokenizer.hasMoreTokens() ) {
                continue;
            }
            String firstToken = tokenizer.nextToken();

            if ( firstToken.equals( "#" ) ) {
                // it's a comment, ignore the line
                continue;
            }
            else if ( firstToken.equals( "v" ) ) {
                // it's a vertex
                vertices.add( new Vector3F( Float.parseFloat( tokenizer.nextToken() ), Float.parseFloat( tokenizer.nextToken() ), Float.parseFloat( tokenizer.nextToken() ) ) );
            }
            else if ( firstToken.equals( "vt" ) ) {
                // texture info
                hasTextures = true;
                uvs.add( new Vector2F( Float.parseFloat( tokenizer.nextToken() ), Float.parseFloat( tokenizer.nextToken() ) ) );
            }
            else if ( firstToken.equals( "vn" ) ) {
                // a normal
                hasNormals = true;
                normals.add( new Vector3F( Float.parseFloat( tokenizer.nextToken() ), Float.parseFloat( tokenizer.nextToken() ), Float.parseFloat( tokenizer.nextToken() ) ) );
            }
            else if ( firstToken.equals( "f" ) ) {
                // a face
                // TODO
                List<Group> groups = new ArrayList<Group>();
                while ( tokenizer.hasMoreTokens() ) {
                    String[] indexStrings = tokenizer.nextToken().split( "/" );
                    int vertex = Integer.parseInt( indexStrings[0] );
                    int texture = indexStrings.length > 1 ? Integer.parseInt( indexStrings[1] ) : 0;
                    int normal = indexStrings.length > 2 ? Integer.parseInt( indexStrings[2] ) : 0;

                    if ( texture > 0 ) {
                        if ( textureMap.containsKey( vertex ) ) {
                            if ( !textureMap.get( vertex ).equals( texture ) ) {
                                // vertex used with different texture coords
//                                System.out.println( "WARNING: vertex has multiple texture coordinates: " + vertex );
                            }
                        }
                        else {
                            textureMap.put( vertex, texture );
                        }
                    }

                    if ( normal > 0 ) {
                        if ( normalMap.containsKey( vertex ) ) {
                            if ( !normalMap.get( vertex ).equals( normal ) ) {
                                // vertex used with different normals
//                                System.out.println( "WARNING: vertex has multiple normals" );
                            }
                        }
                        else {
                            normalMap.put( vertex, normal );
                        }
                    }

                    groups.add( new Group( vertex, texture, normal ) );
                }

                if ( groups.size() == 3 ) {
                    tris.add( groups );
                    triangles.add( new Triangle3F(
                            vertices.get( groups.get( 0 ).vertex - 1 ),
                            vertices.get( groups.get( 1 ).vertex - 1 ),
                            vertices.get( groups.get( 2 ).vertex - 1 )
                    ) );
                }
                else if ( groups.size() == 4 ) {
                    tris.add( Arrays.asList( groups.get( 0 ), groups.get( 1 ), groups.get( 2 ) ) );
                    triangles.add( new Triangle3F(
                            vertices.get( groups.get( 0 ).vertex - 1 ),
                            vertices.get( groups.get( 1 ).vertex - 1 ),
                            vertices.get( groups.get( 2 ).vertex - 1 )
                    ) );
                    tris.add( Arrays.asList( groups.get( 0 ), groups.get( 2 ), groups.get( 3 ) ) );
                    triangles.add( new Triangle3F(
                            vertices.get( groups.get( 0 ).vertex - 1 ),
                            vertices.get( groups.get( 2 ).vertex - 1 ),
                            vertices.get( groups.get( 3 ).vertex - 1 )
                    ) );
                }
                else {
                    System.out.println( "WARNING: non-triangle or quad face!" );
                }
            }
            else if ( firstToken.equals( "usemtl" ) ) {
                // ignore matierals
            }
            else if ( firstToken.equals( "s" ) ) {
                // ignore shading groups for now
            }
        }
        inputStream.close();

        positionBuffer = BufferUtils.createFloatBuffer( vertices.size() * 3 );
        normalBuffer = BufferUtils.createFloatBuffer( vertices.size() * 3 );
        textureBuffer = BufferUtils.createFloatBuffer( vertices.size() * 2 );
        indexBuffer = BufferUtils.createIntBuffer( tris.size() * 3 );

        for ( int i = 0; i < vertices.size(); i++ ) {
            Vector3F vertex = vertices.get( i );
            positionBuffer.put( new float[]{vertex.x, vertex.y, vertex.z} );

            if ( hasNormals ) {
                Vector3F normal = normals.get( normalMap.get( i + 1 ) - 1 );
                normalBuffer.put( new float[]{normal.x, normal.y, normal.z} );
            }

            if ( hasTextures ) {
                Vector2F uv = uvs.get( textureMap.get( i + 1 ) - 1 );
                textureBuffer.put( new float[]{uv.x, uv.y} );
            }
        }

        for ( int i = 0; i < tris.size(); i++ ) {
            for ( Group group : tris.get( i ) ) {
                indexBuffer.put( group.vertex - 1 );
            }
        }

        // compute the bounding box of the points
        float minX, minY, minZ, maxX, maxY, maxZ;
        minX = minY = minZ = Float.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;
        for ( Vector3F vertex : vertices ) {
            minX = Math.min( minX, vertex.x );
            minY = Math.min( minY, vertex.y );
            minZ = Math.min( minZ, vertex.z );

            maxX = Math.max( maxX, vertex.x );
            maxY = Math.max( maxY, vertex.y );
            maxZ = Math.max( maxZ, vertex.z );
        }
        boundingBox = Bounds3F.fromMinMax( minX, maxX, minY, maxY, minZ, maxZ );

        // precompute the display list for faster rendering later
        displayList = new GLDisplayList( new Runnable() {
            public void run() {
                positionBuffer.rewind();
                normalBuffer.rewind();
                textureBuffer.rewind();
                indexBuffer.rewind();

                // TODO: seeing a lot of this type of code. refactor away if possible
                // initialize the needed states
                glEnableClientState( GL_VERTEX_ARRAY );
                if ( hasTextures ) {
                    glEnableClientState( GL_TEXTURE_COORD_ARRAY );
                    glTexCoordPointer( 2, 0, textureBuffer );
                }
                if ( hasNormals ) {
                    glEnableClientState( GL_NORMAL_ARRAY );
                    glNormalPointer( 0, normalBuffer );
                }
                glVertexPointer( 3, 0, positionBuffer );

                glDrawElements( GL_TRIANGLES, indexBuffer );

                glDisableClientState( GL_VERTEX_ARRAY );
                if ( hasTextures ) { glDisableClientState( GL_TEXTURE_COORD_ARRAY ); }
                if ( hasNormals ) { glDisableClientState( GL_NORMAL_ARRAY ); }
            }
        } );
    }

    public void draw() {
        displayList.run();
    }

    public List<Triangle3F> getTriangles() {
        return triangles;
    }

    public Bounds3F getBoundingBox() {
        return boundingBox;
    }

    private static class Group {
        public final int vertex;
        public final int texture;
        public final int normal;

        private Group( int vertex, int texture, int normal ) {
            this.vertex = vertex;
            this.texture = texture;
            this.normal = normal;
        }
    }
}
