// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class Pair<T, U> implements Serializable {
    public final T _1;
    public final U _2;

    public Pair( T _1, U _2 ) {
        this._1 = _1;
        this._2 = _2;
    }

    @Override public String toString() {
        return "Pair(" + _1 + "," + _2 + ")";
    }
}
