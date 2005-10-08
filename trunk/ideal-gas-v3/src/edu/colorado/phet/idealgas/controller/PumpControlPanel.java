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

import edu.colorado.phet.idealgas.model.*;

import javax.swing.*;

/**
 * PumpControlPanel
 * <p>
 * A JPanel with spinners that make gas molecules come into the box from the pump.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PumpControlPanel extends SpeciesSelectionPanel implements Pump.Listener {
    private PressureSensingBox box;

    public PumpControlPanel( IdealGasModule module, GasSource gasSource, String[] speciesNames ) {
        super( module, gasSource, speciesNames );
        module.getPump().addListener( this );
        this.box = module.getBox();
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

    public void moleculeAdded( GasMolecule molecule ) {
        molecule.addObserver( new MoleculeRemover( molecule ) );
    }

    public void removedFromSystem() {

    }

    public void update() {
        // noop
    }

    private class MoleculeRemover implements GasMolecule.Observer {
        GasMolecule molecule;
        boolean isInBox = true;
        private JSpinner spinner;

        MoleculeRemover( GasMolecule molecule ) {
            this.molecule = molecule;
            if( HeavySpecies.class.isAssignableFrom( molecule.getClass() ) ) {
                this.spinner = getHeavySpinner();
            }
            if( LightSpecies.class.isAssignableFrom( molecule.getClass() ) ) {
                this.spinner = getLightSpinner();
            }
        }
        public void removedFromSystem() {
            // noop
        }

        public void update() {
            if( box.isOutsideBox( molecule ) && isInBox ) {
                isInBox = false;
                int oldCnt = ((Integer)spinner.getValue()).intValue();
                spinner.setEnabled( false );
                spinner.setValue( new Integer( --oldCnt) );
                spinner.setEnabled( true );
            }
            if( !box.isOutsideBox( molecule ) && !isInBox ) {
                isInBox = true;
                int oldCnt = ((Integer)spinner.getValue()).intValue();
                spinner.setEnabled( false );
                spinner.setValue( new Integer( ++oldCnt) );
                spinner.setEnabled( true );
            }
        }
    }
}
