/**
 * Class: LaserSystemCommand
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Apr 17, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.application.PhetApplication;

abstract public class LaserSystemCommand implements Command {

    protected LaserSystem getLaserSystem() {
        return (LaserSystem)(PhetApplication.instance().getPhysicalSystem() );
    }
}
