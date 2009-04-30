package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.model.Gene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class MutationPendingNode extends PNode {
    private PText label;

    public MutationPendingNode( Gene gene ) {
        label = new PText( "mutation pending" );
        label.setFont( new PhetFont( 16, true ) );
        addChild( label );
    }

    public double getPlacementWidth() {
        return label.getWidth();
    }

    public double getPlacementHeight() {
        return label.getHeight();
    }
}
