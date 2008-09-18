package edu.colorado.phet.batteryvoltage;

import java.awt.*;
import java.awt.event.MouseEvent;

import edu.colorado.phet.batteryvoltage.common.electron.gui.mouse2.ParticleGrabber;
import edu.colorado.phet.batteryvoltage.common.electron.gui.mouse2.ParticleSelector;
import edu.colorado.phet.batteryvoltage.common.phys2d.PropagatingParticle;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;
import edu.colorado.phet.batteryvoltage.man.Director;

public class VoltParticleGrabber extends ParticleGrabber {
    Propagator dropRight;
    Propagator dropLeft;
    double threshold;
    Battery b;
    Director d;

    public VoltParticleGrabber( Component lp, ParticleSelector ps, Propagator grab, Propagator dropRight, Propagator dropLeft, double threshold, Battery b, Director d ) {
        super( lp, ps, grab );
        this.b = b;
        this.d = d;
        this.threshold = threshold;
        this.dropRight = dropRight;
        this.dropLeft = dropLeft;
    }

    public void mouseReleased( MouseEvent me ) {
        PropagatingParticle sel = (PropagatingParticle) getSelected();
        if ( sel == null ) {
            return;
        }
        if ( sel.getPosition().getX() > threshold ) {
            d.putTag( sel, true );
            sel.setPropagator( dropRight );
        }
        else {
            d.putTag( sel, false );
            sel.setPropagator( dropLeft );
        }
        b.fireParticleMoved( sel );
    }
}
