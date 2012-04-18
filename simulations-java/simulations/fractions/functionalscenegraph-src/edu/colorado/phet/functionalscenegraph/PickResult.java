package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class PickResult {
    public final SNode node;
    public final Effect<Vector2D> dragHandler;
}