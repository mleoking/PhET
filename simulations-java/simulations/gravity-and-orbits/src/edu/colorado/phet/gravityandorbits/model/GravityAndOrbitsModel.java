/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


/**
 * Model template.
 */
public class GravityAndOrbitsModel {
    public static double EARTH_MASS = 5.9742E24;
    private static final double SUN_RADIUS = 6.955E8;
    private static final double EARTH_RADIUS = 6.371E6;
    private static final double G = 6.67428E-11;

    private final GravityAndOrbitsClock clock;
    private final Body sun = new Body( "Sun", 0, 0, SUN_RADIUS * 2, 0, 0, 1.989E30, Color.yellow, Color.white );
    private final Body planet = new Body( "Planet", 149668992000.0, 0, EARTH_RADIUS * 2, 0, -29.78E3, EARTH_MASS, Color.blue, Color.white );//semi-major axis, see http://en.wikipedia.org/wiki/Earth, http://en.wikipedia.org/wiki/Sun


    class ModelState{
        ArrayList<VelocityVerlet.BodyState> 
    }
    public GravityAndOrbitsModel( GravityAndOrbitsClock clock ) {
        super();
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                super.simulationTimeChanged( clockEvent );
                final ArrayList<VelocityVerlet.BodyState> verletState = new ArrayList<VelocityVerlet.BodyState>() {{
                    add( sun.toBodyState() );
                    add( planet.toBodyState() );
                }};
                final ArrayList<VelocityVerlet.PotentialField>fields= new ArrayList<VelocityVerlet.PotentialField>() {{
                    add(new VelocityVerlet.PotentialField() {
                        public ImmutableVector2D getGradient( VelocityVerlet.BodyState body, ImmutableVector2D newPosition, ArrayList<VelocityVerlet.BodyState> state ) {
                            return getForce( state.get(1 ),state.get( 0 ));
                        }
                    });
                    add(new VelocityVerlet.PotentialField() {
                        public ImmutableVector2D getGradient( VelocityVerlet.BodyState body, ImmutableVector2D newPosition, ArrayList<VelocityVerlet.BodyState> state ) {
                            return getForce( state.get(0 ),state.get( 1 ));
                        }
                    });
                }};
                ArrayList<VelocityVerlet.BodyState> state = new MultiStepPhysicsUpdate( 100, new VelocityVerlet() ).getNextState( verletState, clockEvent.getSimulationTimeChange(), fields );
                sun.updateBodyStateFromModel( state.get( 0 ) );
                planet.updateBodyStateFromModel( state.get( 1 ) );
            }
        } );
    }

    private ImmutableVector2D getForce( VelocityVerlet.BodyState source, VelocityVerlet.BodyState target ) {
        return target.getUnitDirectionVector( source ).getScaledInstance( G * source.mass * target.mass / source.distanceSquared( target ) );
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
