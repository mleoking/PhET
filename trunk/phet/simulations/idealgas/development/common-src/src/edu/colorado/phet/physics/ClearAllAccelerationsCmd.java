package edu.colorado.phet.physics;

import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.physics.body.Particle;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 7, 2003
 * Time: 3:13:37 PM
 * To change this template use Options | File Templates.
 */
public class ClearAllAccelerationsCmd implements Command {

    PhysicalSystem physicalSystem;

    /**
     *
     * @param physicalSystem
     */
    public ClearAllAccelerationsCmd( PhysicalSystem physicalSystem ) {
        this.physicalSystem = physicalSystem;
    }

    /**
     *
     */
    public Object doIt() {
        List bodies = physicalSystem.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Particle body = (Particle)bodies.get( i );
            body.clearAcceleration();
        }
        return null;
    }
}
