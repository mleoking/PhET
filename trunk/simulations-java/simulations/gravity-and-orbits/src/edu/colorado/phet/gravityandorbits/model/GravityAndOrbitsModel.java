/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


/**
 * Model template.
 */
public class GravityAndOrbitsModel {
    public static double EARTH_MASS = 5.9742E24;
    private static final double SUN_RADIUS = 6.955E8;
    private static final double EARTH_RADIUS = 6.371E6;
    public static final double G = 6.67428E-11;

    private final GravityAndOrbitsClock clock;
    private final Body sun = new Body( "Sun", 0, 0, SUN_RADIUS * 2, 0, 0, 1.989E30, Color.yellow, Color.white );
    private final Body planet = new Body( "Planet", 149668992000.0, 0, EARTH_RADIUS * 2, 0, -29.78E3, EARTH_MASS, Color.blue, Color.white );//semi-major axis, see http://en.wikipedia.org/wiki/Earth, http://en.wikipedia.org/wiki/Sun

    public GravityAndOrbitsModel( GravityAndOrbitsClock clock ) {
        super();
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                super.simulationTimeChanged( clockEvent );
                ModelState modelState = new ModelState( new ArrayList<BodyState>() {{
                    add( sun.toBodyState() );
                    add( planet.toBodyState() );
                }} );
                ModelState updated = modelState.getNextState( clockEvent.getSimulationTimeChange() );
                sun.updateBodyStateFromModel( updated.getBodyState( 0 ) );
                planet.updateBodyStateFromModel( updated.getBodyState( 1 ) );
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
        getClock().resetSimulationTime();
        getClock().start();
    }
}
