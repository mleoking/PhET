// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function2;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;
import edu.colorado.phet.gravityandorbits.view.IBodyColors;
import edu.colorado.phet.gravityandorbits.view.MultiwayOr;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * Body is a single point mass in the Gravity and Orbits simluation, such as the Earth, Sun, Moon or Space Station.
 * This class also keeps track of body related data such as the path.
 * The Body can be in cartoon scale (not to scale) or real scale, which can change both the location and the size.
 *
 * @author Sam Reid
 */
public class Body implements IBodyColors {
    private final ClockRewindProperty<ImmutableVector2D> positionProperty;//physical position
    private final Property<ImmutableVector2D> scaledPositionProperty;//position accounting for scale (i.e. cartoon or real)
    private final ClockRewindProperty<ImmutableVector2D> velocityProperty;
    private final Property<ImmutableVector2D> accelerationProperty;
    private final Property<ImmutableVector2D> forceProperty;
    private final ClockRewindProperty<Double> massProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;
    private final double density;
    private boolean userControlled;

    private final ArrayList<PathListener> pathListeners = new ArrayList<PathListener>();
    private final ArrayList<PathPoint> path = new ArrayList<PathPoint>();
    private final Property<Scale> scaleProperty;
    private final boolean massSettable;
    private final double cartoonDiameterScaleFactor;
    private final Body parent;
    private final double cartoonOffsetScale;
    private final Function2<Body, Double, BodyRenderer> renderer;//function that creates a PNode for this Body
    private final double labelAngle;
    private final int maxPathLength;
    private final double cartoonForceScale;
    private final boolean massReadoutBelow;
    private final ClockRewindProperty<Boolean> collidedProperty;
    private final Property<Integer> clockTicksSinceExplosion = new Property<Integer>( 0 );
    private double tickValue;
    private String tickLabel;

    private ArrayList<VoidFunction0> userModifiedPositionListeners = new ArrayList<VoidFunction0>();
    private Property<Shape> bounds = new Property<Shape>( new Rectangle2D.Double( 0, 0, 0, 0 ) );//if the object leaves these model bounds, then it can be "returned" using a return button on the canvas
    private BooleanProperty returnable;

