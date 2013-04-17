// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage;

import edu.colorado.phet.batteryvoltage.common.electron.man.Motion;

public interface Action {
    public Action act();

    public Motion getMotion();
}
