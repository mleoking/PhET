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



/**
 * PumpControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HollowSphereControlPanel extends SpeciesSelectionPanel {

    public HollowSphereControlPanel( IdealGasModule module, GasSource gasSource ) {
        super( module, gasSource );
    }

    private RigidHollowSphereModule getRhsModule() {
        return (RigidHollowSphereModule)getModule();
    }

    protected void createMolecule( Class moleculeClass ) {
        getRhsModule().addMoleculeToSphere( moleculeClass );
    }

    protected void removeMolecule( Class moleculeClass ) {
        getRhsModule().removeGasMoleculeFromSphere( moleculeClass );
    }
}
