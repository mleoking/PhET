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
public abstract class AbstractPotentialSpace extends QTObservable {

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
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the number of regions in the potential space.
     * 
     * @return
     */
    public int getNumberOfRegions() {
        return _regions.length;
    }
    
    /**
     * Gets the start position of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getStart( int regionIndex ) {
        return getRegion( regionIndex ).getStart();
    }
    
    /**
     * Gets the end position of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getEnd( int regionIndex ) {
        return getRegion( regionIndex ).getEnd();
    }
    
    /**
     * Gets the middle position of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getMiddle( int regionIndex ) {
        return getRegion( regionIndex ).getMiddle();
    }
    
    /**
     * Gets the width of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getWidth( int regionIndex ) {
        return getRegion( regionIndex ).getWidth();
    }
    
    /**
     * Gets the energy of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getEnergy( int regionIndex ) {
        return getRegion( regionIndex ).getEnergy();
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
     * Gets the range of the space.
     * 
     * @return
     */
    public Range getPositionRange() {
        Range rangeRef = getPositionRangeReference();
        return new Range( rangeRef.getLowerBound(), rangeRef.getUpperBound() );
    }
    
    /**
     * Gets the position where the space begins.
     * 
     * @return
     */
    public double getMinPosition() {
        return getPositionRangeReference().getLowerBound();
    }
    
    /**
     * Gets the position where the space ends.
     * 
     * @return
     */
    public double getMaxPosition() {
        return getPositionRangeReference().getUpperBound();
    }
    
    /**
     * Gets the minimum region width.
     * 
     * @return minimum gap size
     */
    public double getMinRegionWidth() {
        return _minRegionWidth;
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
     * Gets the index of the region that contains a specified position.
     * If the position is outside the space, -1 is returned.
     * If the position falls on the boundary of two regions,
     * the region whose end point matches the position is 
     * returned.
     * 
     * @param position
     * @return the region (possibly null)
     */
    public int getRegionIndexAt( double position ) {
        int regionIndex = -1;
        for ( int i = 0; i < getNumberOfRegions() && regionIndex == -1; i++ ) {
            double start = getRegion( i ).getStart();
            double end = getRegion( i ).getEnd();
            if ( position >= start && position <= end ) {
                regionIndex = i;
            }
        }
        return regionIndex;
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
        int regionIndex = getRegionIndexAt( position );
        if ( regionIndex != -1 ) {
            energy = getRegion( regionIndex ).getEnergy();
        }
        return energy;
    }
    
    /**
     * Adjusts the boundary between two regions.
     * The boundary is adjusted only if doing so doesn't violate the
     * minimum region width.
     * 
     * @param leftRegionIndex
     * @param position
     * @return true or false
     */
    public boolean adjustBoundary( int leftRegionIndex, double position ) {
        validateRegionIndex( leftRegionIndex );
        
        if ( getNumberOfRegions() == 1 ) {
            // there are no boundaries in a single-region space
            return false;
        }
        if ( leftRegionIndex == getNumberOfRegions() - 1 ) {
            // can't adjust the right edge of the rightmost region
            return false;
        }
        
        boolean success = false;
        
        double width1 = position - getStart( leftRegionIndex );
        double width2 = getEnd( leftRegionIndex + 1 ) - position;
        if ( width1 >= _minRegionWidth && width2 >= _minRegionWidth ) {
            setEnd( leftRegionIndex, position );
            setStart( leftRegionIndex + 1, position );
            validateRegions();
            success = true;
        }
        return success;
    }
       
    //----------------------------------------------------------------------------
    // Private & protected accessors
    //----------------------------------------------------------------------------
 
    /*
     * Gets a specified region.  This is private because clients and subclasses
     * should not be accessing regions directly.  Use one of the many public
     * accessor methods that have a regionIndex parameter.
     * 
     * @param index
     * @return
     */
    private PotentialRegion getRegion( int index ) {
        validateRegionIndex( index );
        return _regions[index];
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions. Subclasses should
     * call validateRegions after adjusting all regions.
     */
    protected void setRegion( int index, double start, double end, double energy ) {
        validateRegionIndex( index );
        _regions[ index ] = new PotentialRegion( start, end, energy );
        notifyObservers();
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions. Subclasses should
     * call validateRegions after adjusting all regions.
     */
    protected void setRegion( int index, double start, double end ) {
        validateRegionIndex( index );
        double energy = getRegion( index ).getEnergy();
        setRegion( index, start, end, energy );
    }
   
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions. Subclasses should
     * call validateRegions after adjusting all regions.
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
    
    /*
     * Gets a direct reference to the range of the space.
     * The returned value must not be modified!
     * 
     * @return
     */
    private Range getPositionRangeReference() {
        return QTConstants.POSITION_RANGE;
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
     * Verifies that all regions are contiguous and have the correct minimum width.
     */
    protected void validateRegions() {
        int errors = 0;
        for ( int i = 1; i < _regions.length - 1; i++ ) {
            if ( _regions[i].getEnd() != _regions[i+1].getStart() ) {
                errors++;
                System.err.println( "ERROR: regions " + i + " and " + ( i + 1 ) + " are not contiguous" );
            }
            if ( _regions[i].getWidth() < _minRegionWidth ) {
                errors++;
                System.err.println( "ERROR: region " + i + " is smaller than the minimum width" );
            }
        }
        if ( errors != 0 ) {
            throw new IllegalStateException( "potential space is invalid" );
        }
    }
}
