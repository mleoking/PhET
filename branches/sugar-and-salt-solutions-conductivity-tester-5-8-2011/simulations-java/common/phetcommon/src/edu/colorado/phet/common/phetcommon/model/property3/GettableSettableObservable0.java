// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

/**
 * Interface that signifies a value can be set, get and observed for changes (callback provides no value), used in the controls package.
 *
 * @author Sam Reid
 */
public interface GettableSettableObservable0<T> extends GettableObservable0<T>, Settable<T> {
}
