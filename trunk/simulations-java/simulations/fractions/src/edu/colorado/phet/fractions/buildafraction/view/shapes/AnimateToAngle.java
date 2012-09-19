// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * Piccolo animation that rotates a piece to the specified angle.  When the user drags a pie piece it will rotate to align with the closest open site.
 *
 * @author Sam Reid
 */
class AnimateToAngle extends PInterpolatingActivity {
    private final PiePieceNode node;
    private final double finalAngle;
    private final double initialAngle;

    public AnimateToAngle( final PiePieceNode node, long duration, double finalAngle ) {
        super( duration );
        this.node = node;
        this.finalAngle = finalAngle;
        this.initialAngle = node.pieceRotation;
    }

    @Override public void setRelativeTargetValue( final float zeroToOne ) {
        node.setPieceRotation( new LinearFunction( 0, 1, initialAngle, finalAngle ).evaluate( zeroToOne ) );
    }
}