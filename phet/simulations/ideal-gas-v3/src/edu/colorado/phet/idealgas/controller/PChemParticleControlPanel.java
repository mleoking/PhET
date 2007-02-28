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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.IdealGasModel;

/**
 * PumpControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PChemParticleControlPanel extends SpeciesSelectionPanel implements Pump.Listener {

    public PChemParticleControlPanel( IdealGasModule module, GasSource gasSource,
                                      String labelA, String labelB ) {
        super( module );

        // Hook the spinner up so it will track molecules put in the box by the pump
        ((IdealGasModel)getModule().getModel()).addObserver( new SimpleObserver() {
            public void update() {
                int h = getModule().getIdealGasModel().getHeavySpeciesCnt();
                getHeavySpinner().setValue( new Integer( h ) );
            }
        } );

        // Hook the spinner up so it will track molecules put in the box by the pump
        ((IdealGasModel)getModule().getModel()).addObserver( new SimpleObserver() {
            public void update() {
                int h = getModule().getIdealGasModel().getLightSpeciesCnt();
                getLightSpinner().setValue( new Integer( h ) );
            }
        } );

        setHeavySpeciesLabelText(labelA  );
        setLightSpeciesLabelText( labelB  );
//        setHeavySpeciesLabelText(SimStrings.get( "AdvancedModule.Particle_Type_A" ) );
//        setLightSpeciesLabelText( SimStrings.get( "AdvancedModule.Particle_Type_B" ) );
        setHeavySpeciesLabelColor( AdvancedModule.COLOR_A );
        setLightSpeciesLabelColor( AdvancedModule.COLOR_B );
        repaint();
    }

    protected void createMolecule( Class moleculeClass ) {
        getModule().pumpGasMolecules( 1, moleculeClass );
    }

    protected void removeMolecule( Class moleculeClass ) {
        getModule().removeGasMolecule( moleculeClass );
    }

    protected int getHeavySpeciesCnt() {
        return getModule().getIdealGasModel().getHeavySpeciesCnt();
    }

    protected int getLightSpeciesCnt() {
        return getModule().getIdealGasModel().getLightSpeciesCnt();
    }

    //--------------------------------------------------------------
    // Event handling
    //--------------------------------------------------------------
    public void moleculesAdded( Pump.MoleculeEvent event ) {
        Class species = event.getSpecies();
        if( HeavySpecies.class.isAssignableFrom( species ) ) {
            int cnt = ( (Integer)getHeavySpinner().getValue() ).intValue();
            getHeavySpinner().setValue( new Integer( cnt + event.getNumMolecules() ) );
        }
    }

    public void moleculeAdded( GasMolecule molecule ) {
        // noop
    }
}
