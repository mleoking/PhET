/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 4, 2003
 * Time: 2:11:28 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.controller.command.Command;

public class AddParticlesToHollowSphereCmd implements Command {

    private PumpMoleculeCmd pumpMoleculeCmd;
    private HollowSphere sphere;
    private int numParticles;
    private IdealGasApplication application;

    /**
     *
     * @param application
     * @param sphere
     * @param numParticles
     */
    public AddParticlesToHollowSphereCmd( IdealGasApplication application,
                                          HollowSphere sphere,
                                          int numParticles ) {
        pumpMoleculeCmd = new PumpMoleculeCmd( application );
        this.application = application;
        this.sphere = sphere;
        this.numParticles = numParticles;
    }

    /**
     *
     * @return
     */
    public Object doIt() {

        for( int i = 0; i < numParticles; i++ ) {

            // Pump a new gas molecule into the system
            GasMolecule newMolecule = (GasMolecule)pumpMoleculeCmd.doIt();

            newMolecule.setPosition( sphere.getPosition().getX(), sphere.getPosition().getY() );

            // Put the new molecule in the hollow sphere
            Constraint constraintSpec = new HollowSphereMustContainParticle( sphere, newMolecule );
            newMolecule.addConstraint( constraintSpec );
            sphere.addContainedBody( newMolecule );
            IdealGasSystem idealGasSystem = application.getIdealGasSystem();
            constraintSpec = new BoxMustContainParticle( idealGasSystem.getBox(), newMolecule );
            newMolecule.addConstraint( constraintSpec );
        }
        return null;
    }
}
