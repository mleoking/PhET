// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionNumberNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a fraction as a mixed fraction, such as 1 2/3.  See also MixedNumberNode
 *
 * @author Sam Reid
 */
class MixedFractionNode extends PNode {
    public MixedFractionNode( final MixedFraction fraction ) {
        final double fractionSizeScale = 0.3;

        //AP: Usually mixed numbers are written with the "1" nearly as tall as the entire fraction
        final double mixedNumberWholeScale = 2.4;
        addChild( new HBox( 0,
                            new FractionNumberNode( new Property<Integer>( fraction.whole ) ) {{
                                setScale( fractionSizeScale * mixedNumberWholeScale );
                            }},
                            new FractionNode( fraction.getFractionPart(), fractionSizeScale ) ) );
    }
}