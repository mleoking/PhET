package edu.colorado.phet.ohm1d.volt;

import edu.colorado.phet.ohm1d.Electron;
import edu.colorado.phet.ohm1d.common.wire1d.WireSystem;
import edu.colorado.phet.ohm1d.gui.VoltageListener;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 25, 2002
 * Time: 9:27:53 PM
 * To change this template use Options | File Templates.
 */
public class ResetScatterability implements VoltageListener {
    boolean pos = true;
    private WireSystem ws;

    public ResetScatterability( WireSystem ws ) {
        this.ws = ws;
    }

    public void doChange() {
        for ( int i = 0; i < ws.numParticles(); i++ ) {
            Electron wp = (Electron) ws.particleAt( i );
            //System.err.println(wp.getClass());
            wp.forgetCollision();
        }
    }

    public void valueChanged( double val ) {
        if ( val < 0 && pos ) {
            pos = false;
            doChange();
        }
        else if ( val > 0 && !pos ) {
            pos = true;
            doChange();
        }
    }
}
