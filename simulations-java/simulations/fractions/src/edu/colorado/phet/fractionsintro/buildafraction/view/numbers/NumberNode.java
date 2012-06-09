package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;

/**
 * Node for a number that gets dragged out of the toolbox.
 *
 * @author Sam Reid
 */
public class NumberNode extends RichPNode {
    public final int number;

    public NumberNode( final int number ) {
        this.number = number;
        addChild( new PhetPText( number + "", new PhetFont( 64, true ) ) );
    }
}