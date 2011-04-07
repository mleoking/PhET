// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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
    public boolean teacherMode;
    private final VoidFunction1<Double> stepModel;

    public GravityAndOrbitsModel( GravityAndOrbitsClock clock, final Property<Boolean> gravityEnabledProperty ) {
        super();
        this.clock = clock;

        //Function for stepping the physics of the model
        stepModel = new VoidFunction1<Double>() {
            public void apply( Double dt ) {
                ModelState newState = new ModelState( new ArrayList<BodyState>() {{
                    for ( Body body : bodies ) {
                        add( body.toBodyState() );
                    }
                }} ).getNextState( dt,
                                   100, // 1000 looks great, 50 starts to look awkward for sun+earth+moon, but 100 seems okay
                                   gravityEnabledProperty );
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
                for ( int i = 0; i < bodies.size(); i++ ) {
                    bodies.get( i ).allBodiesUpdated();
                }
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

    //Used for determining initial velocities so the total momentum is zero
    private ImmutableVector2D getTotalMomentum() {
        ImmutableVector2D total = new ImmutableVector2D();
        for ( Body body : bodies ) {
            total = total.getAddedInstance( body.getVelocity().getScaledInstance( body.getMass() ) );
        }
        return total;
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

    /**
     * Since we haven't (yet?) rewritten the gravity forces to auto-update when dependencies change, we update when necessary
     * (1) when a new body is added or (2) when reset is pressed.
     * This update is done by running the physics engine for dt=0.0 then applying the computed forces to the bodies.
     * <p/>
     * Without this block of code, the force vectors would be zero on sim startup until the clock is started.
     */
    private void updateForceVectors() {
        stepModel.apply( 0.0 );
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
    public void returnObjects() {
        for ( Body body : bodies ) {
            body.returnBody( this );
        }
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
