// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import fj.data.List;

import java.util.Collection;

/**
 * Immutable state representing an empty pie which can be filled up by the user.
 *
 * @author Sam Reid
 */
public class Pie {
    public final List<Slice> cells;
    public Collection<? extends Slice> emptyCells;

    public Pie( List<Slice> cells ) {
        this.cells = cells;
    }
}
