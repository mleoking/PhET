package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.WAITING_FOR_USER_TO_CHANGE_ANSWER;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.WAITING_FOR_USER_TO_CHECK_ANSWER;

/**
 * Commands send by the user to update the model.  They are @Data classes for purposes of automated regression testing.
 * They are inner classes since they are short and it is nice to be able to see them together.
 *
 * @author Sam Reid
 */
public class Controller {

    //Function that checks whether an answer is correct and updates the model based on whether it was.
    public static @Data class TryAgain extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState s ) { return s.withMode( WAITING_FOR_USER_TO_CHANGE_ANSWER ); }
    }

    //Function that checks whether an answer is correct and updates the model based on whether it was.
    public static @Data class ShowAnswer extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState state ) {
            assert state.getLeftScaleValue() != state.getRightScaleValue();
            return state.withMode( Mode.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS ).animateToCorrectAnswer();
        }
    }

    //Moves to the next match.
    public static @Data class Next extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState s ) { return s.animateMatchToScoreCell().withMode( WAITING_FOR_USER_TO_CHECK_ANSWER ).withChecks( 0 ); }
    }
}