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

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.hydrogenatom.util.DebugUtils;

/**
 * AlphaParticle is the model of an alpha particle.
 * An alpha particle has a position and direction of motion.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AlphaParticle extends MovingObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point2D _initialPosition; // required by Rutherford Scattering algorithm
    private double _initialSpeed; // required by Rutherford Scattering algorithm
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AlphaParticle( Point2D position, double orientation, double speed ) {
        super( position, orientation, speed );
        _initialPosition = new Point2D.Double( position.getX(), position.getY() );
        _initialSpeed = speed;
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
