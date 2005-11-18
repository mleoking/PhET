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
    
    /**
     * Creates a single barrier.
     */
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
    
    /**
     * Sets the barrier's position.
     * 
     * @param position
     */
    public void setBarrierPosition( double position ) {
        setBarrierPosition( 0, position );
    }
    
    /**
     * Gets the barrier's position.
     * 
     * @return position
     */
    public double getBarrierPosition() {
        return getBarrierPosition( 0 );
    }
    
    /**
     * Sets the barrier's width.
     * 
     * @param width
     */
    public void setBarrierWidth( double width ) {
        setBarrierWidth( 0, width );
    }
    
    /**
     * Gets the barrier's width.
     * 
     * @return width
     */
    public double getBarrierWidth() {
        return getBarrierWidth( 0 );
    }
}
