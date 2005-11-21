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
    private static final double DEFAULT_MIN_GAP = 0.25;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    /* minimum gap between the two barriers (min width of region 2) */
    private double _minGap;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a single barrier.
     */
    public BarrierPotential() {
        this( 1 );
    }
    
    /**
     * Creates a specified number of barriers, with a specified minimum
     * gap between the barriers.
     * 
     * @param numberOfBarriers
     * @param minGap
     */
    public BarrierPotential( int numberOfBarriers, double minGap ) {
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
                double energy = ( i % 2 == 0 ) ? 0 : DEFAULT_BARRIER_ENERGY;
                setRegion( i, start, end, energy );
            }
        }
        _minGap = minGap;
    }
    
    /**
     * Creates a multi-barrier with a default minimum gap size between barriers.
     * 
     * @param numberOfBarriers
     */
    public BarrierPotential( int numberOfBarriers ) {
        this( numberOfBarriers, DEFAULT_MIN_GAP );
    }
    
    /**
     * Copy constructor.
     * 
     * @param barrier
     */
    public BarrierPotential( BarrierPotential barrier ) {
        super( barrier );
        _minGap = barrier.getMinGap();
    }
    
    /**
     * Clones this object.
     */
    public Object clone() {
        return new BarrierPotential( this );
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
    public double getMinGap() {
        return _minGap;
    }
    
    /**
     * Sets the position of a barrier.
     * The barrier is only moved if it doesn't impact the size and location
     * of other barriers, and if the minimum gap between the barriers 
     * can be maintained.
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
        
        if ( position - _minGap >= left.getStart() &&
             position + barrier.getWidth() + _minGap <= right.getEnd() )
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
     * The barrier is only resized if it doesn't impact the size and location
     * of other barriers, and if the minimum gap between the barriers 
     * can be maintained.
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
        
        if ( barrier.getStart() + width + _minGap <= right.getEnd() ) {
            
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
    
    /**
     * Converts a barrier index to a region index.
     * 
     * @param barrierIndex
     */
    public static int toRegionIndex( int barrierIndex ) {
        return ( barrierIndex * 2 ) + 1;
    }
}
