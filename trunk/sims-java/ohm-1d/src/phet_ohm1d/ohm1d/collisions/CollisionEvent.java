package phet_ohm1d.ohm1d.collisions;

import phet_ohm1d.ohm1d.Electron;
import phet_ohm1d.ohm1d.oscillator2d.Core;

public interface CollisionEvent {
    public void collide( Core c, Electron e );
}
