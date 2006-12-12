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
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.LightSpecies;


/**
 * PumpControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HollowSphereControlPanel extends SpeciesSelectionPanel implements HollowSphere.HollowSphereListener {
    private HollowSphere sphere;

    public HollowSphereControlPanel( IdealGasModule module, GasSource gasSource, HollowSphere sphere ) {
        super( module );
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
        GasMolecule molecule = event.getMolecule();
        if( molecule instanceof HeavySpecies ) {
            getHeavySpinner().incrementValue();
//            int oldCnt = ( (Integer)getHeavySpinner().getValue() ).intValue();
//            boolean isEnabled = getHeavySpinner().isEnabled();
//            getHeavySpinner().setEnabled( false );
//            getHeavySpinner().setValue( new Integer( oldCnt + 1 ) );
//            getHeavySpinner().setEnabled( isEnabled );
        }
        else if( molecule instanceof LightSpecies ) {
            getLightSpinner().incrementValue();
//            int oldCnt = ( (Integer)getLightSpinner().getValue() ).intValue();
//            getLightSpinner().setValue( new Integer( oldCnt + 1 ) );
        }
    }

    public void moleculeRemoved( HollowSphere.MoleculeEvent event ) {
        GasMolecule molecule = event.getMolecule();
        if( molecule instanceof HeavySpecies ) {
            getHeavySpinner().decrementValue();
//            int oldCnt = ( (Integer)getHeavySpinner().getValue() ).intValue();
//            getHeavySpinner().setValue( new Integer( oldCnt - 1 ) );
        }
        else if( molecule instanceof LightSpecies ) {
            getLightSpinner().decrementValue();
//            int oldCnt = ( (Integer)getLightSpinner().getValue() ).intValue();
//            getLightSpinner().setValue( new Integer( oldCnt - 1 ) );
        }
    }
}
