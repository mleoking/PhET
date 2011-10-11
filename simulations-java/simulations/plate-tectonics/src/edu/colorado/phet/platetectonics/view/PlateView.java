// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.jmephet.shapes.PlanarPolygon;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.util.TransparentColorMaterial;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * A view (node) that displays everything physical related to a plate model, within the bounds
 * of the specified grid
 */
public class PlateView extends Node {

    public PlateView( final PlateModel model, final PlateTectonicsModule module, final Grid3D grid ) {
        attachChild( new TerrainNode( model, module, grid ) );
//        attachChild( new CrossSectionNode( model, module, grid ) );
        attachChild( new WaterNode( model, module, grid ) );

        // temporary test crust region
        attachChild( new Geometry( "Test Crust" ) {{
            setMesh( new PlanarPolygon( grid.getNumXSamples() * 3 ) {{
                model.modelChanged.addUpdateListener(
                        new UpdateListener() {
                            public void update() {
                                int numSamples = grid.getNumXSamples();
                                int numVertices = numSamples * 2;
                                Vector2f[] vertices = new Vector2f[numVertices];
                                Vector2f[] textureCoordinates = new Vector2f[numVertices];

                                float z = 0;

                                for ( int i = 0; i < numSamples; i++ ) {
                                    float x = grid.getXSample( i );
                                    float y = (float) model.getElevation( x, z );
                                    Vector3f position3d = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( new Vector3f( x, y, z ) ) );
                                    vertices[i] = new Vector2f( position3d.x, position3d.y );
                                    textureCoordinates[i] = new Vector2f( 0, 0 );
                                }
                                for ( int i = 0; i < numSamples; i++ ) {
                                    float x = grid.getXSample( numSamples - i - 1 );
                                    float y = (float) model.getElevation( x, z ) - 5000;
                                    Vector3f position3d = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( new Vector3f( x, y, z ) ) );
                                    vertices[i + numSamples] = new Vector2f( position3d.x, position3d.y );
                                    textureCoordinates[i + numSamples] = new Vector2f( 0, 0 );
                                }

                                setVertices( vertices, textureCoordinates );
                            }
                        }, true );
            }} );
            setMaterial( new TransparentColorMaterial( module.getAssetManager(), new Property<ColorRGBA>( new ColorRGBA( 1, 0, 0, 1f ) ) ) );
            setQueueBucket( Bucket.Transparent );
        }} );
    }
}
