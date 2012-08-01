// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.fractionmatcher.model.IntroLevelFactory;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractions.fractionmatcher.view.LevelSelectionNode;
import edu.colorado.phet.fractions.fractionmatcher.view.MatchingGameCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroApplication.runModule;

/**
 * Module for the matching game tab.
 *
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule( boolean dev ) {
        this( dev, new MatchingGameModel( new IntroLevelFactory() ) );
    }

    private MatchingGameModule( boolean dev, MatchingGameModel model ) {
        super( Components.matchingGameTab, Strings.MATCHING_GAME, model.clock );
        setSimulationPanel( new MatchingGameCanvas( dev, model, Strings.FRACTION_MATCHER, LevelSelectionNode.properIcons ) );
    }

    @Override protected JComponent createClockControlPanel( final IClock clock ) {
        return null;
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new MatchingGameModule( true ) ); }
}