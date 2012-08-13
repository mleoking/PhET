// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import fj.F;
import fj.Unit;

import java.util.ArrayList;

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
 * TODO: This tab is not done yet--it is scheduled for work after Fractions Intro is published.
 *
 * @author Sam Reid
 */
public class BuildAMixedFractionModule extends AbstractFractionsModule {
    public BuildAMixedFractionModule( BuildAFractionModel model ) {
        super( Components.buildAFractionTab, "Mixed Fractions", model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model ) );
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