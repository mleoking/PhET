// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateTectonicsModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.tabs.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.ColorMaterial;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;

/**
 * Displays the top and front of the water, according to the plate model.
 */
public class WaterStripNode extends GLNode {
    private final Terrain terrain;
    private final PlateTectonicsModel model;
    private final PlateTectonicsTab module;

    public WaterStripNode( final Terrain terrain, PlateTectonicsModel model, final PlateTectonicsTab module ) {
        this.terrain = terrain;
        this.model = model;
        this.module = module;

        // allow blending of the alpha in the water
        requireEnabled( GL_BLEND );

        // the top is effectively just a curved mesh on top. much simpler
        addChild( new WaterTopMesh( module.getModelViewTransform() ) {{
            setMaterial( new ColorMaterial( 0.2f, 0.5f, 0.8f, 0.5f ) );
            setRenderPass( RenderPass.TRANSPARENCY );

            // TODO: can we narrow down when this needs to update?
            terrain.elevationChanged.addUpdateListener( new UpdateListener() {
                public void update() {
                    updatePosition();
                }
            }, true );
        }} );

        // render the front of the water. dynamically changes as a strip mesh based on what terrain is above or below sea level
        addChild( new WaterFrontMesh() {{
            setMaterial( new ColorMaterial( 0.1f, 0.3f, 0.7f, 0.5f ) );
            setRenderPass( RenderPass.TRANSPARENCY );
        }} );
    }

    /**
     * Just a curved plane basically. We rely on OpenGL's depth tests for the terrain to "cut" through the water so we only see water where the
     * terrain is below sea-level. GridStripNode (superclass) takes care of the curvature.
     */
    private class WaterTopMesh extends GridStripNode {
        public WaterTopMesh( final LWJGLTransform modelViewTransform ) {
            super( modelViewTransform );

            setComputeNormals( false );
        }

        @Override public float getElevation( int xIndex, int zIndex ) {
            return 0;
        }

        @Override public int getNumberOfVertices() {
            return terrain.getNumberOfVertices();
        }

        @Override public int getNumColumns() {
            return terrain.getNumColumns();
        }

        @Override public int getNumRows() {
            return terrain.getNumRows();
        }

        @Override public float getXPosition( int xIndex ) {
            return terrain.xPositions.get( xIndex );
        }

        @Override public float getZPosition( int zIndex ) {
            return terrain.zPositions.get( zIndex );
        }
    }

    /**
     * A mesh that updates to fill in the area of the cross section between the ocean floor and sea level.
     */
    private class WaterFrontMesh extends GLNode {

        private FloatBuffer positionBuffer;
        private IntBuffer indexBuffer;

        private int capacity = 0;

        public WaterFrontMesh() {
            final int maxVertexCount = terrain.getNumColumns() * 2;
            positionBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
            indexBuffer = BufferUtils.createIntBuffer( maxVertexCount );

            // we essentially create a triangle strip that needs to determine where the front terran crosses sea-level
            final Runnable setPositions = new Runnable() {
                public void run() {
                    checkSize();

                    positionBuffer.clear();
                    indexBuffer.clear();

                    int frontZIndex = terrain.getFrontZIndex();
                    int X_SAMPLES = terrain.getNumColumns();

                    // whether our last x sample was above the sea level (or equal to it)
                    boolean lastAboveSeaLevel = terrain.getSample( 0, frontZIndex ).getElevation() >= 0;

                    // last coordinates
                    float lastX = 0;
                    float lastY = 0;

                    // used for counting how many vertices we have added
                    int vertexQuantity = 0;

                    float z = terrain.zPositions.get( terrain.getFrontZIndex() );

                    // TODO: separate out the coordinate finding parts with the drawing parts!!!

                    for ( int xIndex = 0; xIndex < X_SAMPLES; xIndex++ ) {
                        float x = terrain.xPositions.get( xIndex );
                        final TerrainSample sample = terrain.getSample( xIndex, frontZIndex );
                        float y = sample.getElevation();
                        if ( y >= 0 ) {
                            // elevation above sea level, don't show any more

                            if ( !lastAboveSeaLevel ) {
                                // if our last x sample was below sea level, we need to close off our polygon
                                float xIntercept = lastX - lastY * ( x - lastX ) / ( y - lastY );

                                assert xIntercept >= lastX;
                                assert xIntercept <= x;

                                // put in two vertices at the same location, where we compute the estimated y would be zero
                                Vector3F position = module.getModelViewTransform().transformPosition( z == 0
                                                                                                      ? PlateTectonicsModel.convertToRadial( xIntercept, 0 )
                                                                                                      : PlateTectonicsModel.convertToRadial( new Vector3F( xIntercept, 0, z ) ) );
                                positionBuffer.put( new float[]{
                                        position.x, position.y, position.z,
                                        position.x, position.y, position.z} );
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
                                Vector3F position = module.getModelViewTransform().transformPosition( z == 0
                                                                                                      ? PlateTectonicsModel.convertToRadial( xIntercept, 0 )
                                                                                                      : PlateTectonicsModel.convertToRadial( new Vector3F( xIntercept, 0, z ) ) );
                                positionBuffer.put( new float[]{
                                        position.x, position.y, position.z,
                                        position.x, position.y, position.z} );
                                indexBuffer.put( vertexQuantity++ );
                                indexBuffer.put( vertexQuantity++ );
                            }

                            Vector3F topPosition = module.getModelViewTransform().transformPosition( z == 0
                                                                                                     ? PlateTectonicsModel.convertToRadial( x, 0 )
                                                                                                     : PlateTectonicsModel.convertToRadial( new Vector3F( x, 0, z ) ) );
                            Vector3F bottomPosition = module.getModelViewTransform().transformPosition( z == 0
                                                                                                        ? PlateTectonicsModel.convertToRadial( x, y )
                                                                                                        : PlateTectonicsModel.convertToRadial( new Vector3F( x, y, z ) ) );
                            positionBuffer.put( new float[]{
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

        private void checkSize() {
            // very pessimistic bound for now, because I don't want to worry about it
            final int maxVertexCount = terrain.getNumColumns() * 3;
            if ( capacity < maxVertexCount ) {
                capacity = maxVertexCount;
                positionBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
                indexBuffer = BufferUtils.createIntBuffer( maxVertexCount );
            }
        }

        @Override public void renderSelf( GLOptions options ) {
            super.renderSelf( options );

            positionBuffer.rewind();
            indexBuffer.rewind();

            // TODO: seeing a lot of this type of code. refactor away if possible
            // initialize the needed states
            glEnableClientState( GL_VERTEX_ARRAY );
            glVertexPointer( 3, 0, positionBuffer );

            // renders in a series of triangle strips. quad strips rejected since we can't guarantee they will be planar
            glDrawElements( GL_TRIANGLE_STRIP, indexBuffer );

            // disable the changed states
            glDisableClientState( GL_VERTEX_ARRAY );
        }
    }
}
