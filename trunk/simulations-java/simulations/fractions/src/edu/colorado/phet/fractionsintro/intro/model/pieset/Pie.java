// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.F;
import fj.data.List;
import lombok.Data;

/**
 * Immutable state representing an empty pie which can be filled up by the user.
 *
 * @author Sam Reid
 */
@Data public class Pie {
    public final List<Slice> cells;

    public Pie translate( final double dx, final double dy ) {
        return new Pie( cells.map( new F<Slice, Slice>() {
            @Override public Slice f( Slice slice ) {
                return slice.translate( dx, dy );
            }
        } ) );
    }
}