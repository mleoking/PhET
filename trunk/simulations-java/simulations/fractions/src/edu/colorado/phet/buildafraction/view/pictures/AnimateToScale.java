package edu.colorado.phet.buildafraction.view.pictures;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * @author Sam Reid
 */
public class AnimateToScale extends PInterpolatingActivity {
    private final PNode node;
    private final LinearFunction linearFunction;
    private final long duration;

    public AnimateToScale( PNode node, final double scale, long duration ) {
        this( node, new LinearFunction( 0, 1, node.getScale(), 1.00 ), duration );
    }

    public AnimateToScale( final PNode node, final LinearFunction linearFunction, long duration ) {
        super( duration );
        this.node = node;
        this.linearFunction = linearFunction;
        this.duration = duration;
    }

    @Override public void setRelativeTargetValue( final float zeroToOne ) {
        node.setScale( linearFunction.evaluate( zeroToOne ) );
    }
}