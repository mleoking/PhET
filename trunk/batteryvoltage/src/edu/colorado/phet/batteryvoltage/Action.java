package edu.colorado.phet.batteryvoltage;

import electron.man.Motion;

public interface Action {
    public Action act();

    public Motion getMotion();
}
