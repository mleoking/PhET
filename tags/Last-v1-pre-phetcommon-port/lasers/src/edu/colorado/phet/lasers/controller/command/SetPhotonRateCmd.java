/**
 * Class: SetPhotonRateCmd
 * Package: edu.colorado.phet.lasers.controller.command
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.lasers.physics.photon.CollimatedBeam;

public class SetPhotonRateCmd extends LaserApplicationCmd {

    private int rate;

    public SetPhotonRateCmd( int rate ) {
        this.rate = rate;
    }

    public Object doIt() {
        CollimatedBeam beam = getLaserSystem().getStimulatingBeam();
        beam.setPhotonsPerSecond( (float)rate );
        return null;
    }
}
