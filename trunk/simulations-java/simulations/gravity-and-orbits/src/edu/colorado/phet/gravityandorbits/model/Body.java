// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;
import edu.colorado.phet.gravityandorbits.view.IBodyColors;
import edu.colorado.phet.gravityandorbits.view.MultiwayOr;

/**
 * Body is a single point mass in the Gravity and Orbits simulation, such as the Earth, Sun, Moon or Space Station.
 * This class also keeps track of body-related data such as the path.
 *
 * @author Sam Reid
 */
public class Body implements IBodyColors {
    private final RewindableProperty<Vector2D> positionProperty;
    private final RewindableProperty<Vector2D> velocityProperty;
    private final Property<Vector2D> accelerationProperty;
    private final Property<Vector2D> forceProperty;
    private final RewindableProperty<Double> massProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;
    private final double density;
    private boolean userControlled;//True if the user is currently controlling the position of the body with the mouse

    private final ArrayList<PathListener> pathListeners = new ArrayList<PathListener>();
    private final ArrayList<Vector2D> path = new ArrayList<Vector2D>();
    private final int maxPathLength; //Number of samples in the path before it starts erasing (fading out from the back)

    private final boolean massSettable;
    private final Function2<Body, Double, BodyRenderer> renderer;//function that creates a PNode for this Body.  This is in the model so we can associate the graphical representation directly instead of later with conditional logic or map
    private final double labelAngle;
    private final boolean massReadoutBelow;//True if the mass readout should appear below the body (so that readouts don't overlap too much), in the model for convenience since the body type determines where the mass readout should appear
    private final RewindableProperty<Boolean> collidedProperty;
    private final Property<Integer> clockTicksSinceExplosion = new Property<Integer>( 0 );
    private double tickValue;//value that this body's mass should be identified with, for 'planet' this will be the earth's mass
    private String tickLabel;//name associated with this body when it takes on the tickValue above, for 'planet' this will be "earth"

    private ArrayList<VoidFunction0> userModifiedPositionListeners = new ArrayList<VoidFunction0>();//list of listeners that are notified when the user drags the object, so that we know when certain properties need to be updated
    private Property<Shape> bounds = new Property<Shape>( new Rectangle2D.Double( 0, 0, 0, 0 ) );//if the object leaves these model bounds, then it can be "returned" using a return button on the canvas
    public final boolean fixed;//true if the object doesn't move when the physics engine runs, (though still can be moved by the user's mouse)
    private IUserComponent userComponent;

