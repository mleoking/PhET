package edu.colorado.phet.common.batteryvoltage.electron.man.motions;

import edu.colorado.phet.common.batteryvoltage.electron.man.Man;
import edu.colorado.phet.common.batteryvoltage.electron.man.Motion;

public class StandStill implements Motion {
    public boolean update( double dt, Man m ) {
        return true;
    }
}
