package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;
import edu.colorado.phet.gravityandorbits.view.PathNode;

/**
 * @author Sam Reid
 */
public abstract class Body {
    private final Property<ImmutableVector2D> positionProperty;
    private final Property<ImmutableVector2D> velocityProperty;
    private final Property<ImmutableVector2D> accelerationProperty;
    private final Property<ImmutableVector2D> forceProperty;
    private final PublicProperty<Double> massProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;
    private final double density;
    private boolean userControlled;

    private final ArrayList<PathListener> pathListeners = new ArrayList<PathListener>();
    private final ArrayList<PathPoint> path = new ArrayList<PathPoint>();
    private Function.LinearFunction sizer;
    private final boolean modifyable = true;
    private final Function.LinearFunction iconSizer;
    private final double cartoonDiameterScaleFactor;

    public Body( String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight, Function.LinearFunction sizer, Function.LinearFunction iconSizer, double cartoonDiameterScaleFactor ) {
        this.name = name;
        this.color = color;
        this.highlight = highlight;
        this.sizer = sizer;
        this.iconSizer = iconSizer;
        this.cartoonDiameterScaleFactor = cartoonDiameterScaleFactor;
        positionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        velocityProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( vx, vy ) );
        accelerationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        forceProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        massProperty = new PublicProperty<Double>( mass );
        diameterProperty = new Property<Double>( diameter );
        density = mass / getVolume();
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
        return new BodyState( getPosition(), getVelocity(), getAcceleration(), getMass() );
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
        if ( !isUserControlled() ) {
            positionProperty.setValue( bodyState.position );
            velocityProperty.setValue( bodyState.velocity );
        }
        accelerationProperty.setValue( bodyState.acceleration );
        forceProperty.setValue( bodyState.acceleration.getScaledInstance( bodyState.mass ) );
        addPathPoint();
    }

    private void addPathPoint() {
        PathPoint pathPoint = new PathPoint( getPosition(), isUserControlled() );
        path.add( pathPoint );
        while ( path.size() > PathNode.MAX_TRACE_LENGTH ) {//TODO: make this be 2 orbits after other free parameters are selected
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
        clearPath();
    }

    public Property<ImmutableVector2D> getVelocityProperty() {
        return velocityProperty;
    }

    public PublicProperty<Double> getMassProperty() {
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

    public void removePathListener( PathListener listener ) {
        pathListeners.remove( listener );
    }

    public void setVelocity( ImmutableVector2D velocity ) {
        velocityProperty.setValue( velocity );
    }

    public void setPosition( double x, double y ) {
        positionProperty.setValue( new ImmutableVector2D( x, y ) );
    }

    public double getDensity() {
        return density;
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

    public Function.LinearFunction getSizer() {
        return sizer;
    }

    public boolean isModifyable() {
        return modifyable;
    }

    public abstract BodyRenderer createRenderer( double viewDiameter );

    public Function.LinearFunction getIconSizer() {
        return iconSizer;
    }

    public double getCartoonDiameterScaleFactor() {
        return cartoonDiameterScaleFactor;
    }

    public static class PathPoint {
        public final ImmutableVector2D point;
        public final boolean userControlled;

        public PathPoint( ImmutableVector2D point, boolean userControlled ) {
            this.point = point;
            this.userControlled = userControlled;
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
}
