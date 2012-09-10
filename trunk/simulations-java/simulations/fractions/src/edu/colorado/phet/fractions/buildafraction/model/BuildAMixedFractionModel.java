// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.fractions.buildafraction.model.numbers.MixedNumbersNumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.shapes.MixedNumbersShapeLevelList;

/**
 * @author Sam Reid
 */
public class BuildAMixedFractionModel extends BuildAFractionModel {
    public BuildAMixedFractionModel( BooleanProperty userCreatedMatch ) {
        super( userCreatedMatch, new MixedNumbersShapeLevelList(), new MixedNumbersNumberLevelList() );
    }

    @Override public boolean isMixedNumbers() { return true; }
}