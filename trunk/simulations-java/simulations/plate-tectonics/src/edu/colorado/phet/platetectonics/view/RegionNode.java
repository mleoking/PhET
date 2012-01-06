// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.shapes.PlanarPolygon;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;

/**
 * Visual display of a Region (polygonal area in the cross-section of the earth)
 */
public class RegionNode extends PlanarPolygon {
    private final Region region;

    public RegionNode( final Region region, final PlateModel model, final PlateTectonicsTab tab ) {
        super( region.getBoundary().length );
        this.region = region;

        // update it whenever the model has changed
        final UpdateListener listener = new UpdateListener() {
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
                    ImmutableVector3F position3d = tab.getModelViewTransform().transformPosition( PlateModel.convertToRadial( new ImmutableVector3F( x, y, z ) ) );
                    vertices[i] = new ImmutableVector2F( position3d.x, position3d.y );

                    // TODO: make a material superclass for this
                    switch( tab.colorMode.get() ) {
                        case DENSITY:
                            textureCoordinates[i] = DensityMaterial.densityMap( region.getDensity( boundary[i] ) );
                            break;
                        case TEMPERATURE:
                            textureCoordinates[i] = TemperatureMaterial.temperatureMap( region.getTemperature( boundary[i] ) );
                            break;
                    }

                }

                setVertices( vertices, textureCoordinates );
            }
        };
        model.modelChanged.addUpdateListener( listener, true );

        tab.colorMode.addObserver( new SimpleObserver() {
            public void update() {
                switch( tab.colorMode.get() ) {
                    case DENSITY:
                        setMaterial( new DensityMaterial() );
                        break;
                    case TEMPERATURE:
                        setMaterial( new TemperatureMaterial() );
                        break;
                }
                listener.update();
            }
        } );
    }

    public Region getRegion() {
        return region;
    }
}
