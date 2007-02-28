package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.common.batteryvoltage.electron.man.Motion;

public interface Action {
    public Action act();

    public Motion getMotion();
}
