// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractionmatcher.view.MatchingGameCanvas;
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
        this( dev, new MatchingGameModel(), standaloneSim );
    }

    private MatchingGameModule( boolean dev, MatchingGameModel model, boolean standaloneSim ) {
        super( Components.matchingGameTab, "Matching Game", model.clock );  //REVIEW literal will present an i18n problem if this module is ever used in a multi-tab sim
        setSimulationPanel( new MatchingGameCanvas( dev, model, standaloneSim ) );
    }

    @Override protected JComponent createClockControlPanel( final IClock clock ) {
        return null;
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new MatchingGameModule( true, false ) ); }
}