    public Body( Body parent,//the parent body that this body is in orbit around, used in cartoon mode to exaggerate locations
                 final String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight,
                 double cartoonDiameterScaleFactor, double cartoonOffsetScale,
                 Function2<Body, Double, BodyRenderer> renderer,// way to associate the graphical representation directly instead of later with conditional logic or map
                 final Property<Scale> scaleProperty, double labelAngle, boolean massSettable,
                 int maxPathLength,
                 double cartoonForceScale, boolean massReadoutBelow, double tickValue, String tickLabel, Property<Boolean> clockPausedProperty ) {
        this.scaleProperty = scaleProperty;//Multiplied with mode scale to arrive at total scale for forces for this body, provides body-specific force scaling that is independent of cartoon/real modes
        this.massSettable = massSettable;
        this.maxPathLength = maxPathLength;
        this.cartoonForceScale = cartoonForceScale;
        this.massReadoutBelow = massReadoutBelow;
        this.tickValue = tickValue;
        this.tickLabel = tickLabel;
        assert renderer != null;
        this.parent = parent;
        this.name = name;
        this.color = color;
        this.highlight = highlight;
        this.cartoonDiameterScaleFactor = cartoonDiameterScaleFactor;
        this.cartoonOffsetScale = cartoonOffsetScale;
        this.renderer = renderer;
        this.labelAngle = labelAngle;
        positionProperty = new ClockRewindProperty<ImmutableVector2D>( clockPausedProperty, new ImmutableVector2D( x, y ) );
        velocityProperty = new ClockRewindProperty<ImmutableVector2D>( clockPausedProperty, new ImmutableVector2D( vx, vy ) );
        accelerationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        forceProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        massProperty = new ClockRewindProperty<Double>( clockPausedProperty, mass );
        diameterProperty = new Property<Double>( diameter );
        collidedProperty = new ClockRewindProperty<Boolean>( clockPausedProperty, false );
        density = mass / getVolume();
        scaledPositionProperty = new Property<ImmutableVector2D>( getPosition() );

        //Determine whether the object should be 'returnable', i.e. whether a 'return' button node
        //is shown on the canvas that allows the user to bring back a destroyed or lost object
        returnable = new BooleanProperty( false ) {{
            final SimpleObserver obs = new SimpleObserver() {
                public void update() {
                    setValue( collidedProperty.getValue() || !bounds.getValue().contains( getPosition( scaleProperty.getValue() ).toPoint2D() ) );
                }
            };
            bounds.addObserver( obs );
            collidedProperty.addObserver( obs );
            scaleProperty.addObserver( obs );
            getPositionProperty().addObserver( obs );
        }};

        //Synchronize the scaled position, which accounts for the scale
        final SimpleObserver updateScaledPosition = new SimpleObserver() {
            public void update() {
                scaledPositionProperty.setValue( getPosition( scaleProperty.getValue() ) );
            }
        };
        scaleProperty.addObserver( updateScaledPosition );
        positionProperty.addObserver( updateScaledPosition );
        if ( parent != null ) {
            parent.positionProperty.addObserver( updateScaledPosition );
        }
        collidedProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( collidedProperty.getValue() ) {
                    clockTicksSinceExplosion.setValue( 0 );
                }
            }
        } );
    }

    public Property<Integer> getClockTicksSinceExplosion() {
        return clockTicksSinceExplosion;
    }

    public Property<ImmutableVector2D> getScaledPositionProperty() {
        return scaledPositionProperty;
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

    public Property<ImmutableVector2D> getPositionProperty() {
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

    public void translate( Point2D delta ) {
        translate( delta.getX(), delta.getY() );
        addPathPoint();
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

    public void allBodiesUpdated() {
        addPathPoint();
    }

    private void addPathPoint() {
        PathPoint pathPoint = new PathPoint( getPosition(), getCartoonPosition() );
        path.add( pathPoint );
        while ( path.size() > maxPathLength ) {//start removing data after 2 orbits of the default system
            path.remove( 0 );
            for ( PathListener listener : pathListeners ) {
                listener.pointRemoved();
            }
        }
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
        double radius = Math.pow( 3 * mass / 4 / Math.PI / density, 1.0 / 3.0 );
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

    public Property<ImmutableVector2D> getVelocityProperty() {
        return velocityProperty;
    }

    public Property<Double> getMassProperty() {
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

    public ArrayList<PathPoint> getPath() {
        return path;
    }

    public boolean isMassSettable() {
        return massSettable;
    }

    public BodyRenderer createRenderer( double viewDiameter ) {
        return renderer.apply( this, viewDiameter );
    }

    public double getCartoonDiameterScaleFactor() {
        return cartoonDiameterScaleFactor;
    }

    public Body getParent() {
        return parent;
    }

    public double getCartoonOffsetScale() {
        return cartoonOffsetScale;
    }

    public ImmutableVector2D getCartoonPosition() {
        if ( getParent() != null ) {
            return new CartoonPositionMap( cartoonOffsetScale ).toCartoon( getName(), getPosition(), getParent().getPosition() );
        }
        else {
            return getPosition();//those without parents have a cartoon position equal to their physical position
        }
    }

    public ImmutableVector2D getPosition( Scale scale ) {
        if ( scale == Scale.CARTOON && parent != null ) {
            return getCartoonPosition();
        }
        else {
            return getPosition();
        }
    }

    public double getDiameter( Scale scale ) {
        if ( scale == Scale.CARTOON ) {
            return getCartoonDiameterScaleFactor() * getDiameter();
        }
        else {
            return getDiameter();
        }
    }

    public double getLabelAngle() {
        return labelAngle;
    }

    public boolean isDraggable() {
        return true;
    }

    public int getMaxPathLength() {
        return maxPathLength;
    }

    public Property<Scale> getScaleProperty() {
        return scaleProperty;
    }

    public boolean isMassReadoutBelow() {
        return massReadoutBelow;
    }

    public Property<Boolean> getCollidedProperty() {
        return collidedProperty;
    }

    public boolean collidesWidth( Body body ) {
        final ImmutableVector2D myPosition = getPosition( scaleProperty.getValue() );
        final ImmutableVector2D yourPosition = body.getPosition( body.scaleProperty.getValue() );
        double distance = myPosition.getSubtractedInstance( yourPosition ).getMagnitude();
        double radiiSum = getDiameter( scaleProperty.getValue() ) / 2 + body.getDiameter( body.scaleProperty.getValue() ) / 2;
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

    public ImmutableVector2D globalCartoonToReal( ImmutableVector2D childCartoonPosition ) {
        //constrain the child Body to remain stationary
        return new CartoonPositionMap( cartoonOffsetScale ).toReal( childCartoonPosition, getParent().getPosition() );
    }

    //Unexplodes and returns objects to the stage
    public void returnBody() {
        if ( collidedProperty.getValue() || !bounds.getValue().contains( getPosition( scaleProperty.getValue() ).toPoint2D() ) ) {
            setCollided( false );
            positionProperty.reset();
            velocityProperty.reset();
        }
    }

    public static class PathPoint {
        public final ImmutableVector2D point;
        public final ImmutableVector2D cartoonPoint;

        public PathPoint( ImmutableVector2D point, ImmutableVector2D cartoonPoint ) {
            this.point = point;
            this.cartoonPoint = cartoonPoint;
        }
    }

    public static interface PathListener {
        public void pointAdded( PathPoint point );

        public void pointRemoved();

        public void cleared();
    }

    @Override
    public String toString() {
        return "name = " + getName() + ", mass = " + getMass();
    }

    public double getCartoonForceScale() {
        return cartoonForceScale;
    }

    public Property<Shape> getBounds() {
        return bounds;
    }

    public BooleanProperty getReturnable() {
        return returnable;
    }
}
