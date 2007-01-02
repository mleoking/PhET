package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:29:21 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class Body implements Cloneable {
    private double angleOffset;
    private double distance;

    public Body( double distance ) {
        this( distance, 0 );
    }

    public Body( double distance, double angleOffset ) {
        this.distance = distance;
        this.angleOffset = angleOffset;
    }

    public Body copy() {
        return (Body)clone();
    }

    public Object clone() {
        try {
            Body clone = (Body)super.clone();
            clone.angleOffset = angleOffset;
            clone.distance = distance;
            return clone;
        }
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }

    public double getX( RotationModelState rotationModelState ) {
        return distance * Math.cos( angleOffset + rotationModelState.getAngle() );
    }

    public double getY( RotationModelState rotationModelState ) {
        return distance * Math.sin( angleOffset + rotationModelState.getAngle() );
    }

    public double getVelocityX( RotationModelState rotationModelState ) {
        return getVelocity( rotationModelState ).getX();
    }

    public double getVelocityY( RotationModelState rotationModelState ) {
        return getVelocity( rotationModelState ).getY();
    }

    public Point2D getPosition( RotationModelState rotationModelState ) {
        return new Point2D.Double( getX( rotationModelState ), getY( rotationModelState ) );
    }

    public AbstractVector2D getVelocity( RotationModelState rotationModelState ) {
        double v = distance * rotationModelState.getAngularVelocity();//v=r*omega
        Point2D position = getPosition( rotationModelState );
        double angle = new Vector2D.Double( position ).getAngle() + Math.PI / 2.0;
        return Vector2D.Double.parseAngleAndMagnitude( v, angle );
    }

    public AbstractVector2D getAcceleration( RotationModelState rotationModelState ) {
        double a = distance * rotationModelState.getAngularAcceleration();
        Point2D position = getPosition( rotationModelState );
        double angle = new Vector2D.Double( position ).getAngle();
        return Vector2D.Double.parseAngleAndMagnitude( a, angle );
    }
}
