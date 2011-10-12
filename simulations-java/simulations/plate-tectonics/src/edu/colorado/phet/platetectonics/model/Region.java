// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import com.jme3.math.Vector2f;

/**
 * A region models an internal polygon inside the earth in the z=0 planeP
 */
public abstract class Region {

    public final Type type;

    protected Region( Type type ) {
        this.type = type;
    }

    public abstract float getDensity( Vector2f position );

    public abstract float getTemperature( Vector2f position );

    public abstract boolean isStatic();

    /**
     * @return The outside boundary of the region. Can be concave, but should have no holes
     */
    public abstract Vector2f[] getBoundary();

    public static enum Type {
        CRUST,
        UPPER_MANTLE,
        LOWER_MANTLE,
        OUTER_CORE,
        INNER_CORE
    }
}
