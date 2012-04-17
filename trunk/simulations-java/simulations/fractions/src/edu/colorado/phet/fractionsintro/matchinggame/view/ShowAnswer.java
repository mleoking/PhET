package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.State;

/**
 * Function that checks whether an answer is correct and updates the model based on whether it was.
 *
 * @author Sam Reid
 */
public @Data class ShowAnswer extends F<MatchingGameState, MatchingGameState> {
    @Override public MatchingGameState f( final MatchingGameState state ) {
        assert state.getLeftScaleValue() != state.getRightScaleValue();
        return state.withState( State.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS ).animateToCorrectAnswer();
    }
}