/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.model.Pump;

/**
 * PumpControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PumpControlPanel extends SpeciesSelectionPanel {
    public PumpControlPanel( IdealGasModule module, GasSource gasSource ) {
        super( module, gasSource );
    }

    protected void createMolecule( Class moleculeClass ) {
        Pump pump = getModule().getPump();
        pump.setCurrentGasSpecies( moleculeClass );
        getModule().pumpGasMolecules( 1 );
    }

    protected void removeMolecule( Class moleculeClass ) {
        getModule().removeGasMolecule( moleculeClass );
    }
}
