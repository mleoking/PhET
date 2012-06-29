// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.pieset;

import lombok.Data;

import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Slice;

/**
 * Immutable class for function arguments for creating a slice node.  This is an alternative to using F2 or higher.
 *
 * @author Sam Reid
 */
public @Data class SliceNodeArgs {
    public final Slice slice;
    public final int denominator;
    public final boolean inContainer;
}