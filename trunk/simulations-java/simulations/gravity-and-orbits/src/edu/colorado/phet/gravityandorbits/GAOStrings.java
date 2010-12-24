/* Copyright 2010, University of Colorado */

package edu.colorado.phet.gravityandorbits;

/**
 * A collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class GAOStrings {

    /* not intended for instantiation */
    private GAOStrings() {}
    
    public static final String BILLION_BILLION_SPACE_STATION_MASSES = getString( "billionBillionSpaceStationMasses" );
    public static final String CARTOON = getString( "cartoon" );
    public static final String EARTH = getString( "earth" );
    public static final String EARTH_DAY = getString( "earthDay" );
    public static final String EARTH_DAYS = getString( "earthDays" );
    public static final String EARTH_MASS = getString( "earthMass" );
    public static final String EARTH_MASSES = getString( "earthMasses" );
    public static final String EARTH_MINUTE = getString( "earthMinute" );
    public static final String EARTH_MINUTES = getString( "earthMinutes" );
    public static final String GRAVITY = getString( "gravity" );
    public static final String GRAVITY_FORCE = getString( "gravityForce" );
    public static final String MASS = getString( "mass" );
    public static final String MEASURING_TAPE = getString( "measuringTape" );
    public static final String MOON = getString( "moon" );
    public static final String MILLION_EARTH_MASSES = getString( "millionEarthMasses" );
    public static final String OUR_MOON = getString( "ourMoon" );
    public static final String OUR_SUN = getString( "ourSun" );
    public static final String PATH = getString( "path" );
    public static final String PLANET = getString( "planet" );
    public static final String REAL = getString( "real" );
    public static final String SPACE_STATION = getString( "spaceStation" );
    public static final String SPACE_STATION_MASS = getString( "spaceStationMass" );
    public static final String SPACE_STATION_MASSES = getString( "spaceStationMasses" );
    public static final String SCALE = getString( "scale" );
    public static final String SATELLITE = getString( "satellite" );
    public static final String SHOW = getString( "show" );
    public static final String SUN = getString( "sun" );
    public static final String THOUSAND_EARTH_MASSES = getString( "thousandEarthMasses" );
    public static final String VELOCITY = getString( "velocity" );
    
    public static final String PATTERN_VALUE_UNITS = getString( "pattern.0value.1units" );
    
    private static String getString( String key ) {
        return GAOResources.getString( key );
    }
}
