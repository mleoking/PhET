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

import edu.colorado.phet.idealgas.model.HollowSphere;


/**
 * PumpControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HollowSphereControlPanel extends SpeciesSelectionPanel implements HollowSphere.HollowSphereListener {
    private HollowSphere sphere;

    public HollowSphereControlPanel( IdealGasModule module, GasSource gasSource, HollowSphere sphere ) {
        super( module, gasSource );
//        sphere.addHollowSphereListener( this );
        this.sphere = sphere;
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


    protected int getHeavySpeciesCnt() {
        return sphere.getHeavySpeciesCnt();
    }

    protected int getLightSpeciesCnt() {
        return sphere.getLightSpeciesCnt();
    }


    public void moleculeAdded( HollowSphere.MoleculeEvent event ) {
//        Class species = event.getMoleculeType();
//        if( HeavySpecies.class.isAssignableFrom( species ) ) {
//            int oldCnt = ((Integer)getHeavySpinner().getValue()).intValue();
//            getHeavySpinner().setValue( new Integer( oldCnt + 1 ));
//        }
//
    }

    public void moleculeRemoved( HollowSphere.MoleculeEvent event ) {

    }
}
