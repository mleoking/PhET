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
 * BarrierPotential creates a potential space with a specified number of barriers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarrierPotential extends AbstractPotentialEnergy {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_BARRIER_POSITION = 5;
    private static final double DEFAULT_BARRIER_WIDTH = 3;
    private static final double DEFAULT_BARRIER_ENERGY = 5;
    private static final double DEFAULT_MIN_REGION_WIDTH = 0.5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    /* minimum region width */
    private double _minRegionWidth;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a single barrier, with a default minimum region size.
     */
    public BarrierPotential() {
        this( 1 );
    }
    
    /**
     * Creates a specified number of barriers, with a default minimum region width.
     * 
     * @param numberOfBarriers
     */
    public BarrierPotential( int numberOfBarriers ) {
        this( numberOfBarriers, DEFAULT_MIN_REGION_WIDTH );
    }
    
    /**
     * Creates a specified number of barriers, with a specified minimum region width.
     * 
     * @param numberOfBarriers
     * @param minRegionWidth
     */
    public BarrierPotential( int numberOfBarriers, double minRegionWidth ) {
        super( ( numberOfBarriers * 2 ) + 1 /* number of regions */ );
        for ( int i = 0; i < getNumberOfRegions(); i++ ) {
            if ( i == 0 ) {
                setRegion( i, MIN_POSITION, DEFAULT_BARRIER_POSITION, 0 );
            }
            else if ( i == getNumberOfRegions() - 1 ) {
                setRegion( i, getRegion( i-1 ).getEnd(), MAX_POSITION, 0 );    
            }
            else {
                double start = getRegion( i-1 ).getEnd();
                double end = start + DEFAULT_BARRIER_WIDTH;
                double energy = isaBarrier( i ) ? DEFAULT_BARRIER_ENERGY : 0;
                setRegion( i, start, end, energy );
            }
        }
        _minRegionWidth = minRegionWidth;
    }
    
    /**
     * Copy constructor.
     * 
     * @param barrier
     */
    public BarrierPotential( BarrierPotential barrier ) {
        super( barrier );
        _minRegionWidth = barrier.getMinRegionWidth();
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
     * Gets the size of the minimum gap between barriers.
     * 
     * @return minimum gap size
     */
    public double getMinRegionWidth() {
        return _minRegionWidth;
    }
    
    /**
     * Sets the position of a barrier.
     * The barrier is only moved if it the minumum region size isn't violated.
     *  
     * @param position true or false
     */
    public boolean setBarrierPosition( int barrierIndex, double position ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        if ( position == MIN_POSITION || position == MAX_POSITION ) {
            throw new IllegalArgumentException( "position cannot be at min or max range" );
        }
        
        boolean success = false;
        
        int regionIndex = toRegionIndex( barrierIndex );
        PotentialRegion barrier = getRegion( regionIndex );
        PotentialRegion left = getRegion( regionIndex - 1 );
        PotentialRegion right = getRegion( regionIndex + 1 );
        
        if ( position - _minRegionWidth >= left.getStart() &&
             position + barrier.getWidth() + _minRegionWidth <= right.getEnd() )
        {
            setNotifyEnabled( false );
            
            // move the barrier
            double start = position;
            double end = position + barrier.getWidth();
            setStart( regionIndex, start );
            setEnd( regionIndex, end );
           
            // move the end point of the region to the left of the barrier
            setEnd( regionIndex - 1, start );
            
            // move the start point of the region to the right of the barrier
            setStart( regionIndex + 1, end );
            
            setNotifyEnabled( true );
            
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
    public double getBarrierPosition( int barrierIndex ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        int regionIndex = toRegionIndex( barrierIndex );
        return getRegion( regionIndex ).getStart();
    }
    
    /**
     * Sets the width of a specified barrier.
     * The barrier is only resized if it the minumum region size isn't violated.
     *  
     * @param barrierIndex
     * @param width
     * @param position true or false
     */
    public boolean setBarrierWidth( int barrierIndex, double width ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width is out of range: " + width );
        }
        
        boolean success = false;
        int regionIndex = toRegionIndex( barrierIndex );
        PotentialRegion barrier = getRegion( regionIndex );
        PotentialRegion right = getRegion( regionIndex + 1 );
        
        if ( width >= _minRegionWidth && 
            barrier.getStart() + width + _minRegionWidth <= right.getEnd() ) {
            
            setNotifyEnabled( false );
            
            // move the end of the barrier's region
            double end = barrier.getStart() + width;
            setEnd( regionIndex, end );
            
            // move the start point of the region to the right of the barrier
            setStart( regionIndex + 1, end );
            
            setNotifyEnabled( true );
            
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
    public double getBarrierWidth( int barrierIndex ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        int regionIndex = toRegionIndex( barrierIndex );
        return getRegion( regionIndex ).getWidth();
    }
    
    //----------------------------------------------------------------------------
    // Static utilities
    //----------------------------------------------------------------------------
    
    /**
     * Converts a barrier index to a region index.
     * 
     * @param barrierIndex
     */
    public static int toRegionIndex( int barrierIndex ) {
        return ( barrierIndex * 2 ) + 1;
    }
    
    /**
     * Converts a region index to a barrier index.
     * 
     * @param regionIndex
     * @return barrierIndex, -1 if the region is not a barrier
     */
    public static int toBarrierIndex( int regionIndex ) {
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
    public static boolean isaBarrier( int regionIndex ) {
        return ( regionIndex % 2 != 0 );
    }
}
