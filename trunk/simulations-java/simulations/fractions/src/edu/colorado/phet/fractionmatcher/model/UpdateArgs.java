// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.model;

import lombok.Data;

/**
 * Immutable args for updating a MovableFraction.
 * This is a convenience data structure so the update function can be called with an F with named arguments instead of F3 with unnamed arguments.
 *
 * @author Sam Reid
 */
@Data public class UpdateArgs {
    public final MovableFraction fraction;
    public final double dt;
    public final MatchingGameState state;

    public UpdateArgs withFraction( MovableFraction fraction ) { return new UpdateArgs( fraction, dt, state ); }
}