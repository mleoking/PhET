// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import lombok.Data;

/**
 * @author Sam Reid
 */
public @Data class Graphic {
    public final int numRotations;

    public Graphic rotateRight() { return new Graphic( ( numRotations + 1 ) % 4 ); }
}