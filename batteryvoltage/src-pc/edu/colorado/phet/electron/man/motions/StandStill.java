package edu.colorado.phet.electron.man.motions;

import edu.colorado.phet.electron.man.Man;
import edu.colorado.phet.electron.man.Motion;

public class StandStill implements Motion {
    public boolean update( double dt, Man m ) {
        return true;
    }
}
