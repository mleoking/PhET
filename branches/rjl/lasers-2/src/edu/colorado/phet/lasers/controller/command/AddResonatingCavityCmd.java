/**
 * Class: AddResonatingCavityCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.common.application.PhetApplication;

public class AddResonatingCavityCmd extends LaserApplicationCmd {

    private ResonatingCavity cavity;

    public AddResonatingCavityCmd( ResonatingCavity chamber ) {
        this.cavity = chamber;
    }

    public Object doIt() {
        getLaserSystem().setResonatingCavity( cavity );
        getLaserSystem().addBody( cavity );
        PhetApplication.instance().getPhetMainPanel().addBody( cavity );
        return null;
    }
}
