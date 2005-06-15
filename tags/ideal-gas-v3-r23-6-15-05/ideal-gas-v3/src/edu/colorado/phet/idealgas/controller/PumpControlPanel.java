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

import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.idealgas.model.Pump;

/**
 * PumpControlPanel
 * <p>
 * A JPanel with spinners that make gas molecules come into the box from the pump.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PumpControlPanel extends SpeciesSelectionPanel implements Pump.Listener {
    public PumpControlPanel( IdealGasModule module, GasSource gasSource, String[] speciesNames ) {
        super( module, gasSource, speciesNames );
        module.getPump().addListener( this );
    }

    protected void createMolecule( Class moleculeClass ) {
        getModule().pumpGasMolecules( 1, moleculeClass );
    }

    protected void removeMolecule( Class moleculeClass ) {
        getModule().removeGasMolecule( moleculeClass );
    }

    protected int getHeavySpeciesCnt() {
        return getModule().getHeavySpeciesCnt();
    }

    protected int getLightSpeciesCnt() {
        return getModule().getLightSpeciesCnt();
    }

    //--------------------------------------------------------------
    // Pump.Listener implementation
    //--------------------------------------------------------------

    public void moleculesAdded( Pump.MoleculeEvent event ) {
        Class species = event.getSpecies();
        if( HeavySpecies.class.isAssignableFrom( species ) ) {
            getHeavySpinner().setValue( new Integer( getModule().getHeavySpeciesCnt() ) );
        }
        if( LightSpecies.class.isAssignableFrom( species ) ) {
            getLightSpinner().setValue( new Integer( getModule().getLightSpeciesCnt() ) );
        }
    }
}
