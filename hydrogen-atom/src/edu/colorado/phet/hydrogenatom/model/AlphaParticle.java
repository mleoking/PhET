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
    
    private AbstractHydrogenAtom _atom; // atom that determines the alpha particle's motion
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AlphaParticle( Point2D position, double orientation, double speed ) {
        super( position, orientation, speed );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors implementation
    //----------------------------------------------------------------------------
    
    public void setAtom( AbstractHydrogenAtom atom ) {
        _atom = atom;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        _atom.move( this, dt );
    }
    
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
