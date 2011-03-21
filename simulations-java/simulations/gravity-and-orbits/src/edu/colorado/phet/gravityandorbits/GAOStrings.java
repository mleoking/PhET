// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits;

/**
 * A collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 */
public class GAOStrings {


    /* not intended for instantiation */
    private GAOStrings() {
    }

    public static final String GRAVITY_AND_ORBITS_NAME = getString( "gravity-and-orbits.name" );

    public static final String CARTOON = getString( "cartoon" );
    public static final String TO_SCALE = getString( "toScale" );

    public static final String BILLION_BILLION_SPACE_STATION_MASSES = getString( "billionBillionSpaceStationMasses" );
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
    public static final String GRID = getString( "grid" );
    public static final String PHYSICS = getString( "physics" );
    public static final String PLANET = getString( "planet" );
    public static final String SPACE_STATION = getString( "spaceStation" );
    public static final String SPACE_STATION_MASS = getString( "spaceStationMass" );
    public static final String SPACE_STATION_MASSES = getString( "spaceStationMasses" );
    public static final String SATELLITE = getString( "satellite" );
    public static final String SHOW = getString( "show" );
    public static final String RESET = getString( "reset" );
    public static final String SUN = getString( "sun" );
    public static final String THOUSAND_EARTH_MASSES = getString( "thousandEarthMasses" );
    public static final String THOUSAND_MILES = getString( "thousandMiles" );
    public static final String VELOCITY = getString( "velocity" );
    public static final String ON = getString( "on" );
    public static final String OFF = getString( "off" );

    public static final String PATTERN_LABEL = getString( "pattern.0label" );
    public static final String PATTERN_VALUE_UNITS = getString( "pattern.0value.1units" );
    public static final String ZOOM_IN = getString( "zoomIn" );
    public static final String ZOOM_OUT = getString( "zoomOut" );

    //These strings not currently used, but may be put in as tooltips or labels later
    public static final String SUN_AND_PLANET = getString( "sunAndPlanet" );
    public static final String SUN_PLANET_AND_MOON = getString( "sunPlanetAndMoon" );
    public static final String PLANET_AND_MOON = getString( "planetAndMoon" );
    public static final String PLANET_AND_SPACE_STATION = getString( "planetAndSpaceStation" );

    public static final String RETURN_OBJECT = getString( "returnObject" );

    private static String getString( String key ) {
        return GravityAndOrbitsApplication.RESOURCES.getLocalizedString( key );
    }
}
