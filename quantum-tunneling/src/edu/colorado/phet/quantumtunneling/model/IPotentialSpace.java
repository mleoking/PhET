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


/**
 * IPotentialSpace is the interface for implementing as "potential space".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IPotentialSpace {
   
    public int getNumberOfRegions();
    
    public PotentialRegion[] getRegions();
    
    public PotentialRegion getRegion( final int index );      
}
