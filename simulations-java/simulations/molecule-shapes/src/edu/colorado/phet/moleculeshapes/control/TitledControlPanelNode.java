// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A control panel node with a specific title that is moved over the top border
 */
public class TitledControlPanelNode extends ControlPanelNode {
    public TitledControlPanelNode( final PNode content, final String title, final Color backgroundColor, final BasicStroke borderStroke, final Color borderColor ) {
        super( content, backgroundColor, borderStroke, borderColor );

        final ControlPanelNode controlPanelNode = this;

        // title
        background.addChild( 0, new PNode() {{
            PText text = new PText( title ) {{
                setFont( new PhetFont( 14, true ) );
                setTextPaint( borderColor );
            }};

            // background to block out border
            addChild( new PhetPPath( padBoundsHorizontally( text.getFullBounds(), 10 ), backgroundColor ) );
            addChild( text );
            setOffset( ( controlPanelNode.getFullBounds().getWidth() - text.getFullBounds().getWidth() ) / 2,
                       -text.getFullBounds().getHeight() / 2 );
        }} );
    }

    private static PBounds padBoundsHorizontally( PBounds bounds, double amount ) {
        return new PBounds( bounds.x - amount, bounds.y, bounds.width + 2 * amount, bounds.height );
    }
}
