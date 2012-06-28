// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.fractionmatcher.model.IntroLevelFactory;
import edu.colorado.phet.fractionmatcher.model.MatchingGameModel;
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
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule( boolean dev, boolean standaloneSim ) {
        this( dev, new MatchingGameModel( new IntroLevelFactory() ), standaloneSim );
    }

    private MatchingGameModule( boolean dev, MatchingGameModel model, boolean standaloneSim ) {
        super( Components.matchingGameTab, Strings.MATCHING_GAME, model.clock );
        setSimulationPanel( new MatchingGameCanvas( dev, model, standaloneSim, Strings.FRACTION_MATCHER ) );
    }

    @Override protected JComponent createClockControlPanel( final IClock clock ) {
        return null;
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new MatchingGameModule( true, false ) ); }
}