// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

/**
 * This interface is necessary because it was too unwieldy to have to specify additional type signatures like <U extends Gettable<T>, Observable0> and may be technically infeasible.
 *
 * @author Sam Reid
 */
public interface GettableObservable0<T> extends Gettable<T>, Observable0 {
}
