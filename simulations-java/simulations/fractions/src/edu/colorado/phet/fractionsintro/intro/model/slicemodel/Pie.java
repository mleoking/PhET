// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import fj.data.List;

/**
 * Immutable state representing an empty pie which can be filled up by the user.
 *
 * @author Sam Reid
 */
@lombok.Data public class Pie {
    public final List<Slice> cells;
    public int size;
}