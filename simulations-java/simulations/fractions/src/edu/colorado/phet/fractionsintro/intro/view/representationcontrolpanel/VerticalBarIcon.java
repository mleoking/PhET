// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.BasicStroke;
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
            final double scale = 44 / getFullBounds().getWidth();
            scale( scale );
            setPaint( color );

            //Show the border a bit thicker to match the filled shapes
            path.setStroke( new BasicStroke( (float) ( 2 / scale ) ) );
            path.setStrokePaint( Color.black );
        }};

    }

    public PNode getNode() {
        return node;
    }

    public Representation getRepresentation() {
        return Representation.VERTICAL_BAR;
    }
}