/**
 * Class: SetStimulatingBeamCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;

public class SetPumpingBeamCmd extends LaserApplicationCmd {

    CollimatedBeam pumpingBeam;

    public SetPumpingBeamCmd( CollimatedBeam beam ) {
        pumpingBeam = beam;
    }

    public Object doIt() {
        getLaserSystem().setPumpingBeam( pumpingBeam );
        return null;
    }
}
