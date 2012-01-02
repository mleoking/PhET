// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.lwjglphet.GLMaterial;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.shapes.PlanarPolygon;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.model.regions.Region.Type;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;

/**
 * Visual display of a Region (polygonal area in the cross-section of the earth)
 */
public class RegionNode extends PlanarPolygon {
    private static Map<Region.Type, GLMaterial> materialMap;
    private final Region region;

    public RegionNode( final Region region, final PlateModel model, final PlateTectonicsTab module ) {
        super( region.getBoundary().length );
        this.region = region;

        initializeMaterials( module );

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

                        ImmutableVector2F[] boundary = region.getBoundary();
                        int numVertices = boundary.length;
                        ImmutableVector2F[] vertices = new ImmutableVector2F[numVertices];
                        ImmutableVector2F[] textureCoordinates = new ImmutableVector2F[numVertices];

                        float z = 0; // assume z == 0

                        for ( int i = 0; i < numVertices; i++ ) {
                            float x = boundary[i].x;
                            float y = boundary[i].y;
                            ImmutableVector3F position3d = module.getModelViewTransform().transformPosition( PlateModel.convertToRadial( new ImmutableVector3F( x, y, z ) ) );
                            vertices[i] = new ImmutableVector2F( position3d.x, position3d.y );
                            textureCoordinates[i] = DensityMaterial.densityMap( region.getDensity( boundary[i] ) );
                        }

                        setVertices( vertices, textureCoordinates );
                    }
                }, true );

        // select the material based on the region type
        GLMaterial material = materialMap.get( region.type );
        if ( material != null ) {
            setMaterial( material );
        }
        else {
            setMaterial( new DensityMaterial() );
        }
    }

    // various materials
    private static void initializeMaterials( final PlateTectonicsTab module ) {
        materialMap = new HashMap<Type, GLMaterial>() {{
            put( Type.CRUST, null ); // null for now to indicate a density material
            put( Type.UPPER_MANTLE, null );
            put( Type.LOWER_MANTLE, null );
            put( Type.OUTER_CORE, null );
            put( Type.INNER_CORE, null );
        }};
    }

    public Region getRegion() {
        return region;
    }
}
