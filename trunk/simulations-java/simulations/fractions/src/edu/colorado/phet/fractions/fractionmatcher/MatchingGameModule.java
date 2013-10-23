// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.fractionmatcher.model.IntroLevelFactory;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractions.fractionmatcher.view.LevelSelectionNode;
import edu.colorado.phet.fractions.fractionmatcher.view.MatchingGameCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

/**
 * Module for the matching game tab.
 *
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    private final boolean dev;
    public final BooleanProperty audioEnabled;
    public MatchingGameModel model;

    public MatchingGameModule( boolean dev, BooleanProperty audioEnabled ) {
        this( dev, new MatchingGameModel( new ConstantDtClock( 60.0 ), new IntroLevelFactory() ), audioEnabled );
    }

    private MatchingGameModule( boolean dev, MatchingGameModel model, BooleanProperty audioEnabled ) {
        super( Components.matchingGameTab, Strings.MATCHING_GAME, model.clock );
        setSimulationPanel( new MatchingGameCanvas( dev, model, Strings.FRACTION_MATCHER, LevelSelectionNode.properIcons, audioEnabled, this ) );
        this.dev = dev;
        this.audioEnabled = audioEnabled;
        this.model = model;
    }

    @Override protected JComponent createClockControlPanel( final IClock clock ) {
        return null;
    }

    @Override public void reset() {
        super.reset();
        this.model.clock.removeAllClockListeners();
        final MatchingGameModel newModel = new MatchingGameModel( model.clock, new IntroLevelFactory() );
        this.model = newModel;
        setSimulationPanel( new MatchingGameCanvas( dev, newModel, Strings.FRACTION_MATCHER, LevelSelectionNode.properIcons, audioEnabled, this ) );
        audioEnabled.reset();
    }
}