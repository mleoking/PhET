// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.regions.Region;

import static edu.colorado.phet.platetectonics.model.PlateMotionModel.*;

public class PlateMotionPlate extends Plate {
    public PlateMotionPlate( final PlateMotionModel model, final TextureStrategy textureStrategy, final boolean isLeftPlate ) {
        addMantle( new Region( MANTLE_VERTICAL_SAMPLES, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, SamplePoint>() {
            public SamplePoint apply( Integer yIndex, Integer xIndex ) {
                // start with top first
                float x = isLeftPlate ? model.getLeftX( xIndex ) : model.getRightX( xIndex );
                final float yRatio = ( (float) yIndex ) / ( (float) MANTLE_VERTICAL_SAMPLES - 1 );
                float y = SIMPLE_MANTLE_TOP_Y + ( SIMPLE_MANTLE_BOTTOM_Y - SIMPLE_MANTLE_TOP_Y ) * yRatio;
                float temp = SIMPLE_MANTLE_TOP_TEMP + ( SIMPLE_MANTLE_BOTTOM_TEMP - SIMPLE_MANTLE_TOP_TEMP ) * yRatio;
                return new SamplePoint( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                                        textureStrategy.mapFront( new ImmutableVector2F( x, y ) ) );
            }
        } ) );
        addTerrain( textureStrategy, TERRAIN_DEPTH_SAMPLES, model.getBounds().getMinZ(), model.getBounds().getMaxZ() );
    }
}
