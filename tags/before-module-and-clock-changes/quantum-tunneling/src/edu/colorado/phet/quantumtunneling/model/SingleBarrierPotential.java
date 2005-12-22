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

import edu.colorado.phet.quantumtunneling.enum.PotentialType;


/**
 * SingleBarrierPotential is a convenience class for creating
 * and testing for single barriers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SingleBarrierPotential extends BarrierPotential {
    
    public SingleBarrierPotential() {
        super( 1 /* numberOfBarriers */  );
    }
    
    public SingleBarrierPotential( SingleBarrierPotential barrier ) {
        super( barrier  );
    }
}
