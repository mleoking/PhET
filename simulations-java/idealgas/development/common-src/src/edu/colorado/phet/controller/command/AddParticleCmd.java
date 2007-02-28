/**
 * Class: AddAtomCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.controller.command;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.physics.body.Particle;

public class AddParticleCmd extends PhysicalSystemCommand {

    private Particle physicalEntity;

    public AddParticleCmd( Particle physicalEntity ) {
        this.physicalEntity = physicalEntity;
    }

    public Object doIt() {
        PhetApplication.instance().getPhysicalSystem().addBody( physicalEntity );
        PhetApplication.instance().getPhetMainPanel().addBody( physicalEntity );
        return null;
    }
}
