package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;


public class QuantityControlNode extends IntegerSpinnerNode {
    
    public QuantityControlNode( IntegerRange range, PNode imageNode, double imageScale ) {
        super( range );
        PBounds fullBounds = getFullBoundsReference(); // bounds before adding image!
        addChild( imageNode );
        imageNode.scale( imageScale );
        double x = fullBounds.getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        double y = fullBounds.getMaxY() + 5;
        imageNode.setOffset( x, y );
    }

}
