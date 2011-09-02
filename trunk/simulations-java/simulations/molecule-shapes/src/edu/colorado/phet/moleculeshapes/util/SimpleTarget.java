// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

public abstract class SimpleTarget implements SimpleObserver, Fireable<Object> {
    public void fire( Object param ) {
        update();
    }
}
