// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.fractions.buildafraction.model.numbers.MixedNumbersNumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.shapes.MixedNumbersShapeLevelList;

//REVIEW recommend renaming this to MixedNumbersModel

/**
 * Main model for the "Mixed Numbers" tab of Build a Fraction
 *
 * @author Sam Reid
 */
public class BuildAMixedFractionModel extends BuildAFractionModel {
    public BuildAMixedFractionModel( BooleanProperty collectedMatch ) {
        super( collectedMatch, new MixedNumbersShapeLevelList(), new MixedNumbersNumberLevelList() );
    }

    @Override public boolean isMixedNumbers() { return true; }
}