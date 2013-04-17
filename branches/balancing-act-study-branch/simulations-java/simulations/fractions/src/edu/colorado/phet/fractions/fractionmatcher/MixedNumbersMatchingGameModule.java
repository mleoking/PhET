// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractions.fractionmatcher.model.MixedFractionLevelFactory;
import edu.colorado.phet.fractions.fractionmatcher.view.LevelSelectionNode;
import edu.colorado.phet.fractions.fractionmatcher.view.MatchingGameCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

/**
 * Module for the matching game tab.
 *
 * @author Sam Reid
 */
public class MixedNumbersMatchingGameModule extends AbstractFractionsModule {
    private final boolean dev;
    private final BooleanProperty audioEnabled;
    private MatchingGameModel model;

    public MixedNumbersMatchingGameModule( boolean dev, final BooleanProperty audioEnabled ) {
        this( dev, new MatchingGameModel( new ConstantDtClock( 60.0 ), new MixedFractionLevelFactory() ), audioEnabled );
    }

    private MixedNumbersMatchingGameModule( boolean dev, MatchingGameModel model, final BooleanProperty audioEnabled ) {
        super( Components.mixedNumbersTab, Strings.MIXED_NUMBERS, model.clock );
        this.dev = dev;
        this.audioEnabled = audioEnabled;
        this.model = model;
        setSimulationPanel( new MatchingGameCanvas( dev, model, Strings.FRACTION_MATCHER_MIXED_NUMBERS, LevelSelectionNode.mixedIcons, audioEnabled, this ) );
    }

    @Override protected JComponent createClockControlPanel( final IClock clock ) {
        return null;
    }

    @Override public void reset() {
        super.reset();
        this.model.clock.removeAllClockListeners();
        final MatchingGameModel newModel = new MatchingGameModel( model.clock, new MixedFractionLevelFactory() );
        this.model = newModel;
        setSimulationPanel( new MatchingGameCanvas( dev, newModel, Strings.FRACTION_MATCHER_MIXED_NUMBERS, LevelSelectionNode.mixedIcons, audioEnabled, this ) );
        audioEnabled.reset();
    }
}