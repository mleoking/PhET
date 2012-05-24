// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This class defines a point in model space that also has mass.  It is
 * is used to define the overall shape of the mRNA, which uses a spring
 * algorithm to implement the winding/twisting behavior.
 */
public class PointMass {
    public static final double MASS = 0.25; // In kg.  Arbitrarily chosen to get the desired behavior.
    private final Point2D position = new Point2D.Double( 0, 0 );
    private final Vector2D velocity = new Vector2D( 0, 0 );
    private final Vector2D acceleration = new Vector2D( 0, 0 );
    private PointMass previousPointMass = null;
    private PointMass nextPointMass = null;

    private double targetDistanceToPreviousPoint; // In picometers.

    public PointMass( Point2D initialPosition, double targetDistanceToPreviousPoint ) {
        setPosition( initialPosition );
        this.targetDistanceToPreviousPoint = targetDistanceToPreviousPoint;
    }

    @Override public String toString() {
        return getClass().getName() + " Position: " + position.toString();
    }

    public void setPosition( double x, double y ) {
        position.setLocation( x, y );
    }

    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }

    public Point2D getPosition() {
        return new Point2D.Double( position.getX(), position.getY() );
    }

    public void setVelocity( double x, double y ) {
        velocity.setComponents( x, y );
    }

    public ImmutableVector2D getVelocity() {
        return new ImmutableVector2D( velocity.getX(), velocity.getY() );
    }

    public void setAcceleration( ImmutableVector2D acceleration ) {
        this.acceleration.setValue( acceleration );
    }

    public PointMass getPreviousPointMass() {
        return previousPointMass;
    }

    public void setPreviousPointMass( PointMass previousPointMass ) {
        this.previousPointMass = previousPointMass;
    }

    public PointMass getNextPointMass() {
        return nextPointMass;
    }

    public void setNextPointMass( PointMass nextPointMass ) {
        this.nextPointMass = nextPointMass;
    }

    public double getTargetDistanceToPreviousPoint() {
        return targetDistanceToPreviousPoint;
    }

    public double distance( PointMass p ) {
        return this.getPosition().distance( p.getPosition() );
    }

    public void update( double deltaTime ) {
        velocity.setValue( velocity.getAddedInstance( acceleration.getScaledInstance( deltaTime ) ) );
        position.setLocation( position.getX() + velocity.getX() * deltaTime, position.getY() + velocity.getY() * deltaTime );
    }

    public void translate( ImmutableVector2D translationVector ) {
        setPosition( position.getX() + translationVector.getX(), position.getY() + translationVector.getY() );
    }

    public void setTargetDistanceToPreviousPoint( double targetDistance ) {
        targetDistanceToPreviousPoint = targetDistance;
    }

    public void clearVelocity() {
        velocity.setComponents( 0, 0 );
    }
}
