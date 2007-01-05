/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import edu.colorado.phet.common.model.clock.IClock;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * DeBroglieEnergyDiagram is the energy level diagram that corresponds
 * to a hydrogen atom based on the deBroglie model. it is identical 
 * to the diagram for the Bohr model.
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
    public DeBroglieEnergyDiagram( PSwingCanvas canvas, IClock clock ) {
        super( canvas, clock );
    }
}
