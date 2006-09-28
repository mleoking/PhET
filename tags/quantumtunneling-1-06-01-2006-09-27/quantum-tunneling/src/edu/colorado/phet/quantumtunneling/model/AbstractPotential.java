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
 * AbstractPotential is the abstract base class for all potential spaces.
 * The space is finite; it has non-infinite beginning and end positions.
 * The regions in the space are guaranteed to be contiguous; there are no gaps
 * between the regions, and the regions do not overlap.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractPotential extends QTObservable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /*
     * Double precision numbers have some problems representing
     * numbers like 0.1.  This value determines how close we have to
     * be to QTConstants.MIN_REGION_WIDTH to be considered "equal".
     */
    private static final double ACCEPTABLE_WIDTH_ERROR = 0.001;
    
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
    protected AbstractPotential( final int numberOfRegions ) {
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
        
        _minRegionWidth = QTConstants.MIN_REGION_WIDTH;
    }
    
    /**
     * Copy constructor, for use by subclasses.
     * 
     * @param potential
     */
    protected AbstractPotential( AbstractPotential potential ) {
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
     * This method should be used only by persistence code, 
     * to restore the regions.
     * 
     * @param regions
     */
    public void setRegions( PotentialRegion[] regions ) {
        _regions = regions;
        validateRegions();
    }
    
    /**
     * This method should be used only by persistence code,
     * to save the regions.
     * 
     * @return
     */
    public PotentialRegion[] getRegions() {
        return _regions;
    }
    
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
    public double getStart( final int regionIndex ) {
        return getRegion( regionIndex ).getStart();
    }
    
    /**
     * Gets the end position of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getEnd( final int regionIndex ) {
        return getRegion( regionIndex ).getEnd();
    }
    
    /**
     * Gets the middle position of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getMiddle( final int regionIndex ) {
        return getRegion( regionIndex ).getMiddle();
    }
    
    /**
     * Gets the width of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getWidth( final int regionIndex ) {
        return getRegion( regionIndex ).getWidth();
    }
    
    /**
     * Gets the energy of a specified region.
     * 
     * @param regionIndex
     * @return
     */
    public double getEnergy( final int regionIndex ) {
        return getRegion( regionIndex ).getEnergy();
    }
    
    /**
     * Sets the potential energy of a specified region.
     * 
     * @param regionIndex
     * @param energy
     */
    public void setEnergy( final int regionIndex, final double energy ) {
        validateRegionIndex( regionIndex );
        final double start = getRegion( regionIndex ).getStart();
        final double end = getRegion( regionIndex ).getEnd();
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
    public void setMinRegionWidth( final double minRegionWidth ) {
        if ( minRegionWidth <= 0 ) {
            throw new IllegalArgumentException( "minRegionWidth must be > 0" );
        }
        _minRegionWidth = minRegionWidth;
        notifyObservers();
    }
    
    /**
     * Gets the index of the region that contains a specified position.
     * The first and last regions are treated as if they go on forever.
     * 
     * @param position
     * @return the region index
     */
    public int getRegionIndexAt( final double position ) {
        final int numberOfRegions = _regions.length;
        int regionIndex = numberOfRegions - 1;
        for ( int i = 0; i < numberOfRegions; i++ ) {
            if ( position < getRegion( i ).getEnd() ) {
                regionIndex = i;
                break;
            }
        }
        return regionIndex;
    }
    
    /**
     * Gets the energy at a specified position.
     * The first and last regions are treated as if they go on forever.
     * 
     * @param position
     * @return energy
     */
    public double getEnergyAt( final double position ) {
        final int regionIndex = getRegionIndexAt( position ); // always returns a valid region index
        PotentialRegion region = getRegion( regionIndex );
        return region.getEnergy();
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
    public boolean adjustBoundary( final int leftRegionIndex, final double position ) {
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
        
        final double width1 = position - getStart( leftRegionIndex ) + ACCEPTABLE_WIDTH_ERROR;
        final double width2 = getEnd( leftRegionIndex + 1 ) - position + ACCEPTABLE_WIDTH_ERROR;
        if ( width1 >= _minRegionWidth && width2 >= _minRegionWidth ) {
            setNotifyEnabled( false );
            setEnd( leftRegionIndex, position );
            setStart( leftRegionIndex + 1, position );
            validateRegions();
            setNotifyEnabled( true );
            success = true;
        }

        return success;
    }
    
    /**
     * Determines if a position is in a well.
     * A position is in a well if there is a higher potential on 
     * both the left and right of the position.
     * 
     * @param position
     * @return true or false
     */
    public boolean isInWell( final double position ) {

        boolean inWell = false;
        
        final int numberOfRegions = getNumberOfRegions();
        if ( numberOfRegions > 2 ) {
            
            final int regionIndex = getRegionIndexAt( position );
            
            if ( regionIndex > 0 && regionIndex < numberOfRegions - 1 ) {
                
                // Potential at the position...
                final double energy = getEnergy( regionIndex );
                
                // Find a higher potential to the left...
                boolean higherOnLeft = false;
                for ( int i = 0; i < regionIndex; i++ ) {
                    double leftEnergy = getEnergy( i );
                    if ( leftEnergy > energy ) {
                        higherOnLeft = true;
                        break;
                    }
                }
                
                // Find a higher potential to the right...
                boolean higherOnRight = false;
                for ( int i = regionIndex + 1; i < numberOfRegions; i++ ) {
                    double rightEnergy = getEnergy( i );
                    if ( rightEnergy > energy ) {
                        higherOnRight = true;
                        break;
                    }
                }
                
                inWell = ( higherOnLeft && higherOnRight );
            }
        }
        return inWell;
    }
       
    /**
     * For a position that is in a well, this method returns the 
     * energy of the "top" of the well.  The top of the well is 
     * the lower of the two "sides" of the well.
     * 
     * @param position
     * @return
     */
    public double getTopOfWellAt( final double position ) {
        
        assert( isInWell( position ) ); // check this as an assertion for performance reasons
        
        final int numberOfRegions = getNumberOfRegions();
        final int regionIndex = getRegionIndexAt( position );
        final double energy = getEnergy( regionIndex );
        
        // Find energy of the left side of the well...
        double leftEnergy = energy;
        for ( int i = regionIndex - 1; i >= 0; i-- ) {
            double e = getEnergy( i );
            if ( e > energy ) {
                if ( e > leftEnergy ) {
                    leftEnergy = e;
                }
                else {
                    break;
                }
            }
        }
        
        // Find energy of the right side of the well...
        double rightEnergy = energy;
        for ( int i = regionIndex + 1; i < numberOfRegions; i++ ) {
            double e = getEnergy( i );
            if ( e > energy ) {
                if ( e > rightEnergy ) {
                    rightEnergy = e;
                }
                else {
                    break;
                }
            }
        }
        
        // return the lower of the two sides of the well
        return Math.min( leftEnergy, rightEnergy );
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
    private PotentialRegion getRegion( final int index ) {
        validateRegionIndex( index );
        return _regions[index];
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions. Subclasses should
     * call validateRegions after adjusting all regions.
     */
    protected void setRegion( final int index, final double start, final double end, final double energy ) {
        validateRegionIndex( index );
        _regions[ index ] = new PotentialRegion( start, end, energy );
        notifyObservers();
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions. Subclasses should
     * call validateRegions after adjusting all regions.
     */
    protected void setRegion( final int index, final double start, final double end ) {
        validateRegionIndex( index );
        final double energy = getRegion( index ).getEnergy();
        setRegion( index, start, end, energy );
    }
   
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions. Subclasses should
     * call validateRegions after adjusting all regions.
     */
    protected void setStart( final int regionIndex, final double start ) {
        validateRegionIndex( regionIndex );
        final double end = getRegion( regionIndex ).getEnd();
        final double energy = getRegion( regionIndex ).getEnergy();
        setRegion( regionIndex, start, end, energy ); 
    }
    
    /*
     * Not accessible to clients because we don't want them 
     * to be creating gaps between regions.
     */
    protected void setEnd( final int regionIndex, final double end ) {
        validateRegionIndex( regionIndex );
        final double start = getRegion( regionIndex ).getStart();
        final double energy = getRegion( regionIndex ).getEnergy();
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
    private void validateRegionIndex( final int regionIndex ) {
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
                String s = "ERROR: regions " + i + " and " + ( i + 1 ) + " are not contiguous: " +
                           _regions[i].getEnd() + " != " + _regions[i+1].getStart();
                System.err.println( s );
            }
            if ( _regions[i].getWidth() + ACCEPTABLE_WIDTH_ERROR < _minRegionWidth ) {
                errors++;
                String s = "ERROR: region " + i + " is smaller than the minimum width: " +
                           _regions[i].getWidth() + " < " + _minRegionWidth;
                System.err.println( s );
            }
        }
        if ( errors != 0 ) {
            Exception e = new IllegalStateException( "potential space is invalid" );
            e.printStackTrace();
        }
    }
}
