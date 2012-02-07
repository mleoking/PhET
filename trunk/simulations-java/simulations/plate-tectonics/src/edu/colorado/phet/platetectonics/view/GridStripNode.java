// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.view.materials.EarthTexture;

import static org.lwjgl.opengl.GL11.*;

/**
 * Handles general grids where vertices can be more easily added to each side
 */
public abstract class GridStripNode extends GLNode {
    private final LWJGLTransform modelViewTransform;

    private int capacity = 0;
    private int lastNumXSamples = 0;

    private FloatBuffer positionBuffer;
    private FloatBuffer normalBuffer;
    private IntBuffer indexBuffer;

    // used for normal computation
    private ImmutableVector3F[] positionArray;

    private boolean computeNormals = true;

    public GridStripNode( final LWJGLTransform modelViewTransform ) {
        this.modelViewTransform = modelViewTransform;
    }

    public abstract int getNumberOfVertices();

    public abstract int getNumColumns();

    public abstract int getNumRows();

    public abstract float getXPosition( int xIndex );

    public abstract float getZPosition( int zIndex );

    public abstract float getElevation( int xIndex, int zIndex );

    public void setCapacity( int capacity ) {
        this.capacity = capacity;
        positionBuffer = BufferUtils.createFloatBuffer( capacity * 3 );
        normalBuffer = BufferUtils.createFloatBuffer( capacity * 3 );

        // we build it from strips. there are (rows-1) strips, each strip uses two full rows (columns*2),
        // and for the connections between strips (rows-2 of those), we need 2 extra indices
        int rows = getNumRows();
        int cols = getNumColumns();
        int neededIndices = ( rows - 1 ) * cols * 2 + ( rows - 2 ) * 2;
        indexBuffer = BufferUtils.createIntBuffer( neededIndices );
        lastNumXSamples = 0;
    }

    private void checkSize() {
        final int neededVertexCount = Math.max( 10, getNumberOfVertices() );
        if ( capacity < neededVertexCount ) {
            setCapacity( neededVertexCount );
        }
    }

