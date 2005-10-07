/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetcomponents;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 15, 2004
 * Time: 9:00:36 AM
 * Copyright (c) Dec 15, 2004 by Sam Reid
 */

public class AffineTransformBuilder {
    private Rectangle origRect;
    private double angle;
    private Point2D.Double location = new Point2D.Double();
    private double scaleX = 1.0;
    private double scaleY = 1.0;

    public AffineTransformBuilder( Rectangle origRect ) {
        this.origRect = new Rectangle( origRect );
    }

    public void setAngle( double angle ) {
        this.angle = angle;
    }

    public void setLocation( double x, double y ) {
        location.setLocation( x, y );
    }

    public void translate( double dx, double dy ) {
        this.location = new Point2D.Double( location.getX() + dx, location.getY() + dy );
    }

    public void setScale( double scaleX, double scaleY ) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void scale( double scaleX, double scaleY ) {
        setScale( scaleX * this.scaleX, scaleY * this.scaleY );
    }

    public AffineTransform toAffineTransform() {
        AffineTransform transform = new AffineTransform();
        transform.translate( location.getX(), location.getY() );
        if( scaleX < 0 ) {
            transform.translate( origRect.width, 0 );
        }
        transform.scale( scaleX, scaleY );
        transform.rotate( angle, origRect.width / 2.0, origRect.height / 2.0 );

        return transform;
    }

    public void rotate( double alpha ) {
        angle += alpha;
    }

    public Rectangle getOrigRect() {
        return origRect;
    }

    public double getAngle() {
        return angle;
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setX( double x ) {
        this.location.x = x;
    }

    public double getX() {
        return location.x;
    }

    public void setY( double y ) {
        this.location.y = y;
    }

    public double getY() {
        return this.location.y;
    }
}
