// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class RealModeList extends ModeList {
    // Add in some initial -x velocity to offset the earth-moon barycenter drift
    //This value was computed by sampling the total momentum in GravityAndOrbitsModel for this mode
    static ImmutableVector2D sampledSystemMomentum = new ImmutableVector2D( 7.421397422188586E25, -1.080211713202125E22 );
    static ImmutableVector2D velocityOffset = sampledSystemMomentum.getScaledInstance( -1 / ( EARTH_MASS + MOON_MASS ) );

    public RealModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, scaleProperty, stepping, rewinding, timeSpeedScaleProperty ),
               SUN,
               EARTH,
               MOON,
               SPACE_STATION,
               1.25,
               400, 21600, 0, SPACE_STATION_SPEED,
               0, EARTH_ORBITAL_SPEED_AT_PERIHELION,
               MOON_SPEED, EARTH_ORBITAL_SPEED_AT_PERIHELION,
               MOON_SPEED, 0,
               GravityAndOrbitsClock.DEFAULT_DT,
               SUN_RADIUS, EARTH_RADIUS, SUN_RADIUS, EARTH_RADIUS, MOON_RADIUS, EARTH_RADIUS, MOON_RADIUS, EARTH_RADIUS, SPACE_STATION_RADIUS );
    }
}
