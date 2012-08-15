// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class Key {
    public final int numRotations;

    public Key rotateRight() { return new Key( ( numRotations + 1 ) % 4 ); }
}