/**
 * Class: SetClockDtCmd
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:52:18 AM
 */
package edu.colorado.phet.controller.command;

import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.controller.PhetApplication;

/**
 * This command sets the size of the time step used by the PhysicalSystem's
 * clock
 */
public class SetClockDtCmd implements Command {

    private float dt;

    public SetClockDtCmd( float dt ) {
        this.dt = dt;
    }

    public Object doIt() {
        PhysicalSystem physicalSystem = PhetApplication.instance().getPhysicalSystem();
        int waitTime = (int)physicalSystem.getWaitTime();
        physicalSystem.setClockParams( dt, waitTime );
        return null;
    }
}
