/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import java.awt.geom.Point2D;


/**
 * BarrierPotential is a convenience class for creating a single barrier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarrierPotential extends MultiBarrierPotential {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BarrierPotential() {
        super( 1 /* number of barriers */ );
    }
    
    /**
     * Copy constructor.
     * 
     * @param barrier
     */
    public BarrierPotential( BarrierPotential barrier ) {
        super( barrier );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPosition( double position ) {
        setPosition( 0, position );
    }
    
    public double getPosition() {
        return getPosition( 0 );
    }
    
    public void setWidth( double width ) {
        setWidth( 0, width );
    }
    
    public double getWidth() {
        return getWidth( 0 );
    }
}
