package phet.ohm1d.collisions;

import phet.ohm1d.Electron;
import phet.ohm1d.oscillator2d.Core;

public interface CollisionEvent {
    public void collide( Core c, Electron e );
}
