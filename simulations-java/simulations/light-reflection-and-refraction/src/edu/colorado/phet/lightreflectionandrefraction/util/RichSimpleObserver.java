// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.util;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Adds convenience functionality to SimpleObserver
 *
 * @author Sam Reid
 */
public abstract class RichSimpleObserver implements SimpleObserver {
    public abstract void update();

    /**
     * Add this instance as an observer of the specified Property<T> arguments.
     *
     * @param properties the properties to observe.
     */
    public void observe( Property... properties ) {
        for ( Property property : properties ) {
            property.addObserver( this );
        }
    }
}
