// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.matchinggame.view.DecimalFractionNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class DecimalFraction extends Representation {
    public final Fraction fraction;

    public DecimalFraction( Fraction fraction ) {
        this.fraction = fraction;
    }

    @Override public PNode toNode() {
        return new DecimalFractionNode( this );
    }
}