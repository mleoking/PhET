package edu.colorado.phet.functions.buildafunction;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FunctionBoxWithText extends PNode {
    public FunctionBoxWithText( final String s ) {
        final FunctionBox functionBox = new FunctionBox();
        addChild( functionBox );
        addChild( new PhetPText( s, new PhetFont( 32, true ) ) {{
            centerFullBoundsOnPoint( functionBox.getFullBounds().getCenterX(), functionBox.getFullBounds().getCenterY() );
        }} );
    }
}