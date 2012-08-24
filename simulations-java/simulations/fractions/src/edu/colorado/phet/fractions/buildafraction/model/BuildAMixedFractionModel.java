// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.F;
import fj.Unit;

import java.util.ArrayList;

import edu.colorado.phet.fractions.buildafraction.model.numbers.MixedNumbersNumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.MixedNumbersShapeLevelList;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;

/**
 * @author Sam Reid
 */
public class BuildAMixedFractionModel extends BuildAFractionModel {
    public BuildAMixedFractionModel() {
        super( new F<Unit, ArrayList<ShapeLevel>>() {
                   @Override public ArrayList<ShapeLevel> f( final Unit unit ) {
                       return new MixedNumbersShapeLevelList();
                   }
               }, new F<Unit, ArrayList<NumberLevel>>() {
                   @Override public ArrayList<NumberLevel> f( final Unit unit ) {
                       return new MixedNumbersNumberLevelList();
                   }
               }
        );
    }

    @Override public boolean isMixedNumbers() { return true; }
}