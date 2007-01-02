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

public class Body {
    private double angleOffset;
    private double distance;

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

    private AbstractVector2D getVelocity( RotationModelState rotationModelState ) {
        double v = distance * rotationModelState.getAngularVelocity();//v=r*omega
        Point2D position = getPosition( rotationModelState );
        double angle = new Vector2D.Double( position ).getAngle();
        return Vector2D.Double.parseAngleAndMagnitude( v, angle );
    }
}
