/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 4, 2003
 * Time: 2:11:28 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.controller.command.Command;

import java.util.List;

public class RemoveParticlesFromHollowSphereCmd implements Command {

    private IdealGasApplication application;
    private HollowSphere sphere;
    private int numParticles;

    public RemoveParticlesFromHollowSphereCmd( IdealGasApplication application,
                                          HollowSphere sphere,
                                          int numParticles ) {
        this.application = application;
        this.sphere = sphere;
        this.numParticles = numParticles;
    }

    public Object doIt() {

        for( int i = 0; i < numParticles; i++ ) {
            List molecules = sphere.getContainedBodies();
            int numMolecules = molecules.size();
            GasMolecule molecule = null;
            if( numMolecules > 0 ) {
                molecule = (GasMolecule)molecules.get( numMolecules - 1 );
                sphere.removeContainedBody( molecule );
                RemoveParticleCommand removeCmd = new RemoveParticleCommand( molecule );
                removeCmd.doIt();
            }
        }
        return null;
    }
}
