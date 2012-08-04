// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionNumberNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a mixed number, such as one and one half = 1 1/2.  Used in the 2nd tab of the Fraction Matcher application only.
 *
 * @author Sam Reid
 */
class MixedNumberNode extends PNode {
    public MixedNumberNode( final Fraction fraction, final int scaleFactor, final double fractionSizeScale, final double mixedNumberWholeScale ) {
        final Property<Integer> one = new Property<Integer>( 1 );
        final Fraction scaledFraction = new Fraction( ( fraction.numerator - fraction.denominator ) * scaleFactor, fraction.denominator * scaleFactor );
        addChild( new HBox( 0,
                            new FractionNumberNode( one ) {{
                                setScale( fractionSizeScale * mixedNumberWholeScale );
                            }},
                            new FractionNode( scaledFraction, fractionSizeScale ) ) );
    }
}