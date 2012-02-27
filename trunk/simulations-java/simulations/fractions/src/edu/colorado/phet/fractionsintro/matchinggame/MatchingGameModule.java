// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameCanvas2;

/**
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule() {
        this( new MatchingGameModel() );
    }

    public MatchingGameModule( MatchingGameModel model ) {
        super( "Matching Game", model.clock );
        setSimulationPanel( new MatchingGameCanvas2( model ) );
    }
}