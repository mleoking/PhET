// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.intro.view.PieSetFractionNode;

/**
 * @author Sam Reid
 */
public class PieNode extends RepresentationNode {
    public PieNode( ModelViewTransform transform, final Fraction fraction ) {
        super( transform, fraction );

        PieSetFractionNode pieSetFractionNode = new PieSetFractionNode( new Property<Integer>( fraction.numerator ), new Property<Integer>( fraction.denominator ), new Property<Boolean>( true ) );
        addChild( new ZeroOffsetNode( pieSetFractionNode ) );

        scale( 0.5 );
    }
}