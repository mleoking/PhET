// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.PieSetFractionNode;

/**
 * @author Sam Reid
 */
public class PieNode extends RepresentationNode {
    public PieNode( ModelViewTransform transform, final Fraction fraction, Property<ContainerSet> containerState ) {
        super( transform, fraction );

        PieSetFractionNode pieSetFractionNode = new PieSetFractionNode( containerState, new Property<Boolean>( true ) );
        addChild( new ZeroOffsetNode( pieSetFractionNode ) );

        scale( 0.5 );
    }
}