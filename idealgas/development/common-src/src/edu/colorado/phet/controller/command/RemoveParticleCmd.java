/**
 * Class: RemoveParticleCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.controller.command;

import edu.colorado.phet.controller.command.Command;
public class RemoveParticleCmd implements Command {

    private edu.colorado.phet.physics.body.Particle particle;

    public RemoveParticleCmd( edu.colorado.phet.physics.body.Particle particle ) {
        this.particle = particle;
    }

    public Object doIt() {
        particle.removeFromSystem();
        return null;
    }
}
