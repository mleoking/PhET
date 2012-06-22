package edu.colorado.phet.fractionmatcher.view;

import fj.F;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractionmatcher.model.Mode;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ModelActions;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ModelComponentTypes;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ModelComponents;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys;

import static edu.colorado.phet.fractionmatcher.model.Mode.*;

/**
 * Commands send by the user to update the model.  They are @Data classes for purposes of automated regression testing.
 * They are inner classes since they are short and it is nice to be able to see them together.
 *
 * @author Sam Reid
 */
class Controller {

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
        @Override public MatchingGameState f( final MatchingGameState s ) {
            final MatchingGameState updated = s.animateMatchToScoreCell().withMode( WAITING_FOR_USER_TO_CHECK_ANSWER ).withChecks( 0 );
            return updated.allStartCellsFree() ?
                   updated.withInfo( updated.info.withBestTime( Math.min( updated.info.time, updated.info.bestTime ) ) ).withMode( SHOWING_GAME_OVER_SCREEN ) :
                   updated;
        }
    }

    public static @Data class CheckAnswer extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState state ) {
            int points = state.getChecks() == 0 ? 2 : 1;
            final boolean correct = state.getLeftScaleValue() == state.getRightScaleValue();

            //Send a message to indicate whether it was right or wrong and how many points the user got
            SimSharingManager.sendModelMessage( ModelComponents.answer, ModelComponentTypes.answer, ModelActions.checked,
                                                ParameterSet.parameterSet( ParameterKeys.isCorrect, correct ).with( ParameterKeys.points, correct ? points : 0 ) );
            return correct ? state.withChecks( state.info.checks + 1 ).
                    withMode( USER_CHECKED_CORRECT_ANSWER ).
                    withScore( state.info.score + points ) :
                   state.withChecks( state.info.checks + 1 ).withMode( SHOWING_WHY_ANSWER_WRONG );
        }
    }

    public static @Data class Resample extends fj.F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState matchingGameState ) {
            return MatchingGameState.newLevel( matchingGameState.info.level, matchingGameState.gameResults ).withMode( WAITING_FOR_USER_TO_CHECK_ANSWER );
        }
    }

    public static @Data class GameOver extends fj.F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState matchingGameState ) {
            return matchingGameState.withScore( 12 ).withMode( SHOWING_GAME_OVER_SCREEN );
        }
    }
}