// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.jmephet.shapes.PlanarPolygon;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Region;
import edu.colorado.phet.platetectonics.model.Region.Type;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.colorado.phet.platetectonics.util.TransparentColorMaterial;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * Visual display of a Region (polygonal area in the cross-section of the earth)
 */
public class RegionNode extends Node {
    private static Map<Region.Type, Material> materialMap;

    public RegionNode( final Region region, final PlateModel model, final PlateTectonicsModule module ) {
        super( "Region" );

        initializeMaterials( module );

        attachChild( new Geometry( "Region Geometry" ) {{

            // use a polygon to display the mesh
            setMesh( new PlanarPolygon( region.getBoundary().length ) {{

                // update it whenever the model has changed
                model.modelChanged.addUpdateListener(
                        new UpdateListener() {
                            private boolean updated = false;

                            public void update() {
                                // don't update static regions
                                if ( updated && region.isStatic() ) {
                                    return;
                                }
                                updated = true;

                                Vector2f[] boundary = region.getBoundary();
                                int numVertices = boundary.length;
                                Vector2f[] vertices = new Vector2f[numVertices];
                                Vector2f[] textureCoordinates = new Vector2f[numVertices];

                                float z = 0; // assume z == 0

                                for ( int i = 0; i < numVertices; i++ ) {
                                    float x = boundary[i].x;
                                    float y = boundary[i].y;
                                    Vector3f position3d = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( new Vector3f( x, y, z ) ) );
                                    vertices[i] = new Vector2f( position3d.x, position3d.y );
                                    textureCoordinates[i] = new Vector2f( region.getDensity( boundary[i] ), 0 ); // TODO: handle texture coordinates depending on region type
                                }

                                setVertices( vertices, textureCoordinates );
                            }
                        }, true );
            }} );

            // select the material based on the region type
            setMaterial( materialMap.get( region.type ) );

            // allow transparency?
            setQueueBucket( Bucket.Transparent );
        }} );
    }

    // various materials
    private static void initializeMaterials( final PlateTectonicsModule module ) {
        final Material densityMaterial = new Material( module.getAssetManager(), "plate-tectonics/materials/Density.j3md" ) {{
            setColor( "Color", new ColorRGBA( 0, 1, 0, 1f ) );
        }};
        materialMap = new HashMap<Type, Material>() {{
            put( Type.CRUST, densityMaterial );

            put( Type.UPPER_MANTLE, densityMaterial );

            put( Type.LOWER_MANTLE,
                 new TransparentColorMaterial( module.getAssetManager(), new Property<ColorRGBA>( new ColorRGBA( 0.7f, 0.7f, 0, 1f ) ) ) );

            put( Type.OUTER_CORE,
                 new TransparentColorMaterial( module.getAssetManager(), new Property<ColorRGBA>( new ColorRGBA( 0.3f, 0.3f, 0.3f, 1f ) ) ) );

            put( Type.INNER_CORE,
                 new TransparentColorMaterial( module.getAssetManager(), new Property<ColorRGBA>( new ColorRGBA( 0.6f, 0.6f, 0.6f, 1f ) ) ) );
        }};
    }
}
