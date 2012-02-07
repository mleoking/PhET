// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.TerrainSamplePoint;
import edu.colorado.phet.platetectonics.model.TerrainStrip;
import edu.colorado.phet.platetectonics.view.materials.EarthTexture;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays the top terrain of a plate model, within the bounds of the specified grid
 * <p/>
 * TODO: don't render if we don't have at least 2 columns
 */
public class TerrainStripNode extends GLNode {
    private final TerrainStrip terrainStrip;
    private final LWJGLTransform modelViewTransform;

    private int capacity = 0;
    private int lastNumXSamples = 0;
    private boolean textureRefreshNeeded = true;

    private FloatBuffer positionBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;
    private IntBuffer indexBuffer;

    // used for normal computation
    private ImmutableVector3F[] positionArray;

    public TerrainStripNode( final TerrainStrip terrainStrip, PlateModel model, final LWJGLTransform modelViewTransform ) {
        this.modelViewTransform = modelViewTransform;
        this.terrainStrip = terrainStrip;

        requireEnabled( GL_LIGHTING );
        requireEnabled( GL_COLOR_MATERIAL );

        terrainStrip.elevationChanged.addUpdateListener( new UpdateListener() {
                                                             public void update() {
                                                                 updatePosition();
                                                             }
                                                         }, true );

        terrainStrip.columnsModified.addUpdateListener( new UpdateListener() {
                                                            public void update() {
                                                                textureRefreshNeeded = true;
                                                            }
                                                        }, false );
    }

    private void checkSize() {
        final int neededVertexCount = Math.max( 10, terrainStrip.getNumberOfVertices() );
        if ( capacity < neededVertexCount ) {
            capacity = neededVertexCount;
            positionBuffer = BufferUtils.createFloatBuffer( neededVertexCount * 3 );
            textureBuffer = BufferUtils.createFloatBuffer( neededVertexCount * 2 );
            colorBuffer = BufferUtils.createFloatBuffer( neededVertexCount * 4 );
            normalBuffer = BufferUtils.createFloatBuffer( neededVertexCount * 3 );

            // we build it from strips. there are (rows-1) strips, each strip uses two full rows (columns*2),
            // and for the connections between strips (rows-2 of those), we need 2 extra indices
            int rows = terrainStrip.getNumRows();
            int cols = terrainStrip.getNumColumns();
            int neededIndices = ( rows - 1 ) * cols * 2 + ( rows - 2 ) * 2;
            indexBuffer = BufferUtils.createIntBuffer( neededIndices );
            lastNumXSamples = 0;
        }
    }

