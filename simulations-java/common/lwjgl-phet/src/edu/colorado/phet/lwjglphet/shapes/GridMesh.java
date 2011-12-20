// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays a quad-based polygonal grid tesselated with triangles. Think of it as a table of
 * vertices, each connected to its neighbors.
 */
public class GridMesh extends GLNode {

    private FloatBuffer positionBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureBuffer;
    private IntBuffer indexBuffer;

    private boolean updateNormals = true;

    private int rows;
    private int columns;
    protected ImmutableVector3F[] positions;

    /**
     * @param rows      How many rows of vertices there are in positions
     * @param columns   How many columns of vertices  there are in positions
     * @param positions Vertices. Should be rows*columns of them
     */
    public GridMesh( int rows, int columns, ImmutableVector3F[] positions ) {
        this.rows = rows;
        this.columns = columns;
        this.positions = positions;

        int vertexCount = columns * rows;
        positionBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
        normalBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
        textureBuffer = BufferUtils.createFloatBuffer( vertexCount * 2 );

        // we build it from strips. there are (rows-1) strips, each strip uses two full rows (columns*2),
        // and for the connections between strips (rows-2 of those), we need 2 extra indices
        int numIndices = ( rows - 1 ) * columns * 2 + ( rows - 2 ) * 2;
        indexBuffer = BufferUtils.createIntBuffer( numIndices );

        setPositions( positions );

        // compute texture coordinates at the start
        float maxSize = Math.max( rows, columns );
        for ( int row = 0; row < rows; row++ ) {
            for ( int col = 0; col < columns; col++ ) {
                /*---------------------------------------------------------------------------*
                * texture
                *----------------------------------------------------------------------------*/

                // add texture coordinates based on the largest overall space that will fit in our unit square with the correct aspect ratio
                textureBuffer.put( new float[] {
                        ( (float) ( col ) ) / maxSize,
                        ( (float) ( row ) ) / maxSize, // consider moving this out of the loop for optimization
                        /*
                        ( (float) ( col ) ) / (float) ( columns - 1 ),
                        ( (float) ( row ) ) / (float) ( rows - 1 ), // consider moving this out of the loop for optimization
                         */
                } );
            }
        }

        // create the index information so OpenGL knows how to walk our position indices
        for ( int strip = 0; strip < rows - 1; strip++ ) {
            int stripOffset = strip * columns;

            if ( strip != 0 ) {
                // add in two points that create volume-less triangles (won't render) and keep the winding number the same for the start
                indexBuffer.put( stripOffset + columns - 1 ); // add the last-added point
                indexBuffer.put( stripOffset );
            }

            // each quad is walked over by hitting (in order) upper-left, lower-left, upper-right, lower-right.
            for ( int offset = 0; offset < columns; offset++ ) {
                indexBuffer.put( stripOffset + offset );
                indexBuffer.put( stripOffset + columns + offset ); // down a row
            }
        }
    }

    /**
     * Update the position vertices.
     *
     * @param positions See docs on constructor
     */
    public void updateGeometry( ImmutableVector3F[] positions ) {
        setPositions( positions );
    }

    private void setPositions( ImmutableVector3F[] positions ) {
        // reset the buffers
        positionBuffer.clear();

        if ( updateNormals ) {
            normalBuffer.clear();
        }

        // fill them with data
        for ( int row = 0; row < rows; row++ ) {
            int rowOffset = row * columns;
            for ( int col = 0; col < columns; col++ ) {
                // TODO: normals look kind of broken. investigate
                /*---------------------------------------------------------------------------*
                * position
                *----------------------------------------------------------------------------*/
                ImmutableVector3F position = positions[rowOffset + col];
                positionBuffer.put( new float[] { position.x, position.y, position.z } );


                /*---------------------------------------------------------------------------*
                * normal
                *----------------------------------------------------------------------------*/
                if ( updateNormals ) {
                    ImmutableVector3F up, down, left, right;

                    // calculate up/down vectors
                    if ( row > 0 ) {
                        up = getPosition( row - 1, col ).minus( position );
                        down = ( row < rows - 1 ) ? getPosition( row + 1, col ).minus( position ) : up.negated();
                    }
                    else {
                        down = getPosition( row + 1, col ).minus( position );
                        up = down.negated();
                    }

                    // calculate left/right vectors
                    if ( col > 0 ) {
                        left = getPosition( row, col - 1 ).minus( position );
                        right = ( col < columns - 1 ) ? getPosition( row, col + 1 ).minus( position ) : left.negated();
                    }
                    else {
                        right = getPosition( row, col + 1 ).minus( position );
                        left = right.negated();
                    }

                    ImmutableVector3F normal = new ImmutableVector3F();
                    // basically, sum up the normals of each quad this vertex is part of, and take the average
                    normal = normal.plus( right.cross( up ).normalized() );
                    normal = normal.plus( up.cross( left ).normalized() );
                    normal = normal.plus( left.cross( down ).normalized() );
                    normal = normal.plus( down.cross( right ).normalized() );
                    normal = normal.normalized();
                    normalBuffer.put( new float[] { normal.x, normal.y, normal.z } );
                }
            }
        }
    }

    public void setUpdateNormals( boolean updateNormals ) {
        this.updateNormals = updateNormals;
    }

    @Override public void renderSelf( GLOptions options ) {
        positionBuffer.rewind();
        normalBuffer.rewind();
        textureBuffer.rewind();
        indexBuffer.rewind();

        // TODO: seeing a lot of this type of code. refactor away if possible
        // initialize the needed states
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

        // renders in a series of triangle strips. quad strips rejected since we can't guarantee they will be planar
        glDrawElements( GL_TRIANGLE_STRIP, indexBuffer );

        // disable the changed states
        glDisableClientState( GL_VERTEX_ARRAY );
        if ( options.shouldSendTexture() ) { glDisableClientState( GL_TEXTURE_COORD_ARRAY ); }
        if ( options.shouldSendNormals() ) { glDisableClientState( GL11.GL_NORMAL_ARRAY ); }
    }

    public ImmutableVector3F getPosition( int row, int col ) {
        return positions[row * columns + col];
    }

    public ImmutableVector2D getTextureCoordinate( int row, int col ) {
        // return a default mapping that covers the entire texture
        return new ImmutableVector2D( ( (float) row ) / ( (float) ( rows - 1 ) ),
                                      ( (float) col ) / ( (float) ( columns - 1 ) ) );
    }
}
