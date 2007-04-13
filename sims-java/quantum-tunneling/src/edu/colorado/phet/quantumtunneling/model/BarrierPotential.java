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

import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * BarrierPotential describes a potential space that contains 
 * one or more barriers or wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarrierPotential extends AbstractPotential {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a specified number of barriers.
     * 
     * @param numberOfBarriers
     */
    public BarrierPotential( final int numberOfBarriers ) {
        super( ( numberOfBarriers * 2 ) + 1 /* number of regions */ );
        
        if ( numberOfBarriers <= 0 ) {
            throw new IllegalArgumentException( "numberOfBarriers must be > 0: " + numberOfBarriers );
        }
        
        for ( int i = 0; i < getNumberOfRegions(); i++ ) {
            if ( i == 0 ) {
                setRegion( i, getMinPosition(), QTConstants.DEFAULT_BARRIER_POSITION, 0 );
            }
            else if ( i == getNumberOfRegions() - 1 ) {
                setRegion( i, getEnd( i-1 ), getMaxPosition(), 0 );    
            }
            else if ( isaBarrier( i ) ) {
                // barrier
                double start = getEnd( i-1 );
                double end = start + QTConstants.DEFAULT_BARRIER_WIDTH;
                double energy = QTConstants.DEFAULT_BARRIER_ENERGY;
                setRegion( i, start, end, energy );
            }
            else {
                // space between barriers
                double start = getEnd( i-1 );
                double end = start + QTConstants.DEFAULT_SPACE_BETWEEN_BARRIERS;
                double energy = 0;
                setRegion( i, start, end, energy ); 
            }
        }
        validateRegions();
    }
    
    /**
     * Copy constructor.
     * 
     * @param barrier
     */
    public BarrierPotential( final BarrierPotential barrier ) {
        super( barrier );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the number of barriers.
     * 
     * @return number of barriers
     */
    public int getNumberOfBarriers() {
        return ( getNumberOfRegions() - 1 ) / 2;
    }
    
    /**
     * Sets the position of a barrier.
     * The barrier is only moved if it the minumum region size isn't violated.
     *  
     * @param position true or false
     */
    public boolean setBarrierPosition( final int barrierIndex, final double position ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        
        boolean success = false;
        
        final int regionIndex = toRegionIndex( barrierIndex );
        final double minRegionWidth = getMinRegionWidth();
        
        if ( position - minRegionWidth >= getStart( regionIndex - 1 ) &&
             position + getWidth( regionIndex ) + minRegionWidth <= getEnd( regionIndex + 1 ) )
        {
            setNotifyEnabled( false );
            
            // move the barrier
            final double start = position;
            final double end = position + getWidth( regionIndex );
            setRegion( regionIndex, start, end );
           
            // move the end point of the region to the left of the barrier
            setEnd( regionIndex - 1, start );
            
            // move the start point of the region to the right of the barrier
            setStart( regionIndex + 1, end );
            
            setNotifyEnabled( true );
            
            validateRegions();
            success = true;
        }

        return success;
    }
    
    /**
     * Gets the position of a specified barrier.
     * 
     * @param barrierIndex
     * @return the postion
     */
    public double getBarrierPosition( final int barrierIndex ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        int regionIndex = toRegionIndex( barrierIndex );
        return getStart( regionIndex );
    }
    
    /**
     * Sets the width of a specified barrier.
     * The barrier is only resized if it the minumum region size isn't violated.
     *  
     * @param barrierIndex
     * @param width
     * @param position true or false
     */
    public boolean setBarrierWidth( final int barrierIndex, final double width ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        
        boolean success = false;
        
        final int regionIndex = toRegionIndex( barrierIndex );
        final double minRegionWidth = getMinRegionWidth();
        
        if ( width >= minRegionWidth && 
            getStart( regionIndex ) + width + minRegionWidth <= getEnd( regionIndex + 1 ) ) {
            
            setNotifyEnabled( false );
            
            // move the end of the barrier's region
            final double end = getStart( regionIndex ) + width;
            setEnd( regionIndex, end );
            
            // move the start point of the region to the right of the barrier
            setStart( regionIndex + 1, end );
            
            setNotifyEnabled( true );
            
            validateRegions();
            success = true;
        }
        
        return success;
    }
    
    /**
     * Gets the width of a specified barrier.
     * 
     * @param barrierIndex
     * @return the width
     */
    public double getBarrierWidth( final int barrierIndex ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        final int regionIndex = toRegionIndex( barrierIndex );
        return getWidth( regionIndex );
    }
    
    //----------------------------------------------------------------------------
    // Static utilities
    //----------------------------------------------------------------------------
    
    /**
     * Converts a barrier index to a region index.
     * 
     * @param barrierIndex
     */
    public static int toRegionIndex( final int barrierIndex ) {
        return ( barrierIndex * 2 ) + 1;
    }
    
    /**
     * Converts a region index to a barrier index.
     * 
     * @param regionIndex
     * @return barrierIndex, -1 if the region is not a barrier
     */
    public static int toBarrierIndex( final int regionIndex ) {
        int barrierIndex = -1;
        if ( isaBarrier( regionIndex ) ) {
            barrierIndex = ( regionIndex - 1 ) / 2;
        }
        return barrierIndex;
    }
    
    /**
     * Is the region a barrier?
     * 
     * @param regionIndex
     * @return true or false
     */
    public static boolean isaBarrier( final int regionIndex ) {
        return ( regionIndex % 2 != 0 );
    }
}
