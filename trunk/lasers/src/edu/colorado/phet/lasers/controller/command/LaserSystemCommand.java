/**
 * Class: LaserSystemCommand
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Apr 17, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.lasers.physics.LaserSystem;

abstract public class LaserSystemCommand implements Command {

    protected LaserSystem getLaserSystem() {
        return (LaserSystem)(PhetApplication.instance().getPhysicalSystem() );
    }
}
