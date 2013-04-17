// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.geom.Point2D;

/**
 * PlumPuddingAtom is the model of the plum pudding atom.
 * The plum pudding atom has no interaction with alpha particles.
 * Alpha particles simply pass right through the atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlumPuddingAtom extends AbstractAtom {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int NUMBER_OF_ELECTRONS = 79;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // radius of the atom's goo
    private final double _radius;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param position
     * @param radius
     */
    public PlumPuddingAtom( Point2D position, double radius ) {
        super( position, 0 /* orientation */ );
        _radius = radius;
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    /**
     * Gets the radius of the atom.
     * @return radius
     */
    public double getRadius() {
        return _radius;
    }

    /**
     * Gets the number of electrons.
     * 
     * @return int
     */
    public int getNumberOfElectrons() {
        return NUMBER_OF_ELECTRONS;
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves an alpha particle.
     * The plum pudding atom has no influence on the alpha particle's movement.
     * 
     * @param dt
     * @param alphaParticle
     */
    public void moveAlphaParticle( double dt, AlphaParticle alphaParticle ) {
        double speed = alphaParticle.getSpeed();
        double distance = speed * dt;
        double direction = alphaParticle.getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = alphaParticle.getX() + dx;
        double y = alphaParticle.getY() + dy;
        alphaParticle.setPosition( x, y );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement default implementation
    //----------------------------------------------------------------------------

    /**
     * Called when time has advanced by some delta.
     */
    public void stepInTime( double dt ) {
        // do nothing
    }
}
