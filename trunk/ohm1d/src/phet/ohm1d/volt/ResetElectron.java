package phet.ohm1d.volt;

import phet.ohm1d.Electron;
import phet.wire1d.Propagator1d;
import phet.wire1d.WireParticle;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jan 24, 2003
 * Time: 8:44:20 PM
 * To change this template use Options | File Templates.
 */
public class ResetElectron implements Propagator1d {
    public ResetElectron() {
    }

    public void propagate( WireParticle wireParticle, double v ) {
        Electron e = (Electron)wireParticle;
        e.forgetCollision();
    }
}
