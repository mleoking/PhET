// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.data.List;
import lombok.Data;

/**
 * Immutable state representing an empty pie which can be filled up by the user.
 *
 * @author Sam Reid
 */
@Data public class Pie {
    public final List<Slice> cells;
}