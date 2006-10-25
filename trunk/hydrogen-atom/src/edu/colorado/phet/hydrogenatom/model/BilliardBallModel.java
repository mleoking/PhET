/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * BilliardBall models the hydrogen atom as a billiard ball.
 * <p>
 * Physical representation:
 * The ball is spherical, with its local origin at its center.
 * <p>
 * Collision behavior:
 * When photons and alpha particles collide with the ball,
 * they bounce off as if the ball were a rigid body.
 * <p>
 * Absorption behavior:
 * Does not absorb photons or alpha particles.
 * <p>
 * Emission behavior:
 * Does not emit photons or alpha particles.
 */
public class BilliardBallModel extends AbstractHydrogenAtom {

    private static final double DEFAULT_RADIUS = 150;
    
    private double _radius;
    private Shape _shape;
    
    public BilliardBallModel( Point2D position ) {
        this( position, DEFAULT_RADIUS );
    }
    
    public BilliardBallModel( Point2D position, double radius ) {
        super( position, 0 /* orientation */ );
        _radius = radius;
        updateShape();
    }
    
    public void setPosition( Point2D p ) {
        setPosition( p.getX(), p.getY() );
    }
    
    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
        updateShape();
    }
    
    public double getRadius() {
        return _radius;
    }
    
    public boolean contains( Point2D p ) {
        return _shape.contains( p );
    }
    
    private void updateShape() {
        _shape = new Ellipse2D.Double( getX() - _radius, getY() - _radius, 2 * _radius, 2 * _radius );
    }
    
    public void stepInTime( double dt ) {
        //XXX
    }
}
