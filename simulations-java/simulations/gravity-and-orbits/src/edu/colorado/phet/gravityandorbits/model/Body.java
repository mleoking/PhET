package edu.colorado.phet.gravityandorbits.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * @author Sam Reid
 */
public class Body {
    private final Property<ImmutableVector2D> positionProperty;
    private final Property<ImmutableVector2D> velocityProperty;
    private final Property<ImmutableVector2D> accelerationProperty;
    private final Property<ImmutableVector2D> forceProperty;
    private final Property<Double> massProperty;
    private final Property<Double> diameterProperty;
    private final String name;
    private final Color color;
    private final Color highlight;
    private final double density;
    private boolean userControlled;

    private final java.util.List<TraceListener> traceListeners = new LinkedList<TraceListener>();

    public Body( String name, double x, double y, double diameter, double vx, double vy, double mass, Color color, Color highlight ) {
        this.name = name;
        this.color = color;
        this.highlight = highlight;
        positionProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        velocityProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( vx, vy ) );
        accelerationProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        forceProperty = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
        massProperty = new Property<Double>( mass );
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
        addTracePoint();
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

    private ImmutableVector2D getAcceleration() {
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
        addTracePoint();
    }

    private void addTracePoint() {
        TracePoint trace = new TracePoint( getPosition(), isUserControlled() );
        for ( TraceListener listener : traceListeners ) {
            listener.traceAdded( trace );
        }
    }

    public void clearTrace() {
        for ( TraceListener listener : traceListeners ) {
            listener.traceCleared();
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
        clearTrace();
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

    public void addTraceListener( TraceListener listener ) {
        traceListeners.add( listener );
    }

    public void removeTraceListener( TraceListener listener ) {
        traceListeners.remove( listener );
    }

    public void setVelocity( ImmutableVector2D velocity ) {
        velocityProperty.setValue( velocity );
    }

    public void setPosition( double x, double y ) {
        positionProperty.setValue( new ImmutableVector2D( x,y) );
    }

    public static class TracePoint {
        public ImmutableVector2D point;
        public final boolean userControlled;

        public TracePoint( ImmutableVector2D point, boolean userControlled ) {
            this.point = point;
            this.userControlled = userControlled;
        }
    }

    public static interface TraceListener {
        public void traceAdded( TracePoint trace );

        public void traceCleared();
    }

    @Override
    public String toString() {
        return "name = "+getName()+", mass = "+getMass();
    }
}
