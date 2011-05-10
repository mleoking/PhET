// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

/**
 * Interface that indicates the value may be set, used for Not
 *
 * @author Sam Reid
 */
public interface Settable<T> {
    public abstract void setValue( T value );
}
