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
    public static double EARTH_MASS = 5.9742E24;
    public static double MOON_MASS = 7.3477E22;
    private static final double SUN_RADIUS = 6.955E8;
    private static final double EARTH_RADIUS = 6.371E6;
    private static final double MOON_RADIUS = 1737.1E3;
    public static final double G = 6.67428E-11;

    private final GravityAndOrbitsClock clock;
    private final Body sun = new Body( "Sun", 0, 0, SUN_RADIUS * 2, 0, 0, 1.989E30, Color.yellow, Color.white );
    private final Body planet = new Body( "Planet", 149668992000.0, 0, EARTH_RADIUS * 2, 0, -29.78E3, EARTH_MASS, Color.blue, Color.white );//semi-major axis, see http://en.wikipedia.org/wiki/Earth, http://en.wikipedia.org/wiki/Sun
    private final Body moon = new Body( "Moon", planet.getX() + 384399E3, 0, MOON_RADIUS * 2, 0, -29.78E3 - 1.022E3, MOON_MASS, Color.gray, Color.white );//semi-major axis, see http://en.wikipedia.org/wiki/Earth, http://en.wikipedia.org/wiki/Sun

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
