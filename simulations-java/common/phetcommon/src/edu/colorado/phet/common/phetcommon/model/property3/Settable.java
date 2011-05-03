// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

/**
 * An instance that may have its value set.
 *
 * @author Sam Reid
 */
public interface Settable<T> {
    void set( T value );
}
