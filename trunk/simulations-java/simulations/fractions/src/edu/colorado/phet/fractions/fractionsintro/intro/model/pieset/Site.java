// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model.pieset;

import lombok.Data;

/**
 * Location in a grid of pies
 *
 * @author Sam Reid
 */
public @Data class Site {
    public final int row;
    public final int column;

    //Can't be named equals or lombok skips generating the one with Object arg
    public final boolean eq( int r, int c ) {
        return equals( new Site( r, c ) );
    }
}