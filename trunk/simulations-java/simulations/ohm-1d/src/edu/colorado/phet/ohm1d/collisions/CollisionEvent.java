package edu.colorado.phet.ohm1d.collisions;

import edu.colorado.phet.ohm1d.Electron;
import edu.colorado.phet.ohm1d.oscillator2d.Core;

public interface CollisionEvent {
    public void collide( Core c, Electron e );
}
