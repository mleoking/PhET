// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame;

import edu.colorado.phet.fractions.intro.common.AbstractFractionsModule;
import edu.colorado.phet.fractions.intro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractions.intro.matchinggame.view.MatchingGameCanvas;

/**
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule() {
        super( "Matching Game" );
        MatchingGameModel model = new MatchingGameModel();
        setSimulationPanel( new MatchingGameCanvas( model ) );
    }
}