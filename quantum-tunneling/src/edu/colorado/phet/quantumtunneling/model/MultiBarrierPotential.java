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
public class MultiBarrierPotential extends AbstractPotential {
    
    private static final double DEFAULT_BARRIER_POSITION = 5;
    private static final double DEFAULT_BARRIER_WIDTH = 3;
    private static final double DEFAULT_BARRIER_ENERGY = 5;
    
    /* minimum gap between the two barriers (min width of region 2) */
    private double _minGap;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MultiBarrierPotential( int numberOfBarriers ) {
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
        _minGap = Double.MIN_VALUE;
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

    public int getNumberOfBarriers() {
        return ( getNumberOfRegions() - 1 ) / 2;
    }
    
    public double getMinGap() {
        return _minGap;
    }
    
    /**
     * Sets the position of a barrier.
     * The barrier is only moved if it doesn't impact the size and location
     * of the other barrier, and if the minimum gap between the barriers 
     * can be maintained.
     *  
     * @param position
     */
    public boolean setPosition( int barrierIndex, double position ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        if ( position == MIN_POSITION || position == MAX_POSITION ) {
            throw new IllegalArgumentException( "position cannot be at min or max range" );
        }
        
        boolean success = false;
        
        int regionIndex = toRegionIndex( barrierIndex );
        PotentialRegion barrier = getRegion( regionIndex );
        PotentialRegion before = getRegion( regionIndex - 1 );
        PotentialRegion after = getRegion( regionIndex + 1 );
        
        if ( position >= before.getStart() + _minGap &&
             position + barrier.getWidth() <= after.getEnd() - _minGap )
        {
            // move the barrier
            double start1 = position;
            double end1 = position + barrier.getWidth();
            double energy1 = barrier.getEnergy();
            setRegion( regionIndex, start1, end1, energy1 );
            
            // shrink or expand the region after the barrier
            double start2 = end1;
            double end2 = after.getEnd();
            double energy2 = after.getEnergy();
            setRegion( regionIndex + 1, start2, end2, energy2 );
            
            // shrink or expand the region before the barrier
            double start3 = before.getStart();
            double end3 = position;
            double energy3 = before.getEnergy();
            setRegion( regionIndex - 1, start3, end3, energy3 );
            
            success = true;
        }
        return success;
    }
    
    public double getPosition( int barrierIndex ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        int regionIndex = toRegionIndex( barrierIndex );
        return getRegion( regionIndex ).getStart();
    }
    
    public boolean setWidth( int barrierIndex, double width ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "width is out of range: " + width );
        }
        
        boolean success = false;
        int regionIndex = toRegionIndex( barrierIndex );
        PotentialRegion barrier = getRegion( regionIndex );
        PotentialRegion after = getRegion( regionIndex + 1 );
        
        if ( barrier.getStart() + width <= after.getEnd() - _minGap ) {
            double start1 = barrier.getStart();
            double end1 = start1 + width;
            double energy1 = barrier.getEnergy();
            setRegion( regionIndex, start1, end1, energy1 );
            double start2 = end1;
            double end2 = after.getEnd();
            double energy2 = after.getEnergy();
            setRegion( regionIndex + 1, start2, end2, energy2 );
            success = true;
        }
        
        return success;
    }
    
    public double getWidth( int barrierIndex ) {
        if ( barrierIndex > getNumberOfBarriers() - 1 ) {
            throw new IllegalArgumentException( "barrierIndex out of range: " + barrierIndex );
        }
        int regionIndex = toRegionIndex( barrierIndex );
        return getRegion( regionIndex ).getWidth();
    }
    
    private int toRegionIndex( int barrierIndex ) {
        return ( barrierIndex * 2 ) + 1;
    }
}
