package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.SHOWING_WHY_ANSWER_WRONG;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.USER_CHECKED_CORRECT_ANSWER;

/**
 * Function that checks whether an answer is correct and updates the model based on whether it was.
 *
 * @author Sam Reid
 */
public @Data class CheckAnswer extends F<MatchingGameState, MatchingGameState> {
    @Override public MatchingGameState f( final MatchingGameState state ) {
        int points = state.getChecks() == 0 ? 2 : 1;
        return state.getLeftScaleValue() == state.getRightScaleValue() ? state.withChecks( state.info.checks + 1 ).withMode( USER_CHECKED_CORRECT_ANSWER ).withScore( state.info.score + points ) :
               state.withChecks( state.info.checks + 1 ).withMode( SHOWING_WHY_ANSWER_WRONG );
    }
}