// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This is the model for Gravity and Orbits; there is one GravityAndOrbitsModel per each GravityAndOrbitsMode, and it uses ModelState to update the physics.
 *
 * @author Sam Reid
 * @see edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode
 * @see ModelState
 */
public class GravityAndOrbitsModel {

    private final ArrayList<Body> bodies = new ArrayList<Body>();

    private final GravityAndOrbitsClock clock;
    private ArrayList<SimpleObserver> modelStepListeners = new ArrayList<SimpleObserver>();
    public boolean teacherMode;//Flag for simsharing debugging
    private final VoidFunction1<Double> stepModel;

    public GravityAndOrbitsModel( final GravityAndOrbitsClock clock, final Property<Boolean> gravityEnabledProperty ) {
        super();
        this.clock = clock;

        //Function for stepping the physics of the model
        stepModel = new VoidFunction1<Double>() {
            public void apply( Double dt ) {
                //Compute the next state for each body based on the current state of all bodies in the system.
                ModelState newState = new ModelState( new ArrayList<BodyState>() {{
                    for ( Body body : bodies ) {
                        add( body.toBodyState() );
                    }
                }} ).getNextState( dt,
                                   100, // 1000 looks great, 50 starts to look awkward for sun+earth+moon, but 100 seems okay
                                   gravityEnabledProperty );
                //Set each body to its computed next state.
                //assumes that ModelState.getBodyState returns states in the same order as the container (ArrayList) used for bodies. A possible future improvement would be
                //to switch to use ModelState.getState(Body), which would be safer.
                for ( int i = 0; i < bodies.size(); i++ ) {
                    bodies.get( i ).updateBodyStateFromModel( newState.getBodyState( i ) );
                }
                //when two bodies collide, destroy the smaller
                for ( Body body : bodies ) {
                    for ( Body other : bodies ) {
                        if ( other != body ) {
                            if ( other.collidesWidth( body ) ) {
                                getSmaller( other, body ).setCollided( true );
                            }
                        }
                    }
                }
                //Signify that the model completed an entire step so that any batch operations may be invoked
                for ( int i = 0; i < bodies.size(); i++ ) {
                    bodies.get( i ).allBodiesUpdated();
                }

                //For debugging error in the integrator
//                System.out.println( clock.getSimulationTime() + "\t" + getSunEarthDistance() );
            }

            private Body getSmaller( Body other, Body body ) {
                if ( other.getMass() < body.getMass() ) {
                    return other;
                }
                else {
                    return body;
                }
            }
        };
        //Wire up the physics update to the clock and send out appropriate notifications afterwards.
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                if ( teacherMode ) {
                    return;//Do not run the clock in teacher mode, we should be getting updates from the student state
                }
                final double dt = clockEvent.getSimulationTimeChange();
                stepModel.apply( dt );
                for ( SimpleObserver modelStepListener : modelStepListeners ) {
                    modelStepListener.update();
                }
            }
        } );
        //Have to update force vectors when gravity gets toggled on and off, otherwise displayed value won't update
        gravityEnabledProperty.addObserver( new SimpleObserver() {
            public void update() {
                updateForceVectors();
            }
        } );
    }

    //For debugging the stability of the integration rule
    private double getSunEarthDistance() {
        final Body star = getBody( "star" );
        final Body planet = getBody( "planet" );
        if ( star == null || planet == null ) { return Double.NaN; }
        return star.getPosition().getDistance( planet.getPosition() );
    }

    public GravityAndOrbitsClock getClock() {
        return clock;
    }

    public void resetAll() {
        resetBodies();
        getClock().resetSimulationTime();
        updateForceVectors();
    }

    public void addModelSteppedListener( SimpleObserver simpleObserver ) {
        modelStepListeners.add( simpleObserver );
    }

    //Adds a body and updates the body's force vectors
    public void addBody( Body body ) {
        bodies.add( body );
        body.addUserModifiedPositionListener( new VoidFunction0() {
            public void apply() {
                if ( getClock().isPaused() ) { updateForceVectors(); }
            }
        } );
        body.getMassProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( getClock().isPaused() ) { updateForceVectors(); }
            }
        } );
        updateForceVectors();
    }

    /*
     * Since we haven't (yet?) rewritten the gravity forces to auto-update when dependencies change, we update when necessary
     * (1) when a new body is added or (2) when reset is pressed.
     * This update is done by running the physics engine for dt=0.0 then applying the computed forces to the bodies.
     * <p/>
     * Without this block of code, the force vectors would be zero on sim startup until the clock is started.
     */
    private void updateForceVectors() {
        stepModel.apply( 0.0 );//the effect of stepping the model is to update the force vectors
    }

    public ArrayList<Body> getBodies() {
        return new ArrayList<Body>( bodies );
    }

    public void resetBodies() {
        for ( Body body : bodies ) {
            body.resetAll();
        }
        updateForceVectors();//has to be done separately since physics is computed as a batch
    }

    //Unexplodes and returns objects to the stage
    public void returnBodies() {
        for ( Body body : bodies ) {
            body.returnBody( this );
        }
        updateForceVectors();//Fixes: "Return object" should recalculate the gravity force vectors and update them even when paused ... right now it displays the force vectors of the prior situation before it moved the moon or planet.
    }

    public Body getBody( String name ) {
        for ( Body body : bodies ) {
            if ( body.getName().equalsIgnoreCase( name ) ) {
                return body;
            }
        }
        return null;
    }
}
