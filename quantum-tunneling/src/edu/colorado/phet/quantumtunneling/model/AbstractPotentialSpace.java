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

import org.jfree.data.Range;

import edu.colorado.phet.quantumtunneling.QTConstants;



/**
 * AbstractPotentialSpace is the abstract base class for all potential spaces.
 * The space is finite; it has non-infinite beginning and end positions.
 * The regions in the space are guaranteed to be contiguous; there are no gaps
 * between the regions, and the regions do not overlap.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractPotentialSpace extends QTObservable implements IPotentialSpace {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_MIN_REGION_WIDTH = 0.5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PotentialRegion[] _regions;
    
    /* minimum region width */
    private double _minRegionWidth;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor, for use by subclasses.
     * 
     * @param numberOfRegions
     */
    protected AbstractPotentialSpace( int numberOfRegions ) {
        super();
        
        if ( numberOfRegions <= 0 ) {
            throw new IllegalArgumentException( "numberOfRegions must be > 0" );
        }
        
        _regions = new PotentialRegion[ numberOfRegions ];
        if ( numberOfRegions == 1 ) {
            _regions[0] = new PotentialRegion( getMinPosition(), getMaxPosition(), 0 );
        }
        else {
            for ( int i = 0; i < numberOfRegions; i++ ) {
                if ( i == 0 ) {
                    _regions[i] = new PotentialRegion( getMinPosition(), i+1, 0 );
                }
                else if ( i == numberOfRegions - 1 ) {
                    _regions[i] = new PotentialRegion( i, getMaxPosition(), 0 );
                }
                else {
                    _regions[i] = new PotentialRegion( i, i+1, 0 ); 
                }
            }
        }
        
        _minRegionWidth = DEFAULT_MIN_REGION_WIDTH;
    }
    
    /**
     * Copy constructor, for use by subclasses.
     * 
     * @param potential
     */
    protected AbstractPotentialSpace( AbstractPotentialSpace potential ) {
        super();
        
        _regions = new PotentialRegion[ potential.getNumberOfRegions() ];
        for ( int i = 0; i < potential.getNumberOfRegions(); i ++ ) {
            double start = potential.getRegion( i ).getStart();
            double end = potential.getRegion( i ).getEnd();
            double energy = potential.getRegion( i ).getEnergy();
            setRegion( i, start, end, energy );
        }
        
        _minRegionWidth = potential.getMinRegionWidth();
    }
    
    //----------------------------------------------------------------------------
    // IPotentialSpace implementation
    //----------------------------------------------------------------------------
    
    public int getNumberOfRegions() {
        return _regions.length;
    }

    public PotentialRegion[] getRegions() {
        return _regions;
    }
    
    public PotentialRegion getRegion( int index ) {
        validateRegionIndex( index );
        return _regions[index];
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the range of the space.
     * 
     * @return
     */
    public Range getPositionRange() {
        return QTConstants.POSITION_RANGE;
    }
    
    /**
     * Gets the position where the space begins.
     * 
     * @return
     */
    public double getMinPosition() {
        return getPositionRange().getLowerBound();
    }
    
    /**
     * Gets the position where the space ends.
     * 
     * @return
     */
    public double getMaxPosition() {
        return getPositionRange().getUpperBound();
    }
    
    /**
     * Sets the minimum region width.
     * 
     * @param minRegionWidth
     */
    public void setMinRegionWidth( double minRegionWidth ) {
        if ( minRegionWidth <= 0 ) {
            throw new IllegalArgumentException( "minRegionWidth must be > 0" );
        }
        _minRegionWidth = minRegionWidth;
        notifyObservers();
    }
    
    /**
     * Gets the minimum region width.
     * 
     * @return minimum gap size
     */
    public double getMinRegionWidth() {
        return _minRegionWidth;
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    protected void setRegion( int index, double start, double end, double energy ) {
        validateRegionIndex( index );
        _regions[ index ] = new PotentialRegion( start, end, energy );
        notifyObservers();
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    protected void setRegion( int index, double start, double end ) {
        validateRegionIndex( index );
        double energy = getRegion( index ).getEnergy();
        setRegion( index, start, end, energy );
    }
   
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    protected void setStart( int regionIndex, double start ) {
        validateRegionIndex( regionIndex );
        double end = getRegion( regionIndex ).getEnd();
        double energy = getRegion( regionIndex ).getEnergy();
        setRegion( regionIndex, start, end, energy ); 
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    protected void setEnd( int regionIndex, double end ) {
        validateRegionIndex( regionIndex );
        double start = getRegion( regionIndex ).getStart();
        double energy = getRegion( regionIndex ).getEnergy();
        setRegion( regionIndex, start, end, energy );  
    }
    
    /**
     * Sets the potential energy of a specified region.
     * 
     * @param regionIndex
     * @param energy
     */
    public void setEnergy( int regionIndex, double energy ) {
        validateRegionIndex( regionIndex );
        double start = getRegion( regionIndex ).getStart();
        double end = getRegion( regionIndex ).getEnd();
        setRegion( regionIndex, start, end, energy );
    }
    
    /**
     * Gets the region that contains a specified position.
     * If the position is outside the space, null is returned.
     * If the position falls on the boundary of two regions,
     * the region whose end point matches the position is 
     * returned.
     * 
     * @param position
     * @return the region (possibly null)
     */
    public PotentialRegion getRegionAt( double position ) {
        PotentialRegion region = null;
        for ( int i = 0; i < getNumberOfRegions() && region == null; i++ ) {
            double start = getRegion( i ).getStart();
            double end = getRegion( i ).getEnd();
            if ( position >= start && position <= end ) {
                region = getRegion( i );
            }
        }
        return region;
    }
    
    /**
     * Gets the energy at a specified position.
     * If the position is outside the space, 0 is returned.
     * If the position falls on the boundary of two regions,
     * the energy of the region whose end point matches the
     * position is returned.
     * 
     * @param position
     * @return energy
     */
    public double getEnergyAt( double position ) {
        double energy = 0;
        PotentialRegion region = getRegionAt( position );
        if ( region != null ) {
            energy = region.getEnergy();
        }
        return energy;
    }
       
    //----------------------------------------------------------------------------
    // Validation
    //----------------------------------------------------------------------------
    
    /*
     * Verifies that a region index is valid.
     */
    private void validateRegionIndex( int regionIndex ) {
        if ( regionIndex < 0 || regionIndex > _regions.length - 1 ) {
            throw new IndexOutOfBoundsException( "regionIndex out of bounds: " + regionIndex );
        }
    }
    
    /*
     * Verifies that all regions are contiguous.
     */
    protected void validateRegions() {
        int gapCount = 0;
        for ( int i = 1; i < _regions.length - 1; i++ ) {
            if ( _regions[i].getEnd() != _regions[i+1].getStart() ) {
                gapCount++;
                System.err.println( "ERROR: regions " + i + " and " + ( i + 1 ) + " are not contiguous!" );
            }
        }
        if ( gapCount != 0 ) {
            throw new IllegalStateException( "all regions must be contiguous" );
        }
    }
}
