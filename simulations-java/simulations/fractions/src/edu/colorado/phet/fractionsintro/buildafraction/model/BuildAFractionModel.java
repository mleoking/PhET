package edu.colorado.phet.fractionsintro.buildafraction.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for the "Build a Fraction" tab which contains a switchable immutable state.
 *
 * @author Sam Reid
 */
public class BuildAFractionModel {
    public final Property<BuildAFractionState> state = new Property<BuildAFractionState>( new BuildAFractionState() );
    public final ConstantDtClock clock = new ConstantDtClock();

    public BuildAFractionModel() {
    }
}