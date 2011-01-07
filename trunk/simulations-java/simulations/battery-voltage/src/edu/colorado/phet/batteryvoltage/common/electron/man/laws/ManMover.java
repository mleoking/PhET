// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.electron.man.laws;

import edu.colorado.phet.batteryvoltage.common.electron.man.Man;

/*Rules for moving a single man.*/

public abstract class ManMover {
    Man man;

    public ManMover( Man target ) {
        this.man = target;
    }

    public Man getMan() {
        return man;
    }

    public abstract void move( double dt );
}
