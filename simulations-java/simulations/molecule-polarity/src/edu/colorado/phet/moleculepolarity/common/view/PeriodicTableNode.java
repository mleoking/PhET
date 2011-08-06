// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Placeholder for the periodic table node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PeriodicTableNode extends PComposite {

    public PeriodicTableNode() {

        PPath tableNode = new PPath( new Rectangle2D.Double( 0, 0, 485, 125 ) ) {{
            setPaint( Color.GRAY );
        }};
        addChild( tableNode );

        PText textNode = new PText( "Periodic Table goes here" ) {{
            setFont( new PhetFont( 18 ) );
            setTextPaint( Color.WHITE );
        }};
        addChild( textNode );

        // layout
        textNode.setOffset( tableNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBoundsReference().getWidth() / 2 ),
                            tableNode.getFullBoundsReference().getCenterY() - ( textNode.getFullBoundsReference().getHeight() / 2 ) );
    }
}
