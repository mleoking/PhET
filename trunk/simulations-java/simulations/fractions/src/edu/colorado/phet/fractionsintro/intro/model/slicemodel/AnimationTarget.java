// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
@Data public class AnimationTarget {
    public final ImmutableVector2D position;
    public final double angle;
}
