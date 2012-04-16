// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.Color;

import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.SliceFactory;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.colorado.phet.fractionsintro.intro.view.pieset.ShapeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the icon on the representation control panel for the horizontal bars.
 *
 * @author Sam Reid
 */
public class VerticalBarIcon implements RepresentationIcon {
    private final PNode node;

    public VerticalBarIcon( final SliceFactory sliceFactory, final Color color ) {
        node = new ShapeNode( sliceFactory.createBucketSlice( 1, 0L ) ) {{
            scale( 44 / getFullBounds().getWidth() );
            setPaint( color );
        }};
    }

    @Override public PNode getNode() {
        return node;
    }

    @Override public Representation getRepresentation() {
        return Representation.VERTICAL_BAR;
    }
}