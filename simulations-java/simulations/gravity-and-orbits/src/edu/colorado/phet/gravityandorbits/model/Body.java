// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
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
    private final ClockRewindProperty<ImmutableVector2D> positionProperty;
    private final ClockRewindProperty<ImmutableVector2D> velocityProperty;
    private final Property<ImmutableVector2D> accelerationProperty;
    private final Property<ImmutableVector2D> forceProperty;
    private final ClockRewindProperty<Double> massProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;
    private final double density;
    private boolean userControlled;//True if the user is currently controlling the position of the body with the mouse

    private final ArrayList<PathListener> pathListeners = new ArrayList<PathListener>();
    private final ArrayList<PathPoint> path = new ArrayList<PathPoint>();
    private final int maxPathLength; //Number of samples in the path before it starts erasing (fading out from the back)

    private final boolean massSettable;
    private final Function2<Body, Double, BodyRenderer> renderer;//function that creates a PNode for this Body.  This is in the model so we can associate the graphical representation directly instead of later with conditional logic or map
    private final double labelAngle;
    private final boolean massReadoutBelow;//True if the mass readout should appear below the body (so that readouts don't overlap too much), in the model for convenience since the body type determines where the mass readout should appear
    private final ClockRewindProperty<Boolean> collidedProperty;
    private final Property<Integer> clockTicksSinceExplosion = new Property<Integer>( 0 );
    private double tickValue;//value that this body's mass should be identified with, for 'planet' this will be the earth's mass
    private String tickLabel;//name associated with this body when it takes on the tickValue above, for 'planet' this will be "earth"

    private ArrayList<VoidFunction0> userModifiedPositionListeners = new ArrayList<VoidFunction0>();//list of listeners that are notified when the user drags the object, so that we know when certain properties need to be updated
    private Property<Shape> bounds = new Property<Shape>( new Rectangle2D.Double( 0, 0, 0, 0 ) );//if the object leaves these model bounds, then it can be "returned" using a return button on the canvas
    public final boolean fixed;//true if the object doesn't move when the physics engine runs, (though still can be moved by the user's mouse)

    public Body( final String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight,
                 Function2<Body, Double, BodyRenderer> renderer,// way to associate the graphical representation directly instead of later with conditional logic or map
                 double labelAngle, boolean massSettable,
                 int maxPathLength,
                 boolean massReadoutBelow, double tickValue, String tickLabel, Property<Boolean> clockPaused, Property<Boolean> stepping, Property<Boolean> rewinding,
                 boolean fixed ) {//sun is immobile in cartoon mode
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
        positionProperty = new ClockRewindProperty<ImmutableVector2D>( clockPaused, stepping, rewinding, new ImmutableVector2D( x, y ) );
        velocityProperty = new ClockRewindProperty<ImmutableVector2D>( clockPaused, stepping, rewinding, new ImmutableVector2D( vx, vy ) );
        accelerationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        forceProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        massProperty = new ClockRewindProperty<Double>( clockPaused, stepping, rewinding, mass );
        diameterProperty = new Property<Double>( diameter );
        collidedProperty = new ClockRewindProperty<Boolean>( clockPaused, stepping, rewinding, false );
        density = mass / getVolume();

        //Synchronize the scaled position, which accounts for the scale
        collidedProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( collidedProperty.getValue() ) {
                    clockTicksSinceExplosion.setValue( 0 );
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

    public ClockRewindProperty<ImmutableVector2D> getPositionProperty() {
        return positionProperty;
    }

    public ImmutableVector2D getPosition() {
        return positionProperty.getValue();
    }

    public Property<Double> getDiameterProperty() {
        return diameterProperty;
    }

    public double getDiameter() {
        return diameterProperty.getValue();
    }

    //REVIEW
    //   Clients are required to call notifyUserModifiedPosition if this translation was done by the user.
    //   That's not at all clear (not documented here), it's error prone and it introduces order dependency.
    //   Recommend making notifyUserModifiedPosition private and adding another public variant of translate,
    //   i.e. public void translate(Point2D delta,boolean userModified) {...}
    public void translate( Point2D delta ) {
        translate( delta.getX(), delta.getY() );

        //Only add to the path if the object hasn't collided
        if ( !collidedProperty.getValue() && !userControlled ) {
            addPathPoint();
        }
    }

    public void translate( double dx, double dy ) {
        positionProperty.setValue( new ImmutableVector2D( getX() + dx, getY() + dy ) );
    }

    public double getY() {
        return positionProperty.getValue().getY();
    }

    public double getX() {
        return positionProperty.getValue().getX();
    }

    public String getName() {
        return name;
    }

    public void setDiameter( double value ) {
        diameterProperty.setValue( value );
    }

    //create an immutable representation of this body for use in the physics engine
    public BodyState toBodyState() {
        return new BodyState( getPosition(), getVelocity(), getAcceleration(), getMass(), collidedProperty.getValue() );
    }

    public double getMass() {
        return massProperty.getValue();
    }

    public ImmutableVector2D getAcceleration() {
        return accelerationProperty.getValue();
    }

    public ImmutableVector2D getVelocity() {
        return velocityProperty.getValue();
    }

    //Take the updated BodyState from the physics engine and update the state of this body based on it.
    public void updateBodyStateFromModel( BodyState bodyState ) {
        if ( collidedProperty.getValue() ) {
            clockTicksSinceExplosion.setValue( clockTicksSinceExplosion.getValue() + 1 );
        }
        else {
            if ( !isUserControlled() ) {
                positionProperty.setValue( bodyState.position );
                velocityProperty.setValue( bodyState.velocity );
            }
            accelerationProperty.setValue( bodyState.acceleration );
            forceProperty.setValue( bodyState.acceleration.getScaledInstance( bodyState.mass ) );
        }
    }

    //This method is called after all bodies have been updated by the physics engine (must be done as a batch), so that the path can be updated
    public void allBodiesUpdated() {
        //Only add to the path if the object hasn't collided and if the user isn't dragging it
        if ( !collidedProperty.getValue() && !isUserControlled() ) {
            addPathPoint();
        }
    }

    private void addPathPoint() {
        while ( path.size() + 1//account for the point that will be added
                > maxPathLength ) {//start removing data after 2 orbits of the default system
            path.remove( 0 );
            for ( PathListener listener : pathListeners ) {
                listener.pointRemoved();
            }
        }
        PathPoint pathPoint = new PathPoint( getPosition() );
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

    public Property<ImmutableVector2D> getForceProperty() {
        return forceProperty;
    }

    public void setMass( double mass ) {
        massProperty.setValue( mass );
        double radius = Math.pow( 3 * mass / 4 / Math.PI / density, 1.0 / 3.0 ); //REVIEW how was this derived?
        diameterProperty.setValue( radius * 2 );
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
    }

    public ClockRewindProperty<ImmutableVector2D> getVelocityProperty() {
        return velocityProperty;
    }

    public ClockRewindProperty<Double> getMassProperty() {
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

    public void setVelocity( ImmutableVector2D velocity ) {
        velocityProperty.setValue( velocity );
    }

    public void setPosition( double x, double y ) {
        positionProperty.setValue( new ImmutableVector2D( x, y ) );
    }

    public void setAcceleration( ImmutableVector2D acceleration ) {
        this.accelerationProperty.setValue( acceleration );
    }

    public void setForce( ImmutableVector2D force ) {
        this.forceProperty.setValue( force );
    }

    //REVIEW unused
    public ArrayList<PathPoint> getPath() {
        return path;
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

    //REVIEW is this for subclasses to override? I don't see it overridden anywhere.
    public boolean isDraggable() {
        return true;
    }

    public int getMaxPathLength() {
        return maxPathLength;
    }

    public boolean isMassReadoutBelow() {
        return massReadoutBelow;
    }

    public Property<Boolean> getCollidedProperty() {
        return collidedProperty;
    }

    public boolean collidesWidth( Body body ) {
        double distance = getPosition().getSubtractedInstance( body.getPosition() ).getMagnitude();
        double radiiSum = getDiameter() / 2 + body.getDiameter() / 2;
        return distance < radiiSum;
    }

    public void setCollided( boolean b ) {
        collidedProperty.setValue( b );
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
        if ( collidedProperty.getValue() || !bounds.getValue().contains( getPosition().toPoint2D() ) ) {
            setCollided( false );
            clearPath();//so there is no sudden jump in path from old to new location
            doReturnBody( model );
        }
    }

    //REVIEW fill in doc template, include why this is protected
    /*
     * Template method.
     */
    protected void doReturnBody( GravityAndOrbitsModel model ) {
        positionProperty.reset();
        velocityProperty.reset();
    }

    public boolean isCollided() {
        return collidedProperty.getValue();
    }

    //REVIEW what is the purpose of this class? Is it a marker class? If so, why use composition instead of inheritance?
    public static class PathPoint {
        public final ImmutableVector2D point;

        public PathPoint( ImmutableVector2D point ) {
            this.point = point;
        }
    }

    //REVIEW document. who implements this and why?
    public static interface PathListener {
        public void pointAdded( PathPoint point );

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