/**
 * Class: SetStimulatingBeamCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;

public class SetStimulatingBeamCmd extends LaserApplicationCmd {

    CollimatedBeam incomingBeam;

    public SetStimulatingBeamCmd( CollimatedBeam beam ) {
        incomingBeam = beam;
    }

    public Object doIt() {
        getLaserSystem().setStimulatingBeam( incomingBeam );
        return null;
    }
}
