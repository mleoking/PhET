/**
 * Class: PhysicalSystemCommand
 * Package: edu.colorado.phet.controller.command
 * Author: Another Guy
 * Date: Apr 17, 2003
 */
package edu.colorado.phet.controller.command;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.physics.PhysicalSystem;

/**
 * An abstract class specifically for implementing commands to the
 * physical system. It provides a mechanism for commands being executed
 * synchronously in the model thread.
 */
abstract public class PhysicalSystemCommand implements Command {

    public void doItLater() {
        PhetApplication.instance().getPhysicalSystem().addPrepCmd( this );
    }

    protected PhysicalSystem getPhysicalSystem() {
        return PhetApplication.instance().getPhysicalSystem();
    }
}
