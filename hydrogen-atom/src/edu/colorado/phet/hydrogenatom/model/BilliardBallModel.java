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

import edu.colorado.phet.hydrogenatom.util.RandomUtils;

/**
 * BilliardBallModel models the hydrogen atom as a billiard ball.
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

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_RADIUS = 50;
    
    private static final double MIN_DEFLECTION_ANGLE = Math.toRadians( 120 );
    private static final double MAX_DEFLECTION_ANGLE = Math.toRadians( 170 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _radius;
    private Shape _shape;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BilliardBallModel( Point2D position ) {
        this( position, DEFAULT_RADIUS );
    }
    
    public BilliardBallModel( Point2D position, double radius ) {
        super( position, 0 /* orientation */ );
        _radius = radius;
        updateShape();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public double getRadius() {
        return _radius;
    }
    
    /*
     * Updates the shape to match the radius.
     */
    private void updateShape() {
        _shape = new Ellipse2D.Double( getX() - _radius, getY() - _radius, 2 * _radius, 2 * _radius );
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves a photon.
     * If the photon collides with the atom, the photon bounces back 
     * at a "steep but random" angle. Otherwise it continues to move
     * in its current direction.
     * 
     * @param photon
     * @param dt
     */
    public void movePhoton( Photon photon, double dt ) {
        Point2D position = photon.getPosition();
        if ( !photon.isCollided() ) {
            if ( _shape.contains( position ) ) {
                final int sign = ( position.getX() > getX() ) ? 1 : -1;
                final double deflection = sign * RandomUtils.nextDouble( MIN_DEFLECTION_ANGLE, MAX_DEFLECTION_ANGLE );
                final double orientation = photon.getOrientation() + deflection;
                photon.setOrientation( orientation );
                photon.setCollided( true );
            }
        }
        super.movePhoton( photon, dt );
    }
    
    /**
     * Detects and handles collision with an alpha particle.
     * If a collision occurs, the alpha particle bounces back at a "steep but random" angle.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void moveAlphaParticle( AlphaParticle alphaParticle, double dt ) {
        Point2D position = alphaParticle.getPosition();
        if ( _shape.contains( position ) ) {
            final int sign = ( position.getX() > getX() ) ? 1 : -1;
            final double deflection = sign * RandomUtils.nextDouble( MIN_DEFLECTION_ANGLE, MAX_DEFLECTION_ANGLE );
            final double orientation = alphaParticle.getOrientation() + deflection;
            alphaParticle.setOrientation( orientation );
        }
        super.moveAlphaParticle( alphaParticle, dt );
    }
}
