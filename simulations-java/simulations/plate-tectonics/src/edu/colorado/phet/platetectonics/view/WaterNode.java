// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.colorado.phet.platetectonics.util.TransparentColorMaterial;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * Displays the top and front of the water, according to the plate model.
 */
public class WaterNode extends Node {
    private final Terrain terrain;
    private final PlateModel model;
    private final PlateTectonicsModule module;

    public WaterNode( final Terrain terrain, PlateModel model, final PlateTectonicsModule module ) {
        this.terrain = terrain;
        this.model = model;
        this.module = module;

        // render the top of the water (flat surface. we rely on OpenGL's intersection handling)
        attachChild( new Geometry( "Water Top" ) {{
            // construct grid mesh
            int rows = terrain.numZSamples;
            int columns = terrain.numXSamples;
            Vector3f[] positions = new Vector3f[rows * columns];
            for ( int zIndex = 0; zIndex < rows; zIndex++ ) {
                float z = terrain.zData[zIndex];
                for ( int xIndex = 0; xIndex < columns; xIndex++ ) {
                    float x = terrain.xData[xIndex];
                    float y = 0;
                    // TODO: performance increases
                    positions[zIndex * columns + xIndex] = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( new Vector3f( x, y, z ) ) );
                }
            }
            setMesh( new GridMesh( rows, columns, positions ) {{
                setUpdateNormals( false );
            }} );

            setMaterial( new TransparentColorMaterial( module.getAssetManager(), new Property<ColorRGBA>( new ColorRGBA( 0.2f, 0.5f, 0.8f, 0.5f ) ) ) );
            setQueueBucket( Bucket.Transparent ); // allow it to be transparent
        }} );

        // render the top of the water. dynamically changes as a strip mesh based on what terrain is above or below sea level
        attachChild( new Geometry( "Water Front", new WaterFrontMesh() ) {{
            setMaterial( new TransparentColorMaterial( module.getAssetManager(), new Property<ColorRGBA>( new ColorRGBA( 0.1f, 0.3f, 0.7f, 0.5f ) ) ) );
            setQueueBucket( Bucket.Transparent ); // allow it to be transparent
        }} );
    }

    /**
     * A mesh that updates to fill in the area of the cross section between the ocean floor and sea level.
     */
    private class WaterFrontMesh extends Mesh {
        public WaterFrontMesh() {
            final int maxVertexCount = terrain.numXSamples * 2;
            final FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
            final FloatBuffer normalBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 3 );
//            final FloatBuffer textureBuffer = BufferUtils.createFloatBuffer( maxVertexCount * 2 );
            final IntBuffer indexBuffer = BufferUtils.createIntBuffer( maxVertexCount );
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
                                Vector3f position = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( xIntercept, 0 ) );
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
                                Vector3f position = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( xIntercept, 0 ) );
                                positionBuffer.put( new float[] {
                                        position.x, position.y, position.z,
                                        position.x, position.y, position.z } );
                                indexBuffer.put( vertexQuantity++ );
                                indexBuffer.put( vertexQuantity++ );
                            }

                            Vector3f topPosition = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( x, 0 ) );
                            Vector3f bottomPosition = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( x, y ) );
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

                                                          getBuffer( Type.Position ).updateData( positionBuffer );
                                                          getBuffer( Type.Index ).updateData( indexBuffer );

                                                          updateBound();
                                                          updateCounts(); // TODO: if this doesn't work, put before buffer calls
//                    getBuffer( Type.TexCoord ).updateData( textureBuffer );
                                                      }
                                                  }, false );

            setMode( Mode.TriangleStrip );
            setBuffer( VertexBuffer.Type.Position, 3, positionBuffer );
            setBuffer( VertexBuffer.Type.Normal, 3, normalBuffer );
//            setBuffer( VertexBuffer.Type.TexCoord, 2, textureBuffer );
            setBuffer( VertexBuffer.Type.Index, 3, indexBuffer );
            updateBound();
            updateCounts();
        }

        // TODO: find out why ray-casting fails on this mesh. Seems to be an Index-buffer related issue
        @Override public int collideWith( Collidable other, Matrix4f worldMatrix, BoundingVolume worldBound, CollisionResults results ) {
            // don't allow ray-casting with this object, due to a bug. see above TODO
            return 0;
        }
    }
}
