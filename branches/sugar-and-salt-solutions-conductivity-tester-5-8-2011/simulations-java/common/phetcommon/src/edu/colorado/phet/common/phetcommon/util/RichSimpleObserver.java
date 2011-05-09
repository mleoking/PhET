// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.util;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Adds convenience functionality to SimpleObserver.  Right now it just adds one additional function, but it may add more later.
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

    /**
     * Remove this instance as an observer of the specified Property<T> arguments.
     *
     * @param properties the properties to stop observing.
     */
    public void unobserve( Property... properties ) {
        for ( Property property : properties ) {
            property.removeObserver( this );
        }
    }
}
