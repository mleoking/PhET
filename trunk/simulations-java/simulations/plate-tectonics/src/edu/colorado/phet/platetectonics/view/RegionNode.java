// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.lwjglphet.GLMaterial;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.shapes.PlanarPolygon;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Region;
import edu.colorado.phet.platetectonics.model.Region.Type;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.ColorMaterial;

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
                            ImmutableVector3F position3d = module.getModelViewTransform().modelToView( PlateModel.convertToRadial( new ImmutableVector3F( x, y, z ) ) );
                            vertices[i] = new ImmutableVector2F( position3d.x, position3d.y );
                            textureCoordinates[i] = new ImmutableVector2F( region.getDensity( boundary[i] ), 0 ); // TODO: handle texture coordinates depending on region type
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
            setMaterial( new DensityMaterial( new Function0<Float>() {
                public Float apply() {
                    // TODO: replace with continuous-based texture version that doesn't depend on the particular region
                    return region.getDensity( new ImmutableVector2F() );
                }
            } ) );
        }
    }

    // various materials
    private static void initializeMaterials( final PlateTectonicsTab module ) {
        materialMap = new HashMap<Type, GLMaterial>() {{
            put( Type.CRUST, null ); // null for now to indicate a density material
            put( Type.UPPER_MANTLE, null );
            put( Type.LOWER_MANTLE, new ColorMaterial( 0.7f, 0.7f, 0, 1f ) );
            put( Type.OUTER_CORE, new ColorMaterial( 0.3f, 0.3f, 0.3f, 1f ) );
            put( Type.INNER_CORE, new ColorMaterial( 0.6f, 0.6f, 0.6f, 1f ) );
        }};
    }

    public static class DensityMaterial extends GLMaterial {

        private final Function0<Float> densityFunc;

        public DensityMaterial( Function0<Float> density ) {
            this.densityFunc = density;
        }

        @Override public void before( GLOptions options ) {
            float density = densityFunc.apply();
            float minDensityToShow = 2500;
            float maxDensityToShow = 3500;

            float x = 100f + ( 1f - ( density - minDensityToShow ) / ( maxDensityToShow - minDensityToShow ) ) * 155f;
            x = (float) MathUtil.clamp( 0.0, x / 255.0, 1.0 ); // clamp it in the normal range
            GL11.glColor4f( x, x, x, 1f );
        }
    }
}
