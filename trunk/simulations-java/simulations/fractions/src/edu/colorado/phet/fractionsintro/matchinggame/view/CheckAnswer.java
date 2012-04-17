package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;

/**
 * Function that checks whether an answer is correct and updates the model based on whether it was.
 *
 * @author Sam Reid
 */
public @Data class CheckAnswer extends F<MatchingGameState, MatchingGameState> {
    @Override public MatchingGameState f( final MatchingGameState state ) {
        return state.getLeftScaleValue() == state.getRightScaleValue() ? state.withChecks( state.checks + 1 ).withMode( Mode.USER_CHECKED_CORRECT_ANSWER ) :
               state.withChecks( state.checks + 1 ).withMode( Mode.SHOWING_WHY_ANSWER_WRONG );
    }
}