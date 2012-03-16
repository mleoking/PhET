// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * DeBroglieEnergyDiagram is the energy diagram for the deBroglie model.
 * It is identical to the diagram for the Bohr model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieEnergyDiagram extends BohrEnergyDiagram {

    /**
     * Constructor.
     * @param canvas
     * @param clock
     */
    public DeBroglieEnergyDiagram( IClock clock ) {
        super( clock );
    }
}
