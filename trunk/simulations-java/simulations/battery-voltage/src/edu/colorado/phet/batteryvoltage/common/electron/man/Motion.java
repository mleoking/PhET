// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.electron.man;


public interface Motion {
    /*Return true if has more to do.*/
    public void update( double dt, Man m );
}
