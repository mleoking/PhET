// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, scaleProperty, stepping, rewinding, timeSpeedScaleProperty ),
               //Use real masses and positions as much as possible, this ensures reasonable looking orbits and minimizes the differences between tabs
               new BodyPrototype( SUN_MASS, 0, 0 ),
               new BodyPrototype( EARTH_MASS, EARTH.x, EARTH.y ),
               new BodyPrototype( MOON_MASS, MOON_X, MOON_Y ),
               new BodyPrototype( SPACE_STATION_MASS, SPACE_STATION.x, SPACE_STATION.y ),
               1.25,
               0, SPACE_STATION_SPEED,
               0, EARTH_ORBITAL_SPEED_AT_PERIHELION,
               MOON_SPEED, EARTH_ORBITAL_SPEED_AT_PERIHELION * 0.8,
               MOON_SPEED, 0,
               GravityAndOrbitsClock.DEFAULT_DT,
               400,
               21600,
               //Keeping the scale factors similar keeps both objects closer to the same scale
               SUN_RADIUS * 50, EARTH_RADIUS * 500,
               SUN_RADIUS * 50, EARTH_RADIUS * 500, MOON_RADIUS * 10000,
               EARTH_RADIUS * 15, MOON_RADIUS * 15,
               EARTH_RADIUS * 0.9, SPACE_STATION_RADIUS * 3 );
    }
}
