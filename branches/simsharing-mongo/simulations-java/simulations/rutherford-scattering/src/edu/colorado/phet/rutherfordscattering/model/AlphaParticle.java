// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

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
    
    public void stepInTime( double dt ) {
        // do nothing -- alpha particle motion is handled in RSModel.clockTicked
    }
}
