package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.electron.man.Motion;

public interface Action {
    public Action act();

    public Motion getMotion();
}