    private void updatePosition() {
        checkSize();

        int numXSamples = terrainStrip.getNumColumns();
        int numZSamples = terrainStrip.getNumRows();

        if ( lastNumXSamples != numXSamples ) {
            updateIndexBuffer( numXSamples, numZSamples );
        }

        if ( textureRefreshNeeded ) {
            textureRefreshNeeded = false;

            textureBuffer.clear();
            for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
                for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                    TerrainSamplePoint point = terrainStrip.getSample( xIndex, zIndex );
                    textureBuffer.put( new float[] { point.getTextureCoordinates().x, point.getTextureCoordinates().y } );
                }
            }
        }

        positionBuffer.clear();
        colorBuffer.clear();
        normalBuffer.clear();

        // used for normal computation
        positionArray = new ImmutableVector3F[numXSamples * numZSamples];

        // used for faster trig calculations for positioning
        ImmutableVector3F[] xRadials = new ImmutableVector3F[numXSamples];
        ImmutableVector3F[] zRadials = new ImmutableVector3F[numZSamples];

        for ( int i = 0; i < numXSamples; i++ ) {
            xRadials[i] = PlateModel.getXRadialVector( terrainStrip.xPositions.get( i ) );
        }
        for ( int i = 0; i < numZSamples; i++ ) {
            zRadials[i] = PlateModel.getZRadialVector( terrainStrip.zPositions.get( i ) );
        }

        /*---------------------------------------------------------------------------*
        * position buffer (and array, used later for normals)
        *----------------------------------------------------------------------------*/
        for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
            ImmutableVector3F zRadial = zRadials[zIndex];
            for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                float elevation = terrainStrip.getSample( xIndex, zIndex ).getElevation();
                ImmutableVector3F cartesianModelVector = PlateModel.convertToRadial( xRadials[xIndex], zRadial, elevation );
                ImmutableVector3F viewVector = modelViewTransform.transformPosition( cartesianModelVector );

                // fill the position array, so we can compute normals in a second
                positionArray[zIndex * numXSamples + xIndex] = viewVector;

                // fill in the buffer while we are at it
                positionBuffer.put( new float[] { viewVector.x, viewVector.y, viewVector.z } );
            }
        }

        /*---------------------------------------------------------------------------*
        * normal buffer
        *----------------------------------------------------------------------------*/
        for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
            for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                ImmutableVector3F normal = getArrayNormal( zIndex, xIndex );
                normalBuffer.put( new float[] { normal.x, normal.y, normal.z } );
            }
        }

        /*---------------------------------------------------------------------------*
        * color buffer
        *----------------------------------------------------------------------------*/
        for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
            for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                float elevation = terrainStrip.getSample( xIndex, zIndex ).getElevation();

                if ( elevation < 0 ) {
                    float value = (float) MathUtil.clamp( 0, ( elevation + 10000 ) / 20000, 1 );
                    colorBuffer.put( new float[] { value, value, value, 1 } );
                }
                else {
                    float grassRed = (float) ( PlateTectonicsConstants.EARTH_GREEN.getRed() ) / 255f;
                    float grassGreen = (float) ( PlateTectonicsConstants.EARTH_GREEN.getGreen() ) / 255f;
                    float grassBlue = (float) ( PlateTectonicsConstants.EARTH_GREEN.getBlue() ) / 255f;
                    if ( elevation < 3000 ) {
                        colorBuffer.put( new float[] { grassRed, grassGreen, grassBlue, 1 } );
                    }
                    else {
                        float ratioToSnow = ( elevation - 3000 ) / 7000;
                        if ( ratioToSnow > 1 ) {
                            ratioToSnow = 1;
                        }
                        colorBuffer.put( new float[] {
                                grassRed + ( 0.8f - grassRed ) * ratioToSnow,
                                grassGreen + ( 0.8f - grassGreen ) * ratioToSnow,
                                grassBlue + ( 0.8f - grassBlue ) * ratioToSnow,
                                1 } );
                    }
                }
            }
        }
    }

    private ImmutableVector3F getArrayPosition( int zIndex, int xIndex ) {
        return positionArray[zIndex * terrainStrip.getNumColumns() + xIndex];
    }

    // adapted from GridMesh
    private ImmutableVector3F getArrayNormal( int zIndex, int xIndex ) {
        // basic approximation based on neighbors.
        ImmutableVector3F position = getArrayPosition( zIndex, xIndex );
        ImmutableVector3F up, down, left, right;

        // calculate up/down vectors
        if ( zIndex > 0 ) {
            up = getArrayPosition( zIndex - 1, xIndex ).minus( position );
            down = ( zIndex < terrainStrip.getNumRows() - 1 ) ? getArrayPosition( zIndex + 1, xIndex ).minus( position ) : up.negated();
        }
        else {
            down = getArrayPosition( zIndex + 1, xIndex ).minus( position );
            up = down.negated();
        }

        // calculate left/right vectors
        if ( xIndex > 0 ) {
            left = getArrayPosition( zIndex, xIndex - 1 ).minus( position );
            right = ( xIndex < terrainStrip.getNumColumns() - 1 ) ? getArrayPosition( zIndex, xIndex + 1 ).minus( position ) : left.negated();
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
        textureBuffer.rewind();
        indexBuffer.rewind();
        colorBuffer.rewind();

        glEnableClientState( GL_COLOR_ARRAY );
        glColorMaterial( GL_FRONT, GL_DIFFUSE );
        glColorPointer( 4, 0, colorBuffer );

        glEnableClientState( GL_VERTEX_ARRAY );
        if ( options.shouldSendTexture() ) {
            glEnableClientState( GL_TEXTURE_COORD_ARRAY );
            glTexCoordPointer( 2, 0, textureBuffer );
        }
        if ( options.shouldSendNormals() ) {
            glEnableClientState( GL_NORMAL_ARRAY );
            glNormalPointer( 0, normalBuffer );
        }
        glVertexPointer( 3, 0, positionBuffer );

        EarthTexture.begin();
    }

    @Override public void renderSelf( GLOptions options ) {
        // renders in a series of triangle strips. quad strips rejected since we can't guarantee they will be planar
        glDrawElements( GL_TRIANGLE_STRIP, indexBuffer );
    }

    @Override protected void postRender( GLOptions options ) {
        super.postRender( options );

        EarthTexture.end();

        // disable the changed states
        glDisableClientState( GL_VERTEX_ARRAY );
        if ( options.shouldSendTexture() ) { glDisableClientState( GL_TEXTURE_COORD_ARRAY ); }
        if ( options.shouldSendNormals() ) { glDisableClientState( GL_NORMAL_ARRAY ); }
        glDisableClientState( GL_COLOR_ARRAY );
    }

    public TerrainStrip getTerrainStrip() {
        return terrainStrip;
    }
}