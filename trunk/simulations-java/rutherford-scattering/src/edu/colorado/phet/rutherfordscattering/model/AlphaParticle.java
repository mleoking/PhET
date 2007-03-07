/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.rutherfordscattering.util.DebugUtils;

/**
 * AlphaParticle is the model of an alpha particle.
 * An alpha particle has a position and direction of motion.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AlphaParticle extends MovingObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Point2D _initialPosition; // required by Rutherford Scattering algorithm
    private final double _initialSpeed; // required by Rutherford Scattering algorithm
    private final double _defaultSpeed; // required by Rutherford Scattering algorithm
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AlphaParticle( Point2D position, double orientation, double initialSpeed, double defaultSpeed ) {
        super( position, orientation, initialSpeed );
        _initialPosition = new Point2D.Double( position.getX(), position.getY() );
        _initialSpeed = initialSpeed;
        _defaultSpeed = defaultSpeed;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors implementation
    //----------------------------------------------------------------------------
    
    public Point2D getInitialPosition() {
        return _initialPosition;
    }
    
    public double getInitialSpeed() {
        return _initialSpeed;
    }
    
    public double getDefaultSpeed() {
        return _defaultSpeed;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /** Do nothing */
    public void stepInTime( double dt ) {}
    
    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------
    
    public String toString() {
        String s = "Photon ";
        s += ( "id=" + getId() + " " );
        s += ( "position=" + DebugUtils.format( getPositionRef() ) + " " );
        s += ( "orientation=" + DebugUtils.format( Math.toDegrees( getOrientation() ) ) + " " );
        return s;
    }
}
