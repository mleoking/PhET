package edu.colorado.phet.fractionsintro.matchinggame.view;

import lombok.Data;

import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.WAITING_FOR_USER_TO_CHECK_ANSWER;

/**
 * @author Sam Reid
 */
public @Data class Resample extends fj.F<MatchingGameState, MatchingGameState> {
    @Override public MatchingGameState f( final MatchingGameState matchingGameState ) {
        return MatchingGameState.newLevel( matchingGameState.info.level ).withMode( WAITING_FOR_USER_TO_CHECK_ANSWER );
    }
}