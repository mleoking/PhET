package electron.man.motions;

import electron.man.Man;
import electron.man.Motion;

public class StandStill implements Motion {
    public boolean update( double dt, Man m ) {
        return true;
    }
}
