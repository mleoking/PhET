// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.intro.view.PieSetFractionNode;
import edu.colorado.phet.fractions.intro.matchinggame.model.PieRepresentation;

/**
 * @author Sam Reid
 */
public class PieNode extends RepresentationNode {
    public PieNode( ModelViewTransform transform, final PieRepresentation representation ) {
        super( transform, representation );

        PieSetFractionNode pieSetFractionNode = new PieSetFractionNode( new Property<Integer>( representation.fraction.numerator ), new Property<Integer>( representation.fraction.denominator ), new Property<Boolean>( true ) );
        addChild( new ZeroOffsetNode( pieSetFractionNode ) );
    }
}