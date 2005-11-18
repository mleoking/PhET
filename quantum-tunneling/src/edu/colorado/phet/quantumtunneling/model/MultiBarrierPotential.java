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
 * MultiBarrierPotential creates a potential space with multiple barriers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MultiBarrierPotential extends AbstractPotentialEnergy {
    
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
     * Creates a barrier.
     * 
     * @param numberOfBarriers
     * @param minGap
     */
    public MultiBarrierPotential( int numberOfBarriers, double minGap ) {
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
    public MultiBarrierPotential( int numberOfBarriers ) {
        this( numberOfBarriers, DEFAULT_MIN_GAP );
    }
    
    /**
     * Copy constructor.
     * 
     * @param barrier
     */
    public MultiBarrierPotential( MultiBarrierPotential barrier ) {
        super( barrier );
        _minGap = barrier.getMinGap();
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
        
        if ( position >= left.getStart() + _minGap &&
             position + barrier.getWidth() <= right.getEnd() - _minGap )
        {
            // move the barrier
            double start1 = position;
            double end1 = position + barrier.getWidth();
            double energy1 = barrier.getEnergy();
            setRegion( regionIndex, start1, end1, energy1 );
            
            // shrink or expand the region to the right of the barrier
            double start2 = end1;
            double end2 = right.getEnd();
            double energy2 = right.getEnergy();
            setRegion( regionIndex + 1, start2, end2, energy2 );
            
            // shrink or expand the region to the left of the barrier
            double start3 = left.getStart();
            double end3 = position;
            double energy3 = left.getEnergy();
            setRegion( regionIndex - 1, start3, end3, energy3 );
            
            success = true;
            notifyObservers();
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
        
        if ( barrier.getStart() + width <= right.getEnd() - _minGap ) {
            
            // shrink or expand the barrier
            double start1 = barrier.getStart();
            double end1 = start1 + width;
            double energy1 = barrier.getEnergy();
            setRegion( regionIndex, start1, end1, energy1 );
            
            // shrink or expand the region to the right of the barrier
            double start2 = end1;
            double end2 = right.getEnd();
            double energy2 = right.getEnergy();
            setRegion( regionIndex + 1, start2, end2, energy2 );
            
            success = true;
            notifyObservers();
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
    
    /*
     * Converts a barrier index to a region index.
     * 
     * @param barrierIndex
     */
    private int toRegionIndex( int barrierIndex ) {
        return ( barrierIndex * 2 ) + 1;
    }
}
