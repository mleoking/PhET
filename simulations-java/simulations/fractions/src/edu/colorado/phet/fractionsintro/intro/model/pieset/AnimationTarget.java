// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Place where a slice is animating to (a cell or back to the bucket).
 *
 * @author Sam Reid
 */
@Data public class AnimationTarget {
    public final Vector2D position;
    public final double angle;
}
