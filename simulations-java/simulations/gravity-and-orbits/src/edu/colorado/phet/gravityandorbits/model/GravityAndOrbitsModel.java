/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


/**
 * Model template.
 */
public class GravityAndOrbitsModel {

    public static final double DUBSON_SUN_MASS = 200;
    public static final double DUBSON_PLANET_MASS = 10;
    public static final double DUBSON_PLANET_DISTANCE = 160;
    public static final double DUBSON_PLANET_VELOCITY = 120;//to the up 
    public static final double DUBSON_MOON_MASS = 0.001;
    public static final double DUBSON_MOON_DISTANCE = 140;
    public static final double DUBSON_MOON_VELOCITY = 53;

    private static final double DUBSON_MASS_SCALE = 1;
    private static final double DUBSON_DISTANCE_SCALE = 1;
    private static final double DUBSON_VELOCITY_SCALE = 1;

    public static double EARTH_MASS = 5.9742E24;
    public static double MOON_MASS = 7.3477E22;
    private static final double SUN_RADIUS = 6.955E8;
    private static final double EARTH_RADIUS = 6.371E6;
    private static final double MOON_RADIUS = 1737.1E3;
    public static final double G = 6.67428E-11;

    private final GravityAndOrbitsClock clock;
    public static final double SUN_MASS = 1.989E30;
    private final Body sun = new Body( "Sun", 0, 0, SUN_RADIUS * 2, 0, 0, SUN_MASS, Color.yellow, Color.white );
    private final double EARTH_ORBIT_RADIUS = 149668992000.0;
    private final double EARTH_ORBITAL_SPEED = -29.78E3;
    private final Body planet = new Body( "Planet", EARTH_ORBIT_RADIUS, 0, EARTH_RADIUS * 2, 0, EARTH_ORBITAL_SPEED, EARTH_MASS, Color.blue, Color.white );//semi-major axis, see http://en.wikipedia.org/wiki/Earth, http://en.wikipedia.org/wiki/Sun
    private final double MOON_ORBITAL_SPEED = EARTH_ORBITAL_SPEED - 1.022E3;
    private final Body moon = new Body( "Moon", planet.getX() + 384399E3, 0, MOON_RADIUS * 2, 0, MOON_ORBITAL_SPEED, MOON_MASS, Color.gray, Color.white );//semi-major axis, see http://en.wikipedia.org/wiki/Earth, http://en.wikipedia.org/wiki/Sun

    public GravityAndOrbitsModel( GravityAndOrbitsClock clock, final Property<Boolean> moonProperty ) {
        super();
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                super.simulationTimeChanged( clockEvent );
                ModelState newState = new ModelState( new ArrayList<BodyState>() {{
                    add( sun.toBodyState() );
                    add( planet.toBodyState() );
                    if ( moonProperty.getValue() ) {
                        add( moon.toBodyState() );
                    }
                }} ).getNextState( clockEvent.getSimulationTimeChange(), 100 );
                sun.updateBodyStateFromModel( newState.getBodyState( 0 ) );
                planet.updateBodyStateFromModel( newState.getBodyState( 1 ) );
                if ( moonProperty.getValue() ) {
                    moon.updateBodyStateFromModel( newState.getBodyState( 2 ) );
                }
            }
        } );
    }

    public GravityAndOrbitsClock getClock() {
        return clock;
    }

    public Body getSun() {
        return sun;
    }

    public Body getPlanet() {
        return planet;
    }

    public void resetAll() {
        sun.resetAll();
        planet.resetAll();
        moon.resetAll();
        getClock().resetSimulationTime();
        getClock().start();
    }

    public Body getMoon() {
        return moon;
    }
}
