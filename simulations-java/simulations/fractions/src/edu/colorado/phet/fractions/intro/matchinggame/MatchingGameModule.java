// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule() {
        super( "Matching Game" );
        setSimulationPanel( new PhetPCanvas() );
    }
}