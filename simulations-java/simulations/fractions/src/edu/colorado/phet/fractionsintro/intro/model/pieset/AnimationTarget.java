// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import lombok.Data;

import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * Place where a slice is animating to (a cell or back to the bucket).
 *
 * @author Sam Reid
 */
@Data public class AnimationTarget {
    public final Vector2D position;
    public final double angle;

    public static AnimationTarget animateToSlice( Slice slice ) {
        return new AnimationTarget( slice.position, slice.angle );
    }
}