    public Body( IUserComponent userComponent, final String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight,
                 Function2<Body, Double, BodyRenderer> renderer,// way to associate the graphical representation directly instead of later with conditional logic or map
                 double labelAngle, boolean massSettable,
                 int maxPathLength,
                 boolean massReadoutBelow, double tickValue, String tickLabel, Property<Boolean> playButtonPressed, Property<Boolean> stepping, Property<Boolean> rewinding,
                 boolean fixed ) {
        this.userComponent = userComponent;//sun is immobile in cartoon mode
        this.massSettable = massSettable;
        this.maxPathLength = maxPathLength;
        this.massReadoutBelow = massReadoutBelow;
        this.tickValue = tickValue;
        this.tickLabel = tickLabel;
        this.fixed = fixed;
        assert renderer != null;
        this.name = name;
        this.color = color;
        this.highlight = highlight;
        this.renderer = renderer;
        this.labelAngle = labelAngle;
        positionProperty = new RewindableProperty<Vector2D>( playButtonPressed, stepping, rewinding, new Vector2D( x, y ) );
        velocityProperty = new RewindableProperty<Vector2D>( playButtonPressed, stepping, rewinding, new Vector2D( vx, vy ) );
        accelerationProperty = new Property<Vector2D>( new Vector2D( 0, 0 ) );
        forceProperty = new Property<Vector2D>( new Vector2D( 0, 0 ) );
        massProperty = new RewindableProperty<Double>( playButtonPressed, stepping, rewinding, mass );
        diameterProperty = new Property<Double>( diameter );
        collidedProperty = new RewindableProperty<Boolean>( playButtonPressed, stepping, rewinding, false );
        density = mass / getVolume();

        //Synchronize the scaled position, which accounts for the scale
        collidedProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( collidedProperty.get() ) {
                    clockTicksSinceExplosion.set( 0 );
                }
            }
        } );

        //If any of the rewind properties changes while the clock is paused, set a rewind point for all of them.

        //Relates to this problem reported by NP:
        //NP: odd behavior with rewind: Open sim and press play, let the planet move to directly left of the sun.
        //  Pause, then move the planet closer to sun. Press play, planet will move CCW. Then pause and hit rewind.
        //  Press play again, the planet will start to move in the opposite direction (CW).
        //SR: reproduced this in 0.0.14, perhaps the velocity is not being reset?

        final VoidFunction0 rewindValueChangeListener = new VoidFunction0() {
            public void apply() {
                positionProperty.storeRewindValueNoNotify();
                velocityProperty.storeRewindValueNoNotify();
                massProperty.storeRewindValueNoNotify();
                collidedProperty.storeRewindValueNoNotify();
            }
        };
        positionProperty.addRewindValueChangeListener( rewindValueChangeListener );
        velocityProperty.addRewindValueChangeListener( rewindValueChangeListener );
        massProperty.addRewindValueChangeListener( rewindValueChangeListener );
        collidedProperty.addRewindValueChangeListener( rewindValueChangeListener );
    }

    public Property<Integer> getClockTicksSinceExplosion() {
        return clockTicksSinceExplosion;
    }

    private double getVolume() {
        return 4.0 / 3.0 * Math.PI * Math.pow( getRadius(), 3 );
    }

    public double getRadius() {
        return getDiameter() / 2;
    }

    public Color getColor() {
        return color;
    }

    public Color getHighlight() {
        return highlight;
    }

    public RewindableProperty<Vector2D> getPositionProperty() {
        return positionProperty;
    }

    public Vector2D getPosition() {
        return positionProperty.get();
    }

    public Property<Double> getDiameterProperty() {
        return diameterProperty;
    }

    public double getDiameter() {
        return diameterProperty.get();
    }

    //TODO:
    //   Clients are required to call notifyUserModifiedPosition if this translation was done by the user.
    //   That's not at all clear (not documented here), it's error prone and it introduces order dependency.
    //   Recommend making notifyUserModifiedPosition private and adding another public variant of translate,
    //   i.e. public void translate(Point2D delta,boolean userModified) {...}
    public void translate( Point2D delta ) {
        translate( delta.getX(), delta.getY() );

        //Only add to the path if the object hasn't collided
        if ( !collidedProperty.get() && !userControlled ) {
            addPathPoint();
        }
    }

    public void translate( double dx, double dy ) {
        positionProperty.set( new Vector2D( getX() + dx, getY() + dy ) );
    }

    public double getY() {
        return positionProperty.get().getY();
    }

    public double getX() {
        return positionProperty.get().getX();
    }

    public String getName() {
        return name;
    }

    public void setDiameter( double value ) {
        diameterProperty.set( value );
    }

    //create an immutable representation of this body for use in the physics engine
    public BodyState toBodyState() {
        return new BodyState( getPosition(), getVelocity(), getAcceleration(), getMass(), collidedProperty.get() );
    }

    public double getMass() {
        return massProperty.get();
    }

    public Vector2D getAcceleration() {
        return accelerationProperty.get();
    }

    public Vector2D getVelocity() {
        return velocityProperty.get();
    }

    //Take the updated BodyState from the physics engine and update the state of this body based on it.
    public void updateBodyStateFromModel( BodyState bodyState ) {
        if ( collidedProperty.get() ) {
            clockTicksSinceExplosion.set( clockTicksSinceExplosion.get() + 1 );
        }
        else {
            if ( !isUserControlled() ) {
                positionProperty.set( bodyState.position );
                velocityProperty.set( bodyState.velocity );
            }
            accelerationProperty.set( bodyState.acceleration );
            forceProperty.set( bodyState.acceleration.getScaledInstance( bodyState.mass ) );
        }
    }

    //This method is called after all bodies have been updated by the physics engine (must be done as a batch), so that the path can be updated
    public void allBodiesUpdated() {
        //Only add to the path if the user isn't dragging it
        //But do add to the path even if the object is collided at the same location so the path will still grow in size and fade at the right time
        if ( !isUserControlled() ) {
            addPathPoint();
        }
    }

    private void addPathPoint() {
        while ( path.size() + 1//account for the point that will be added
                > maxPathLength * GravityAndOrbitsModel.SMOOTHING_STEPS ) {//start removing data after 2 orbits of the default system
            path.remove( 0 );
            for ( PathListener listener : pathListeners ) {
                listener.pointRemoved();
            }
        }
        Vector2D pathPoint = getPosition();
        path.add( pathPoint );
        for ( PathListener listener : pathListeners ) {
            listener.pointAdded( pathPoint );
        }
    }

    public void clearPath() {
        path.clear();
        for ( PathListener listener : pathListeners ) {
            listener.cleared();
        }
    }

    public Property<Vector2D> getForceProperty() {
        return forceProperty;
    }

    public void setMass( double mass ) {
        massProperty.set( mass );
        double radius = Math.pow( 3 * mass / 4 / Math.PI / density, 1.0 / 3.0 ); //derived from: density = mass/volume, and volume = 4/3 pi r r r
        diameterProperty.set( radius * 2 );
    }

    public void resetAll() {
        positionProperty.reset();
        velocityProperty.reset();
        accelerationProperty.reset();
        forceProperty.reset();
        massProperty.reset();
        diameterProperty.reset();
        collidedProperty.reset();
        clockTicksSinceExplosion.reset();
        clearPath();
        //TODO: anything else to reset here?
    }

    public RewindableProperty<Vector2D> getVelocityProperty() {
        return velocityProperty;
    }

    public RewindableProperty<Double> getMassProperty() {
        return massProperty;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public void setUserControlled( boolean b ) {
        this.userControlled = b;
    }

    public void addPathListener( PathListener listener ) {
        pathListeners.add( listener );
    }

    public void setVelocity( Vector2D velocity ) {
        velocityProperty.set( velocity );
    }

    public void setPosition( double x, double y ) {
        positionProperty.set( new Vector2D( x, y ) );
    }

    public void setAcceleration( Vector2D acceleration ) {
        this.accelerationProperty.set( acceleration );
    }

    public void setForce( Vector2D force ) {
        this.forceProperty.set( force );
    }

    public boolean isMassSettable() {
        return massSettable;
    }

    public BodyRenderer createRenderer( double viewDiameter ) {
        return renderer.apply( this, viewDiameter );
    }

    public double getLabelAngle() {
        return labelAngle;
    }

    public int getMaxPathLength() {
        return maxPathLength * GravityAndOrbitsModel.SMOOTHING_STEPS;
    }

    public boolean isMassReadoutBelow() {
        return massReadoutBelow;
    }

    public Property<Boolean> getCollidedProperty() {
        return collidedProperty;
    }

    public boolean collidesWidth( Body body ) {
        double distance = getPosition().minus( body.getPosition() ).getMagnitude();
        double radiiSum = getDiameter() / 2 + body.getDiameter() / 2;
        return distance < radiiSum;
    }

    public void setCollided( boolean b ) {
        collidedProperty.set( b );
    }

    public double getTickValue() {
        return tickValue;
    }

    public String getTickLabel() {
        return tickLabel;
    }

    public void addUserModifiedPositionListener( VoidFunction0 listener ) {
        userModifiedPositionListeners.add( listener );
    }

    public void notifyUserModifiedPosition() {
        for ( VoidFunction0 listener : userModifiedPositionListeners ) {
            listener.apply();
        }
    }

    private ArrayList<VoidFunction0> userModifiedVelocityListeners = new ArrayList<VoidFunction0>();

    public void addUserModifiedVelocityListener( VoidFunction0 listener ) {
        userModifiedVelocityListeners.add( listener );
    }

    public void notifyUserModifiedVelocity() {
        for ( VoidFunction0 listener : userModifiedVelocityListeners ) {
            listener.apply();
        }
    }

    public void rewind() {
        positionProperty.rewind();
        velocityProperty.rewind();
        massProperty.rewind();
        collidedProperty.rewind();
        clearPath();
    }

    public Property<Boolean> anyPropertyDifferent() {
        return new MultiwayOr( Arrays.asList( positionProperty.different(), velocityProperty.different(), massProperty.different(), collidedProperty.different() ) );
    }

    //Unexplodes and returns objects to the stage
    public void returnBody( GravityAndOrbitsModel model ) {
        if ( collidedProperty.get() || !bounds.get().contains( getPosition().toPoint2D() ) ) {
            setCollided( false );
            clearPath();//so there is no sudden jump in path from old to new location
            doReturnBody( model );
        }
    }

    //Returns the body, overriden by bodies that need to be returned near the current location of the body they orbit
    protected void doReturnBody( GravityAndOrbitsModel model ) {
        positionProperty.reset();
        velocityProperty.reset();
    }

    public boolean isCollided() {
        return collidedProperty.get();
    }

    public IUserComponent getUserComponent() {
        return userComponent;
    }

    //Listener interface for getting callbacks when the path has changed, for displaying the path with picclo
    public static interface PathListener {
        public void pointAdded( Vector2D point );

        public void pointRemoved();

        public void cleared();
    }

    @Override
    public String toString() {
        return "name = " + getName() + ", mass = " + getMass();
    }

    public Property<Shape> getBounds() {
        return bounds;
    }
}