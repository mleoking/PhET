// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;

public class SimpleLinearRegion extends SimpleRegion {

    private float topY;
    private float bottomY;
    private float topDensity;
    private float bottomDensity;
    private float topTemperature;
    private float bottomTemperature;

    public SimpleLinearRegion( Type type, ImmutableVector2F[] top, ImmutableVector2F[] bottom, float topY, float bottomY, float topDensity, float bottomDensity, float topTemperature, float bottomTemperature ) {
        super( type, top, bottom );
        this.topY = topY;
        this.bottomY = bottomY;
        this.topDensity = topDensity;
        this.bottomDensity = bottomDensity;
        this.topTemperature = topTemperature;
        this.bottomTemperature = bottomTemperature;
    }

    @Override public float getDensity( ImmutableVector2F position ) {
        float ratio = ( position.y - bottomY ) / ( topY - bottomY );
        return bottomDensity + ratio * ( topDensity - bottomDensity );
    }

    @Override public float getTemperature( ImmutableVector2F position ) {
        float ratio = ( position.y - bottomY ) / ( topY - bottomY );
        return bottomTemperature + ratio * ( topTemperature - bottomTemperature );
    }

    @Override public boolean isStatic() {
        return false;
    }
}
