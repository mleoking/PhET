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


/**
 * IPotentialSpace is the interface for implementing a "potential space".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IPotentialSpace {
   
    /**
     * Gets the number of regions in the potential space.
     * 
     * @return
     */
    public int getNumberOfRegions();
    
    /**
     * Gets the set of regions that make up the potential space.
     * The regions should be continguous, with no gaps between
     * regions.  The regions appear in order of ascending position.
     * 
     * @return
     */
    public PotentialRegion[] getRegions();
    
    /**
     * Gets a specified region.
     * 
     * @param index
     * @return
     */
    public PotentialRegion getRegion( final int index );  
}
