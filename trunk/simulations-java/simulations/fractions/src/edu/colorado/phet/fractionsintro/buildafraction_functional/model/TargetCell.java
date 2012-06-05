package edu.colorado.phet.fractionsintro.buildafraction_functional.model;

import fj.data.Option;
import lombok.Data;

/**
 * Location where a filled in fraction can be dragged.
 *
 * @author Sam Reid
 */
public @Data class TargetCell {
    public final int index;
    public final Option<FractionID> fraction;

    public TargetCell withFraction( final FractionID id ) { return new TargetCell( index, Option.some( id ) ); }
}