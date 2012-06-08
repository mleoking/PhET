package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for a number that gets dragged out of the toolbox.
 *
 * @author Sam Reid
 */
public class NumberNode extends PNode {
    public final int number;

    public NumberNode( final int number, final NumberDragContext context ) {
        this.number = number;
        addChild( new PhetPText( number + "", new PhetFont( 64, true ) ) );
    }
}