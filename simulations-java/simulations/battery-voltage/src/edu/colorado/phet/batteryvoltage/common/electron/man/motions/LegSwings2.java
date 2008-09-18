package edu.colorado.phet.batteryvoltage.common.electron.man.motions;

import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.electron.man.Motion;

public class LegSwings2 implements Motion {
    double omega;
    double ang;
    boolean started = false;

    public LegSwings2( double omega ) {
        this.omega = omega;
        this.ang = 0;
    }

    public void update( double dt, Man m ) {
        double dTheta = omega * dt;
        ang += dTheta;
        if ( Math.abs( ang ) > Math.PI / 2 * .4 ) {
            started = true;
        }
        if ( started ) {
            m.getRightHip().rotate( omega * dt );
            if ( ang > Math.PI / 2 * .4 || ang < -Math.PI / 2 * .4 ) {
                omega = -omega;
            }
        }
    }
}
