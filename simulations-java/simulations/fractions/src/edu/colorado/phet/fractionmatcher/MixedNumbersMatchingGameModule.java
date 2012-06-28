// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractionmatcher.model.MixedFractionLevelFactory;
import edu.colorado.phet.fractionmatcher.view.MatchingGameCanvas;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;

import static edu.colorado.phet.fractionsintro.FractionsIntroApplication.runModule;

/**
 * Module for the matching game tab.
 *
 * @author Sam Reid
 */
public class MixedNumbersMatchingGameModule extends AbstractFractionsModule {
    public MixedNumbersMatchingGameModule( boolean dev, boolean standaloneSim ) {
        this( dev, new MatchingGameModel( new MixedFractionLevelFactory() ), standaloneSim );
    }

    private MixedNumbersMatchingGameModule( boolean dev, MatchingGameModel model, boolean standaloneSim ) {
        super( Components.mixedNumbersTab, Strings.MIXED_NUMBERS, model.clock );
        setSimulationPanel( new MatchingGameCanvas( dev, model, standaloneSim ) );
    }

    @Override protected JComponent createClockControlPanel( final IClock clock ) {
        return null;
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new MatchingGameModule( true, false ) ); }
}