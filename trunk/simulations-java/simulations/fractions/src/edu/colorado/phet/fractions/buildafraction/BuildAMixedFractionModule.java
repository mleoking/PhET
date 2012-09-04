// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import fj.F;
import fj.Unit;

import java.util.ArrayList;

import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.model.numbers.MixedNumbersNumberLevelList;
import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberLevel;
import edu.colorado.phet.fractions.buildafraction.model.shapes.MixedNumbersShapeLevelList;
import edu.colorado.phet.fractions.buildafraction.model.shapes.ShapeLevel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroApplication.runModule;

/**
 * Main module for "build a fraction with mixed numbers" tab, which is only visible in the standalone "Build a Fraction" sim.
 *
 * @author Sam Reid
 */
public class BuildAMixedFractionModule extends AbstractFractionsModule {
    public BuildAMixedFractionModule( BuildAFractionModel model ) {
        super( Components.buildAFractionTab, Strings.MIXED_NUMBERS, model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model, Strings.BUILD_A_MIXED_FRACTION ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) {
        runModule( args, new BuildAFractionModule( new BuildAFractionModel( new F<Unit, ArrayList<ShapeLevel>>() {
            @Override public ArrayList<ShapeLevel> f( final Unit unit ) {
                return new MixedNumbersShapeLevelList();
            }
        },
                                                                            new F<Unit, ArrayList<NumberLevel>>() {
                                                                                @Override public ArrayList<NumberLevel> f( final Unit unit ) {
                                                                                    return new MixedNumbersNumberLevelList();
                                                                                }
                                                                            }
        ) ) );
    }
}