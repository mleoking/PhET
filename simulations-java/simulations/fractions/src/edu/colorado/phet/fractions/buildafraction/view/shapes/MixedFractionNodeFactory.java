// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MixedFractionNodeFactory {
    public static PNode toNode( final MixedFraction target ) {
        return new FractionNode( target.toFraction(), 0.33 );
    }
}