// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.collisions;

import edu.colorado.phet.batteryresistorcircuit.Electron;
import edu.colorado.phet.batteryresistorcircuit.oscillator2d.Core;

public interface CollisionEvent {
    public void collide( Core c, Electron e );
}
