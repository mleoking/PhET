package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.Law;
import edu.colorado.phet.common.phys2d.System2D;

public class StateLaw implements Law {
    GoToFinger gtk;
    //TravoltaScene ts;
    Law current;

    public StateLaw() {
    }

    public void iterate( double dt, System2D sys ) {
//        edu.colorado.phet.common.util.Debug.traceln();
        current.iterate( dt, sys );
    }
}

