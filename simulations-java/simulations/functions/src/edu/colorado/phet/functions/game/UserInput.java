// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class UserInput {
    public final Option<Vector2D> pressLocation;
}