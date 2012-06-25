package edu.colorado.phet.functions.buildafunction;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class FunctionBoxWithText extends PNode {

    private final PhetPText textNode;

    public FunctionBoxWithText( final String s ) {
        final FunctionBox functionBox = new FunctionBox();
        addChild( functionBox );
        textNode = new PhetPText( s, new PhetFont( 32, true ) ) {{
            centerFullBoundsOnPoint( functionBox.getFullBounds().getCenterX(), functionBox.getFullBounds().getCenterY() );
        }};
        addChild( textNode );
    }

    public String getText() {
        return textNode.getText();
    }

    public abstract Object evaluate( final Object v );
}