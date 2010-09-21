/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.util;

import java.util.Collection;

public interface DynamicListenerController {
    void addListener( Object listener ) throws IllegalStateException;

    void removeListener( Object listener );

    Collection getAllListeners();
}
