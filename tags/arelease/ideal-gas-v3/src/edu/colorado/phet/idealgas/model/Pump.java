/**
 * Class: Pump
 * Package: edu.colorado.phet.idealgas.physics
 * Author: Another Guy
 * Date: Jun 25, 2004
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.idealgas.controller.AddModelElementCmd;
import edu.colorado.phet.common.util.SimpleObservable;

public class Pump extends SimpleObservable {
    private IdealGasModel model;
    private GasMoleculeFactory gasFactory = new GasMoleculeFactory();

    public Pump( IdealGasModel model ) {
        this.model = model;
    }

    public void pump( int numMolecules ) {
        for( int i = 0; i < numMolecules; i++ ) {
            this.pumpGasMolecule();
        }
    }

    /**
     * Creates a gas molecule of the proper species
     */
    protected GasMolecule pumpGasMolecule() {

        // Add a new gas molecule to the system
        GasMolecule newMolecule = gasFactory.create( model,
                                                       model.getAverageMoleculeEnergy() );
//        PumpMoleculeCmd pumpCmd = new PumpMoleculeCmd( (IdealGasApplication)IdealGasApplication.instance(),
//                                                       model.getAverageMoleculeEnergy() );
//        GasMolecule newMolecule = (GasMolecule)pumpCmd.doIt();
        new AddModelElementCmd( model, newMolecule ).doIt();

        // Constrain the molecule to be inside the box
        Box2D box = model.getBox();
        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule, model );
        newMolecule.addConstraint( constraintSpec );
        return newMolecule;
    }

}