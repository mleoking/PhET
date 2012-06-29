// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import lombok.Data;

import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class PickResult {
    public final SNode node;
    public final Effect<Vector2D> dragHandler;
}