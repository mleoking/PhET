// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    //Use real masses and positions as much as possible, this ensures reasonable looking orbits and minimizes the differences between tabs
    private static final double SUN_MASS = RealModeList.SUN_MASS;
    private static final double EARTH_MASS = RealModeList.EARTH_MASS;
    private static final double MOON_MASS = RealModeList.MOON_MASS;
    private static final double SPACE_STATION_MASS = RealModeList.SPACE_STATION_MASS;

    private static final double EARTH_ORBITAL_SPEED_AT_PERIHELION = RealModeList.EARTH_ORBITAL_SPEED_AT_PERIHELION;
    private static final double MOON_SPEED = RealModeList.MOON_SPEED * 10;
    private static final double SPACE_STATION_SPEED = RealModeList.SPACE_STATION_SPEED;

    private static final double SUN_RADIUS = RealModeList.SUN_RADIUS * 30;
    private static final double EARTH_RADIUS = RealModeList.EARTH_RADIUS * 1000;
    private static final double MOON_RADIUS = EARTH_RADIUS / 2;
    private static final double SPACE_STATION_RADIUS = 109;

    public static final double EARTH_PERIHELION = RealModeList.EARTH_PERIHELION;
    private static final double MOON_PERIGEE = SUN_RADIUS * 0.7;
    private static final double MOON_X = EARTH_PERIHELION;
    private static final double MOON_Y = MOON_PERIGEE;

    //see http://en.wikipedia.org/wiki/International_Space_Station
    private static final double SPACE_STATION_PERIGEE = 347000;

    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, scaleProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new BodyPrototype( SUN_RADIUS, SUN_MASS, 0, 0 ),
               new BodyPrototype( EARTH_RADIUS, EARTH_MASS, EARTH_PERIHELION, 0 ),
               new BodyPrototype( MOON_RADIUS, MOON_MASS, MOON_X, MOON_Y ),
               new BodyPrototype( SPACE_STATION_RADIUS, SPACE_STATION_MASS, EARTH_PERIHELION + SPACE_STATION_PERIGEE + EARTH_RADIUS, 0 ),
               1.25,
               0, SPACE_STATION_SPEED,
               0, EARTH_ORBITAL_SPEED_AT_PERIHELION,
               MOON_SPEED, EARTH_ORBITAL_SPEED_AT_PERIHELION * 0.8,
               MOON_SPEED, 0,
               GravityAndOrbitsClock.DEFAULT_DT );
    }
}
