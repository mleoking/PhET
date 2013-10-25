// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.F;
import fj.data.Option;
import lombok.EqualsAndHashCode;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.fractions.fractionmatcher.model.AbstractLevelFactory;
import edu.colorado.phet.fractions.fractionmatcher.model.Answer;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractions.fractionmatcher.model.Mode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ModelActions;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ModelComponentTypes;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ModelComponents;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys;

import static edu.colorado.phet.fractions.fractionmatcher.model.Mode.*;

/**
 * Commands send by the user to update the model.
 * They are inner classes since they are short and it is nice to be able to see them together.
 * <p/>
 * They are @Data classes for purposes of semi-automated regression testing, so they can be easily serialized, deserialized and compared.
 * See edu.colorado.phet.fractions.fractionsintro.intro.model.RegressionTestRecorder for more information about semi-automated regression testing.
 *
 * @author Sam Reid
 */
class Controller {

    //Function that checks whether an answer is correct and updates the model based on whether it was.
    public static @EqualsAndHashCode(callSuper = false) class TryAgain extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState s ) { return s.withMode( WAITING_FOR_USER_TO_CHANGE_ANSWER ); }
    }

    //Function that checks whether an answer is correct and updates the model based on whether it was.
    public static @EqualsAndHashCode(callSuper = false) class ShowAnswer extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState state ) {
            assert state.getLeftScaleValue() != state.getRightScaleValue();
            return state.withMode( Mode.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS ).animateToCorrectAnswer();
        }
    }

    //Moves to the next match.
    public static @EqualsAndHashCode(callSuper = false) class Next extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState s ) {
            final MatchingGameState updated = s.animateMatchToScoreCell().withMode( USER_IS_MOVING_OBJECTS_TO_THE_SCALES ).withChecks( 0 ).withLastWrongAnswer( Option.<Answer>none() );
            return updated.allStartCellsFree() ?
                   updated.withInfo( updated.info.withBestTime( Math.min( updated.info.time, updated.info.bestTime ) ) ).withMode( SHOWING_GAME_OVER_SCREEN ) :
                   updated;
        }
    }

    public static @EqualsAndHashCode(callSuper = false) class CheckAnswer extends F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState state ) {
            int points = state.getChecks() == 0 ? 2 : 1;
            final boolean correct = state.getLeftScaleValue() == state.getRightScaleValue();

            //Send a message to indicate whether it was right or wrong and how many points the user got
            SimSharingManager.sendModelMessage( ModelComponents.answer, ModelComponentTypes.answer, ModelActions.checked,
                                                ParameterSet.parameterSet( ParameterKeys.isCorrect, correct ).
                                                        with( ParameterKeys.levelID, state.levelID ).
                                                        with( ParameterKeys.points, correct ? points : 0 ).
                                                        with( ParameterKeys.correct, correct ).
                                                        with( ParameterKeys.leftScaleNumerator, state.getLeftScaleNumerator() ).
                                                        with( ParameterKeys.leftScaleDenominator, state.getLeftScaleDenominator() ).
                                                        with( ParameterKeys.rightScaleNumerator, state.getRightScaleNumerator() ).
                                                        with( ParameterKeys.rightScaleDenominator, state.getRightScaleDenominator() ).
                                                        with( ParameterKeys.leftScaleRepresentation, state.getLeftScaleRepresentation() ).
                                                        with( ParameterKeys.rightScaleRepresentation, state.getRightScaleRepresentation() ) );
            return correct ?
                   state.withChecks( state.info.checks + 1 ).
                           withMode( USER_CHECKED_CORRECT_ANSWER ).
                           withScore( state.info.score + points ).
                           withLastWrongAnswer( Option.<Answer>none() ) :
                   state.withChecks( state.info.checks + 1 ).withMode( SHOWING_WHY_ANSWER_WRONG ).recordWrongAnswer();
        }
    }

    public static @EqualsAndHashCode(callSuper = false) class Resample extends fj.F<MatchingGameState, MatchingGameState> {
        public final AbstractLevelFactory levelFactory;

        public Resample( final AbstractLevelFactory levelFactory ) {this.levelFactory = levelFactory;}

        @Override public MatchingGameState f( final MatchingGameState matchingGameState ) {
            return MatchingGameState.newLevel( matchingGameState.info.level, matchingGameState.gameResults, levelFactory ).withMode( USER_IS_MOVING_OBJECTS_TO_THE_SCALES );
        }
    }

    public static @EqualsAndHashCode(callSuper = false) class GameOver extends fj.F<MatchingGameState, MatchingGameState> {
        @Override public MatchingGameState f( final MatchingGameState matchingGameState ) {
            return matchingGameState.withScore( 12 ).withMode( SHOWING_GAME_OVER_SCREEN );
        }
    }
}