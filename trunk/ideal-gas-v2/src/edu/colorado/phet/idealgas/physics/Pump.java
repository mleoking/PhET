/**
 * Class: Pump
 * Package: edu.colorado.phet.idealgas.physics
 * Author: Another Guy
 * Date: Jun 25, 2004
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.common.util.SimpleObservable;

import java.util.Observable;

public class Pump extends SimpleObservable {
//public class Pump extends Observable {
    private IdealGasSystem model;

    public Pump( IdealGasSystem model ) {
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
        PumpMoleculeCmd pumpCmd = new PumpMoleculeCmd( model, model.getAverageMoleculeEnergy() );
        GasMolecule newMolecule = (GasMolecule)pumpCmd.doIt();

        // Constrain the molecule to be inside the box
        Box2D box = model.getBox();
        Constraint constraintSpec = new BoxMustContainParticle( box, newMolecule );
        newMolecule.addConstraint( constraintSpec );
        return newMolecule;
    }

}