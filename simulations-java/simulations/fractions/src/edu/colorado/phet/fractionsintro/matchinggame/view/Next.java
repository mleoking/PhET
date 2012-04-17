package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;

import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.WAITING_FOR_USER_TO_CHECK_ANSWER;

/**
 * Moves to the next match.
 *
 * @author Sam Reid
 */
public class Next extends F<MatchingGameState, MatchingGameState> {
    @Override public MatchingGameState f( final MatchingGameState s ) {
        return s.animateMatchToScoreCell().withMode( WAITING_FOR_USER_TO_CHECK_ANSWER ).withChecks( 0 );
    }
}