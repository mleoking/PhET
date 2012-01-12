// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.regions;

import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public class SimpleConstantRegion extends SimpleRegion {
    private final Function0<Double> density;
    private final Function0<Double> temperature;
    private final boolean aStatic;

    public SimpleConstantRegion( Type type, ImmutableVector2F[] top, ImmutableVector2F[] bottom, Function0<Double> density, Function0<Double> temperature ) {
        this( type, top, bottom, density, temperature, false );
    }

    public SimpleConstantRegion( Type type, ImmutableVector2F[] top, ImmutableVector2F[] bottom, Function0<Double> density, Function0<Double> temperature, boolean isStatic ) {
        super( type, top, bottom );
        this.density = density;
        this.temperature = temperature;
        aStatic = isStatic;
    }

    // constant density over the entire surface
    @Override public float getDensity( ImmutableVector2F position ) {
        return density.apply().floatValue();
    }

    // constant temperature over the entire surface
    @Override public float getTemperature( ImmutableVector2F position ) {
        return temperature.apply().floatValue();
    }

    @Override public boolean isStatic() {
        return aStatic;
    }
}
