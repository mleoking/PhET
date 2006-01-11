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
 * DoubleBarrierPotential is a convenience class for creating
 * and testing for double barriers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DoubleBarrierPotential extends BarrierPotential {

    public DoubleBarrierPotential() {
        super( 2 /* numberOfBarriers */  );
    }
    
    public DoubleBarrierPotential( DoubleBarrierPotential barrier ) {
        super( barrier  );
    }
}
