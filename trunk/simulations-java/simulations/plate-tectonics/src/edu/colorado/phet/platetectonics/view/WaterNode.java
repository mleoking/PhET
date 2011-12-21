// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.shapes.GridMesh;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.ColorMaterial;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays the top and front of the water, according to the plate model.
 */
public class WaterNode extends GLNode {
    private final Terrain terrain;
    private final PlateModel model;
    private final PlateTectonicsTab module;

    public WaterNode( final Terrain terrain, PlateModel model, final PlateTectonicsTab module ) {
        this.terrain = terrain;
        this.model = model;
        this.module = module;

        int rows = terrain.numZSamples;
        int columns = terrain.numXSamples;
        ImmutableVector3F[] positions = new ImmutableVector3F[rows * columns];
        for ( int zIndex = 0; zIndex < rows; zIndex++ ) {
            float z = terrain.zData[zIndex];
            for ( int xIndex = 0; xIndex < columns; xIndex++ ) {
                float x = terrain.xData[xIndex];
                float y = 0;
                // TODO: performance increases
                positions[zIndex * columns + xIndex] = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( new ImmutableVector3F( x, y, z ) ) );
            }
        }
        addChild( new GridMesh( rows, columns, positions ) {{
            setUpdateNormals( false );
            setMaterial( new ColorMaterial( 0.2f, 0.5f, 0.8f, 0.5f ) );
            setRenderPass( RenderPass.TRANSPARENCY );
        }} );

        // render the top of the water. dynamically changes as a strip mesh based on what terrain is above or below sea level
        addChild( new WaterFrontMesh() {{
            setMaterial( new ColorMaterial( 0.1f, 0.3f, 0.7f, 0.5f ) );
            setRenderPass( RenderPass.TRANSPARENCY );
        }} );
    }

    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        // don't write to the depth buffer
//            glDepthMask( false );
        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
    }

    @Override protected void postRender( GLOptions options ) {
        super.postRender( options );

        // write to the depth buffer again
//            glDepthMask( true );
        glDisable( GL_BLEND );
    }

    /**
     * A mesh that updates to fill in the area of the cross section between the ocean floor and sea level.
     */
    private class WaterFrontMesh extends GLNode {

        final FloatBuffer positionBuffer;
        final FloatBuffer normalBuffer;
        final IntBuffer indexBuffer;

        public WaterFrontMesh() {
            final int maxVertexCount = terrain.numXSamples * 2;
            positionBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
            normalBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
            indexBuffer = BufferUtils.createIntBuffer( maxVertexCount );
            // TODO: update front water mesh at each timestep!

            for ( int i = 0; i < maxVertexCount; i++ ) {
                normalBuffer.put( new float[] { 0, 0, 1 } );
            }

            final Runnable setPositions = new Runnable() {
                public void run() {
                    positionBuffer.clear();
//                    textureBuffer.clear();
                    indexBuffer.clear();

                    float z = 0; // assuming water front at z=0
                    int frontZIndex = terrain.getFrontZIndex();
                    int X_SAMPLES = terrain.numXSamples;

                    // whether our last x sample was above the sea level (or equal to it)
                    boolean lastAboveSeaLevel = terrain.getElevation( 0, frontZIndex ) >= 0;

                    // last coordinates
                    float lastX = 0;
                    float lastY = 0;

                    // used for counting how many vertices we have added
                    int vertexQuantity = 0;

                    // TODO: separate out the coordinate finding parts with the drawing parts!!!

                    for ( int xIndex = 0; xIndex < X_SAMPLES; xIndex++ ) {
                        float x = terrain.xData[xIndex];
                        float y = terrain.getElevation( xIndex, frontZIndex );
                        if ( y >= 0 ) {
                            // elevation above sea level, don't show any more

                            if ( !lastAboveSeaLevel ) {
                                // if our last x sample was below sea level, we need to close off our polygon
                                float xIntercept = lastX - lastY * ( x - lastX ) / ( y - lastY );

                                assert xIntercept >= lastX;
                                assert xIntercept <= x;

                                // put in two vertices at the same location, where we compute the estimated y would be zero
                                ImmutableVector3F position = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( xIntercept, 0 ) );
                                positionBuffer.put( new float[] {
                                        position.x, position.y, position.z,
                                        position.x, position.y, position.z } );
                                indexBuffer.put( vertexQuantity++ );
                                indexBuffer.put( vertexQuantity++ );
                            }
                        }
                        else {
                            // elevation below sea level. draw water

                            if ( lastAboveSeaLevel ) {
                                // we need to compute where the x-intercept of our segment is so we start our polygon where the elevation is 0
                                float xIntercept = lastX - lastY * ( x - lastX ) / ( y - lastY );

                                assert xIntercept >= lastX;
                                assert xIntercept <= x;

                                // put in two vertices at the same location, where we compute the estimated y would be zero
                                ImmutableVector3F position = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( xIntercept, 0 ) );
                                positionBuffer.put( new float[] {
                                        position.x, position.y, position.z,
                                        position.x, position.y, position.z } );
                                indexBuffer.put( vertexQuantity++ );
                                indexBuffer.put( vertexQuantity++ );
                            }

                            ImmutableVector3F topPosition = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( x, 0 ) );
                            ImmutableVector3F bottomPosition = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( x, y ) );
                            positionBuffer.put( new float[] {
                                    topPosition.x, topPosition.y, topPosition.z,
                                    bottomPosition.x, bottomPosition.y, bottomPosition.z
                            } );
                            indexBuffer.put( vertexQuantity++ );
                            indexBuffer.put( vertexQuantity++ );
                        }
                        lastX = x;
                        lastY = y;
                        lastAboveSeaLevel = y >= 0;
                    }

                    // since the number of vertices may change, we need to manually update the buffer limits so that JME doesn't render beyond these
                    positionBuffer.limit( vertexQuantity * 3 );
                    indexBuffer.limit( vertexQuantity );
                }
            };
            setPositions.run();

            model.modelChanged.addUpdateListener( new UpdateListener() {
                                                      public void update() {
                                                          setPositions.run();
                                                      }
                                                  }, false );
        }

        @Override public void renderSelf( GLOptions options ) {
            super.renderSelf( options );

            positionBuffer.rewind();
            normalBuffer.rewind();
            indexBuffer.rewind();

            // TODO: seeing a lot of this type of code. refactor away if possible
            // initialize the needed states
            glEnableClientState( GL_VERTEX_ARRAY );
            if ( options.shouldSendNormals() ) {
                glEnableClientState( GL11.GL_NORMAL_ARRAY );
                glNormalPointer( 3, normalBuffer );
            }
            glVertexPointer( 3, 0, positionBuffer );

            // renders in a series of triangle strips. quad strips rejected since we can't guarantee they will be planar
            glDrawElements( GL_TRIANGLE_STRIP, indexBuffer );

            // disable the changed states
            glDisableClientState( GL_VERTEX_ARRAY );
            if ( options.shouldSendNormals() ) { glDisableClientState( GL11.GL_NORMAL_ARRAY ); }
        }
    }
}
