// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * Piccolo animation that updates the scale.
 *
 * @author Sam Reid
 */
public class AnimateToScale extends PInterpolatingActivity {
    private final PNode node;
    private final LinearFunction linearFunction;

    public AnimateToScale( double toScale, PNode node, long duration ) {
        this( node, new LinearFunction( 0, 1, node.getScale(), toScale ), duration );
    }

    private AnimateToScale( final PNode node, final LinearFunction linearFunction, long duration ) {
        super( duration );
        this.node = node;
        this.linearFunction = linearFunction;
    }

    @Override public void setRelativeTargetValue( final float zeroToOne ) {
        node.setScale( linearFunction.evaluate( zeroToOne ) );
    }
}