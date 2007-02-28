package edu.colorado.phet.electron.man.motions;

import edu.colorado.phet.electron.man.Man;
import edu.colorado.phet.electron.man.Motion;

public class LegSwings implements Motion {
    double omega;
    double ang;

    public LegSwings( double omega ) {
        this.omega = omega;
        this.ang = 0;
    }

    public boolean update( double dt, Man m ) {
        double dTheta = omega * dt;
        m.getLeftHip().rotate( omega * dt );
        ang += dTheta;
        if( ang > Math.PI / 2 * .8 || ang < -Math.PI / 2 * .4 ) {
            omega = -omega;
        }
        return true;
    }
}
