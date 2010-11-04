/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

public class GravityAndOrbitsModel {

    public static final double G = 6.67428E-11;

    public static final double SUN_MASS = 2E29;
    public static final double SUN_RADIUS = 6.955E8;

    public static final double PLANET_RADIUS = 6.371E6;
    public static final double PLANET_MASS = 1E28;
    public static final double PLANET_ORBIT_RADIUS = 1.6E11;
    public static final double PLANET_ORBITAL_SPEED = 0.9E4;

    public static final double MOON_RADIUS = 1737.1E3;
    public static final double MOON_MASS = 1E25;
    public static final double MOON_INITIAL_X = 1.4E11;
    public static final double MOON_ORBITAL_SPEED = 0.397E4;

    public final Body sun = new Body( "Sun", 0, 0, SUN_RADIUS * 2, 0, -0.045E4, SUN_MASS, Color.yellow, Color.white );
    public final Body planet = new Body( "Planet", PLANET_ORBIT_RADIUS, 0, PLANET_RADIUS * 2, 0, PLANET_ORBITAL_SPEED, PLANET_MASS, Color.magenta, Color.white );
    public final Body moon = new Body( "Moon", MOON_INITIAL_X, 0, MOON_RADIUS * 2, 0, MOON_ORBITAL_SPEED, MOON_MASS, Color.gray, Color.white );

    private final GravityAndOrbitsClock clock;

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
                }} ).getNextState( clockEvent.getSimulationTimeChange(), 10 );
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