    protected void updatePosition() {
        checkSize();

        int numXSamples = getNumColumns();
        int numZSamples = getNumRows();

        if ( lastNumXSamples != numXSamples ) {
            updateIndexBuffer( numXSamples, numZSamples );
        }

        positionBuffer.clear();
        normalBuffer.clear();

        // used for normal computation
        if ( isComputeNormals() ) {
            positionArray = new ImmutableVector3F[numXSamples * numZSamples];
        }

        // used for faster trig calculations for positioning
        ImmutableVector3F[] xRadials = new ImmutableVector3F[numXSamples];
        ImmutableVector3F[] zRadials = new ImmutableVector3F[numZSamples];

        for ( int i = 0; i < numXSamples; i++ ) {
            xRadials[i] = PlateModel.getXRadialVector( getXPosition( i ) );
        }
        for ( int i = 0; i < numZSamples; i++ ) {
            zRadials[i] = PlateModel.getZRadialVector( getZPosition( i ) );
        }

        /*---------------------------------------------------------------------------*
        * position buffer (and array, used later for normals)
        *----------------------------------------------------------------------------*/
        for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
            ImmutableVector3F zRadial = zRadials[zIndex];
            for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                float elevation = getElevation( xIndex, zIndex );
                ImmutableVector3F cartesianModelVector = PlateModel.convertToRadial( xRadials[xIndex], zRadial, elevation );
                ImmutableVector3F viewVector = modelViewTransform.transformPosition( cartesianModelVector );

                // fill the position array, so we can compute normals in a second
                if ( isComputeNormals() ) {
                    positionArray[zIndex * numXSamples + xIndex] = viewVector;
                }

                // fill in the buffer while we are at it
                positionBuffer.put( new float[] { viewVector.x, viewVector.y, viewVector.z } );
            }
        }

        /*---------------------------------------------------------------------------*
        * normal buffer
        *----------------------------------------------------------------------------*/
        if ( isComputeNormals() ) {
            for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
                for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                    ImmutableVector3F normal = getArrayNormal( zIndex, xIndex );
                    normalBuffer.put( new float[] { normal.x, normal.y, normal.z } );
                }
            }
        }
    }

    private ImmutableVector3F getArrayPosition( int zIndex, int xIndex ) {
        return positionArray[zIndex * getNumColumns() + xIndex];
    }

    // adapted from GridMesh
    private ImmutableVector3F getArrayNormal( int zIndex, int xIndex ) {
        // basic approximation based on neighbors.
        ImmutableVector3F position = getArrayPosition( zIndex, xIndex );
        ImmutableVector3F up, down, left, right;

        // calculate up/down vectors
        if ( zIndex > 0 ) {
            up = getArrayPosition( zIndex - 1, xIndex ).minus( position );
            down = ( zIndex < getNumRows() - 1 ) ? getArrayPosition( zIndex + 1, xIndex ).minus( position ) : up.negated();
        }
        else {
            down = getArrayPosition( zIndex + 1, xIndex ).minus( position );
            up = down.negated();
        }

        // calculate left/right vectors
        if ( xIndex > 0 ) {
            left = getArrayPosition( zIndex, xIndex - 1 ).minus( position );
            right = ( xIndex < getNumColumns() - 1 ) ? getArrayPosition( zIndex, xIndex + 1 ).minus( position ) : left.negated();
        }
        else {
            right = getArrayPosition( zIndex, xIndex + 1 ).minus( position );
            left = right.negated();
        }

        ImmutableVector3F normal = new ImmutableVector3F();
        // basically, sum up the normals of each quad this vertex is part of, and take the average
        normal = normal.plus( right.cross( up ).normalized() );
        normal = normal.plus( up.cross( left ).normalized() );
        normal = normal.plus( left.cross( down ).normalized() );
        normal = normal.plus( down.cross( right ).normalized() );
        return normal.normalized();
    }

    private void updateIndexBuffer( int numXSamples, int numZSamples ) {
        indexBuffer.clear();

        // NOTE: index allocation somewhat copied from GridMesh. look there for inspiration

        // create the index information so OpenGL knows how to walk our position indices
        for ( int strip = 0; strip < numZSamples - 1; strip++ ) {
            int stripOffset = strip * numXSamples;

            if ( strip != 0 ) {
                // add in two points that create volume-less triangles (won't render) and keep the winding number the same for the start
                indexBuffer.put( stripOffset + numXSamples - 1 ); // add the last-added point
                indexBuffer.put( stripOffset );
            }

            // each quad is walked over by hitting (in order) upper-left, lower-left, upper-right, lower-right.
            for ( int offset = 0; offset < numXSamples; offset++ ) {
                indexBuffer.put( stripOffset + offset );
                indexBuffer.put( stripOffset + numXSamples + offset ); // down a row
            }
        }
    }

    // TODO: better handling for the enabling/disabling of lighting
    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        // rewind all buffers so we don't overflow
        positionBuffer.rewind();
        normalBuffer.rewind();
        indexBuffer.rewind();

        glEnableClientState( GL_VERTEX_ARRAY );

        if ( options.shouldSendNormals() && isComputeNormals() ) {
            glEnableClientState( GL_NORMAL_ARRAY );
            glNormalPointer( 0, normalBuffer );
        }
        glVertexPointer( 3, 0, positionBuffer );
    }

    @Override public void renderSelf( GLOptions options ) {
        // renders in a series of triangle strips. quad strips rejected since we can't guarantee they will be planar
        glDrawElements( GL_TRIANGLE_STRIP, indexBuffer );
    }

    @Override protected void postRender( GLOptions options ) {
        super.postRender( options );

        // disable the changed states
        glDisableClientState( GL_VERTEX_ARRAY );
        if ( options.shouldSendNormals() && isComputeNormals() ) { glDisableClientState( GL_NORMAL_ARRAY ); }
    }

    public boolean isComputeNormals() {
        return computeNormals;
    }

    public void setComputeNormals( boolean computeNormals ) {
        this.computeNormals = computeNormals;
    }
}
