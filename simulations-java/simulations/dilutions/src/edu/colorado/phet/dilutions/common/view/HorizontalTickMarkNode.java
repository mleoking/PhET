// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A horizontal line with a label to the left of the line.
 * Origin is at the right end of the line, to simplify client layout code.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HorizontalTickMarkNode extends PComposite {

    public HorizontalTickMarkNode( String label, final PhetFont labelFont, double tickLength ) {

        // nodes
        PNode tickNode = new PPath( new Line2D.Double( -tickLength, 0, 0, 0 ) );
        PNode labelNode = new PText( label ) {{
            setFont( labelFont );
        }};

        // rendering order
        addChild( tickNode );
        addChild( labelNode );

        // layout
        labelNode.setOffset( tickNode.getFullBoundsReference().getMinX() - labelNode.getFullBoundsReference().getWidth() - 3,
                             -( labelNode.getFullBoundsReference().getHeight() / 2 ) );
    }
}
