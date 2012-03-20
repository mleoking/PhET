// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.view.materials.EarthTexture;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays the top terrain of a plate model, within the bounds of the specified grid
 * <p/>
 * TODO: don't render if we don't have at least 2 columns
 */
public class TerrainNode extends GridStripNode {
    private final Terrain terrainStrip;

    private boolean textureRefreshNeeded = true;

    private FloatBuffer textureBuffer;
    private FloatBuffer colorBuffer;


    public TerrainNode( final Terrain terrainStrip, final LWJGLTransform modelViewTransform ) {
        super( modelViewTransform );
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

    @Override public int getNumberOfVertices() {
        return terrainStrip.getNumberOfVertices();
    }

    @Override public int getNumColumns() {
        return terrainStrip.getNumColumns();
    }

    @Override public int getNumRows() {
        return terrainStrip.getNumRows();
    }

    @Override public float getXPosition( int xIndex ) {
        return terrainStrip.xPositions.get( xIndex );
    }

    @Override public float getZPosition( int zIndex ) {
        return terrainStrip.zPositions.get( zIndex );
    }

    @Override public float getElevation( int xIndex, int zIndex ) {
        return terrainStrip.getSample( xIndex, zIndex ).getElevation();
    }

    @Override public void setCapacity( int capacity ) {
        super.setCapacity( capacity );
        textureBuffer = BufferUtils.createFloatBuffer( capacity * 2 );
        colorBuffer = BufferUtils.createFloatBuffer( capacity * 4 );
    }

    @Override protected void updatePosition() {
        super.updatePosition();

        int numXSamples = terrainStrip.getNumColumns();
        int numZSamples = terrainStrip.getNumRows();

        if ( textureRefreshNeeded ) {
            textureRefreshNeeded = false;

            textureBuffer.clear();
            for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
                for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                    TerrainSample point = terrainStrip.getSample( xIndex, zIndex );
                    textureBuffer.put( new float[] { point.getTextureCoordinates().x, point.getTextureCoordinates().y } );
                }
            }
        }

        colorBuffer.clear();

        /*---------------------------------------------------------------------------*
        * color buffer
        *----------------------------------------------------------------------------*/
        float grassRed = (float) ( PlateTectonicsConstants.EARTH_GREEN.getRed() ) / 255f;
        float grassGreen = (float) ( PlateTectonicsConstants.EARTH_GREEN.getGreen() ) / 255f;
        float grassBlue = (float) ( PlateTectonicsConstants.EARTH_GREEN.getBlue() ) / 255f;

        for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
            for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                float elevation = terrainStrip.getSample( xIndex, zIndex ).getElevation();

                // blend elevations below a certain window
                if ( elevation < 1000 ) {
                    float value = (float) MathUtil.clamp( 0, ( elevation + 7000 ) / 10000, 1 );
                    if ( elevation < -500 ) {
                        // below this, no blend, and just straight ocean floor coloring based on depth
                        colorBuffer.put( new float[] { value, value, value, 1 } );
                    }
                    else {
                        // between 1km and -0.5km, we blend together the land and ocean terrain colors
                        // -0.5km is chosen since this is the elevation of the mid-oceanic ridge
                        float blend = (float) MathUtil.clamp( 0, -( elevation - 1000 ) / 1500, 1 );
                        colorBuffer.put( new float[] {
                                value * blend + grassRed * ( 1 - blend ),
                                value * blend + grassGreen * ( 1 - blend ),
                                value * blend + grassBlue * ( 1 - blend ),
                                1
                        } );
                    }
                }
                else {
                    if ( elevation < 3000 ) {
                        // straight grass below 3k
                        colorBuffer.put( new float[] { grassRed, grassGreen, grassBlue, 1 } );
                    }
                    else {
                        // otherwise, we need to blend to snow (white) at the top
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

    // TODO: better handling for the enabling/disabling of lighting
    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        // rewind all buffers so we don't overflow
        textureBuffer.rewind();
        colorBuffer.rewind();

        glEnableClientState( GL_COLOR_ARRAY );
        glColorMaterial( GL_FRONT, GL_DIFFUSE );
        glColorPointer( 4, 0, colorBuffer );

        if ( options.shouldSendTexture() ) {
            glEnableClientState( GL_TEXTURE_COORD_ARRAY );
            glTexCoordPointer( 2, 0, textureBuffer );
        }

        EarthTexture.begin();
    }

    @Override protected void postRender( GLOptions options ) {
        super.postRender( options );

        EarthTexture.end();

        // disable the changed states
        if ( options.shouldSendTexture() ) { glDisableClientState( GL_TEXTURE_COORD_ARRAY ); }
        glDisableClientState( GL_COLOR_ARRAY );
    }

    public Terrain getTerrainStrip() {
        return terrainStrip;
    }
}