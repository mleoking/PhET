// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property2;

import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * @author Sam Reid
 */
public class UpdateEvent<T> {
    public final T value;
    public final Option<T> oldValue;

    //Values will be the same on callback during listener registration
    public UpdateEvent( T value, Option<T> oldValue ) {
        this.value = value;
        this.oldValue = oldValue;
    }

    @Override public String toString() {
        return "value = " + value + ", oldValue = " + oldValue;
    }
}
