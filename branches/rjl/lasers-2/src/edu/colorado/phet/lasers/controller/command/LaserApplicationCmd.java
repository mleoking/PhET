/**
 * Class: LaserApplicationCmd
 * Package: edu.colorado.phet.controller
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.lasers.physics.LaserSystem;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.Command;

abstract public class LaserApplicationCmd implements Command {

    protected LaserSystem getLaserSystem() {
        return (LaserSystem)(PhetApplication.instance().getPhysicalSystem());
    }
}
