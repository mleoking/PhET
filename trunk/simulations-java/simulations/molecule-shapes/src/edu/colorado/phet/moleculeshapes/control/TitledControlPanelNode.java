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
 * TODO: match other constructors?
 * TODO: title fonts?
 */
public class TitledControlPanelNode extends ControlPanelNode {

    public TitledControlPanelNode( final PNode content, final String title, final Color backgroundColor, final BasicStroke borderStroke, final Color borderColor ) {
        this( content, new PText( title ) {{
                  setFont( new PhetFont( 14, true ) );
                  setTextPaint( borderColor );
              }}, backgroundColor, borderStroke, borderColor );
    }

    public TitledControlPanelNode( final PNode content, final PNode titleNode, final Color backgroundColor, final BasicStroke borderStroke, final Color borderColor ) {
        super( content, backgroundColor, borderStroke, borderColor );

        final ControlPanelNode controlPanelNode = this;

        // title
        background.addChild( 0, new PNode() {{
            // background to block out border
            addChild( new PhetPPath( padBoundsHorizontally( titleNode.getFullBounds(), 10 ), backgroundColor ) );
            addChild( titleNode );
            setOffset( ( controlPanelNode.getFullBounds().getWidth() - titleNode.getFullBounds().getWidth() ) / 2,
                       -titleNode.getFullBounds().getHeight() / 2 );
        }} );
    }

    private static PBounds padBoundsHorizontally( PBounds bounds, double amount ) {
        return new PBounds( bounds.x - amount, bounds.y, bounds.width + 2 * amount, bounds.height );
    }
}
