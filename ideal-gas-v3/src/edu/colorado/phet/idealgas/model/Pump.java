/**
 * Class: Pump
 * Package: edu.colorado.phet.idealgas.physics
 * Author: Another Guy
 * Date: Jun 25, 2004
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.idealgas.controller.AddModelElementCmd;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.application.Module;

public class Pump extends SimpleObservable {
    private IdealGasModel model;

    // The box to which the pump is attached
    private Box2D box;
    private GasMoleculeFactory gasFactory = new GasMoleculeFactory();
    private Module module;

    public Pump( Module module, Box2D box ) {
        this.module = module;
        this.model = (IdealGasModel)module.getModel();
        this.box = box;
    }

    public void pump( int numMolecules ) {
//        for( int i = 0; i < numMolecules; i++ ) {
            this.pumpGasMolecule();
//        }
        return;
    }

    /**
     * Creates a gas molecule of the proper species
     */
    protected GasMolecule pumpGasMolecule() {

        // Add a new gas molecule to the system
        GasMolecule newMolecule = gasFactory.create( model,
                                                       model.getAverageMoleculeEnergy() );
        new PumpMoleculeCmd( model, newMolecule, module ).doIt();

        // Constrain the molecule to be inside the box
        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule, model );
        newMolecule.addConstraint( constraintSpec );
        box.addContainedBody( newMolecule );     // added 9/14/04 RJL
        return newMolecule;
    }

}