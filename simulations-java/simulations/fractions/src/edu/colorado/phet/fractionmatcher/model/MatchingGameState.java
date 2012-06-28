// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.fractionmatcher.model.Motions.*;
import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static fj.data.List.range;

/**
 * Immutable class for the state of the matching game model.
 *
 * @author Sam Reid
 */
public @Data class MatchingGameState {

    //Movable fractions
    public final List<MovableFraction> fractions;

    //Cells where the fractions start
    public final List<Cell> startCells;

    //Cells where the fractions start
    public final List<Cell> scoreCells;

    //Number of times the user scored by getting a fraction into a score cell, used for iterating to the next score cell for animation
    public final int scored;

    //Scales that fractions can be dropped on
    public final Scale leftScale = new Scale( new Vector2D( 220, 320 ) );
    public final Scale rightScale = new Scale( new Vector2D( 570, 320 ) );

    //Time (in sim time) of when something was dropped on the scale, for purposes of animating the bar
    public final long leftScaleDropTime;
    public final long rightScaleDropTime;

    //Issues general to other games
    public final GameInfo info;

    //How long the bar graphs have been animating in seconds, 0 if not started yet
    public final double barGraphAnimationTime;

    //List of results from completing games.
    public final List<GameResult> gameResults;

    //Last wrong answer, stored for purposes of making sure the user changes their answer before pressing try again
    public final Option<Answer> lastWrongAnswer;

    public static MatchingGameState initialState( AbstractLevelFactory factory ) {
        return newLevel( 1, List.<GameResult>nil(), factory ).withMode( Mode.CHOOSING_SETTINGS );
    }

    public static MatchingGameState newLevel( int level, List<GameResult> gameResults, AbstractLevelFactory factory ) {
        final List<Cell> startCells = createCells( 100, 427, 130, 120, 6, 2, 0, 0 );
        final List<Cell> scoreCells = createCells( 10, 12, 155, 90, 6, 1, 10, 0 );
        return new MatchingGameState( factory.createLevel( level, startCells ), startCells, scoreCells, 0, 0, 0, new GameInfo( level, false, 0, Mode.USER_IS_MOVING_OBJECTS_TO_THE_SCALES, 0, 0, 0, true ), 0, gameResults, Option.<Answer>none() );
    }

    //Create adjacent cells from which fractions can be dragged
    private static List<Cell> createCells( final int x, final int y, final int width, final int height, int columns, final int rows, final double spacingX, final double spacingY ) {
        return range( 0, columns ).bind( new F<Integer, List<Cell>>() {
            @Override public List<Cell> f( final Integer column ) {
                return range( 0, rows ).map( new F<Integer, Cell>() {
                    @Override public Cell f( Integer row ) {
                        return new Cell( new ImmutableRectangle2D( x + column * ( width + spacingX ), y + row * ( height + spacingY ), width, height ) );
                    }
                } );
            }
        } );
    }

    public Mode getMode() { return info.mode; }

    public int getChecks() {return info.checks;}

    public MatchingGameState withFractions( List<MovableFraction> fractions ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    MatchingGameState withScored( int scored ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    public MatchingGameState withAudio( boolean audio ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info.withAudio( audio ), barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    public MatchingGameState withLeftScaleDropTime( long leftScaleDropTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    public MatchingGameState withRightScaleDropTime( long rightScaleDropTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    //NOTE: changing modes resets the bar graph animation time
    public MatchingGameState withMode( final Mode mode ) { return withInfo( info.withMode( mode ) ).withBarGraphAnimationTime( 0.0 ); }

    public MatchingGameState withInfo( final GameInfo info ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    MatchingGameState withBarGraphAnimationTime( final double barGraphAnimationTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    MatchingGameState withGameResults( final List<GameResult> gameResults ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    public MatchingGameState withLastWrongAnswer( final Option<Answer> lastWrongAnswer ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, info, barGraphAnimationTime, gameResults, lastWrongAnswer ); }

    public MatchingGameState stepInTime( final double dt ) {
        return withFractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction f ) {
                return f.stepInTime( new UpdateArgs( f, dt, MatchingGameState.this ) );
            }
        } ) ).
                withInfo( info.withTime( info.time + (long) ( dt * 1000.0 ) ) ).
                withBarGraphAnimationTime( info.mode == Mode.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS || info.mode == Mode.SHOWING_WHY_ANSWER_WRONG || info.mode == Mode.USER_CHECKED_CORRECT_ANSWER ? barGraphAnimationTime + dt : barGraphAnimationTime );
    }

    public Option<MovableFraction> getScaleFraction( final Scale scale ) {
        return fractions.find( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( MovableFraction m ) {
                return m.position.equals( scale.getAttachmentPoint( m ) );
            }
        } );
    }

    double getScaleValue( Scale scale ) { return getScaleFraction( scale ).isSome() ? getScaleFraction( scale ).some().getFractionValue() : 0.0; }

    public double getLeftScaleValue() { return getScaleValue( leftScale ); }

    public double getRightScaleValue() { return getScaleValue( rightScale ); }

    //Move fractions from the scale back to a free starting cell
    public MatchingGameState jettisonFraction( Scale scale ) {
        final Option<MovableFraction> scaleFraction = getScaleFraction( scale );
        return scaleFraction.option( this, new F<MovableFraction, MatchingGameState>() {
            @Override public MatchingGameState f( final MovableFraction match ) {
                return withFractions( fractions.map( new F<MovableFraction, MovableFraction>() {
                    @Override public MovableFraction f( MovableFraction m ) {
                        return match.equals( m ) ? m.withMotion( moveToCell( getClosestFreeStartCell( m ) ) ) : m;
                    }
                } ) );
            }
        } );
    }

    boolean isOnScale( Scale scale, final MovableFraction movableFraction ) {
        return getScaleFraction( scale ).option( false, new F<MovableFraction, Boolean>() {
            @Override public Boolean f( MovableFraction m ) {
                return m.equals( movableFraction );
            }
        } );
    }

    boolean isOnScale( final MovableFraction m ) { return isOnScale( leftScale, m ) || isOnScale( rightScale, m ); }

    //After the user created a correct match, it should be animated to the top of the screen so it will be visible as an equality for the rest of the sim run.
    public MatchingGameState animateMatchToScoreCell() {

        //Create the list of fractions which has the matched values moving toward the appropriate locations in the cells at the top of the screenl
        final List<MovableFraction> newFractionList = fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction m ) {

                //Metrics for where to move the fractions.
                double width = m.withScale( 0.5 ).getNodeWithCorrectScale().getFullBounds().getWidth();
                final Cell cell = scoreCells.index( scored );
                final int offset = 15;

                //Create functions for moving to the correct location at the top of the screen.
                final F<UpdateArgs, MovableFraction> moveToLeftSide = moveToPosition( new Vector2D( cell.rectangle.getCenter().getX() - width / 2 - offset, cell.rectangle.getCenter().getY() ) );
                final F<UpdateArgs, MovableFraction> moveToRightSide = moveToPosition( new Vector2D( cell.rectangle.getCenter().getX() + width / 2 + offset, cell.rectangle.getCenter().getY() ) );

                //Combine moving and shrinking
                final F<UpdateArgs, MovableFraction> moveLeftAndShrink = composite( moveToLeftSide, scale( 0.5 ) );
                final F<UpdateArgs, MovableFraction> moveRightAndShrink = composite( moveToRightSide, scale( 0.5 ) );

                //Precompute variables to make ternary operator more palatable
                final boolean onLeftScale = isOnScale( leftScale, m );
                final boolean onRightScale = isOnScale( rightScale, m );
                final MovableFraction movingLeft = m.withMotion( moveLeftAndShrink ).withScored( true );
                final MovableFraction movingRight = m.withMotion( moveRightAndShrink ).withScored( true );

                //If it was on the left scale, move it to the left half of the scoring cell, and vice versa.
                return onLeftScale ? movingLeft :
                       onRightScale ? movingRight :
                       m;
            }
        } );

        //Return a new MatchingGameState, with the matched fractions animating toward the top of the screen.
        return withFractions( newFractionList ).withScored( scored + 1 );
    }

    // Returns true if a fraction was placed on the right scale after a fraction was placed on the left scale.
    boolean getLastDroppedScaleRight() { return rightScaleDropTime > leftScaleDropTime; }

    public Cell getClosestFreeStartCell( final MovableFraction f ) {
        return startCells.filter( new F<Cell, Boolean>() {
            @Override public Boolean f( final Cell cell ) {
                return isFree( cell );
            }
        } ).sort( ord( new F<Cell, Double>() {
            @Override public Double f( final Cell u ) {
                return u.getPosition().distance( f.position );
            }
        } ) ).head();
    }

    //Find out if a cell is free for a fraction to move to.  True if no other fraction is there and no other fraction is moving there
    private boolean isFree( final Cell cell ) {
        return !isTaken( cell );
    }

    public boolean allStartCellsFree() {
        return startCells.filter( new F<Cell, Boolean>() {
            @Override public Boolean f( final Cell cell ) {
                return isFree( cell );
            }
        } ).length() == startCells.length();
    }

    private boolean isTaken( final Cell cell ) {
        return fractions.exists( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( final MovableFraction movableFraction ) {
                return !movableFraction.dragging && movableFraction.position.equals( cell.getPosition() );
            }
        } );
    }

    //The user pressed show answer.  We need to take the last object off the scale, and move the right one to the scale.
    public MatchingGameState animateToCorrectAnswer() {

        //find which side needs a match and what the value should be
        final double valueToMatch = getLastDroppedScaleRight() ? getLeftScaleValue() : getRightScaleValue();

        //Find the answer to match with.  It must have the same value and not be on either scale
        final MovableFraction match = fractions.find( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( final MovableFraction m ) {
                return Math.abs( m.getFractionValue() - valueToMatch ) < 1E-8 && !isOnScale( m ) && !m.scored;
            }
        } ).some();

        return withFractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction m ) {

                //Move the bad value back to a free cell
                final boolean matchesLeft = isOnScale( leftScale, m ) && !getLastDroppedScaleRight();
                final boolean matchesRight = isOnScale( rightScale, m ) && getLastDroppedScaleRight();
                if ( matchesLeft || matchesRight ) {
                    return m.withMotion( moveToCell( getClosestFreeStartCell( m ) ) );
                }

                //Replace the latest thing the user placed
                if ( m == match ) {
                    return m.withMotion( getLastDroppedScaleRight() ? MOVE_TO_RIGHT_SCALE : MOVE_TO_LEFT_SCALE );
                }

                return m;
            }
        } ) );
    }

    public MatchingGameState newGame( final int level, final int score ) {
        return withMode( Mode.CHOOSING_SETTINGS ).withGameResults( gameResults.snoc( new GameResult( level, score ) ) );
    }

    public MatchingGameState withChecks( final int checks ) { return withInfo( info.withChecks( checks ) ); }

    public MatchingGameState withScore( final int score ) { return withInfo( info.withScore( score ) ); }

    public MatchingGameState withTimerVisible( final Boolean timerVisible ) { return withInfo( info.withTimerVisible( timerVisible ) ); }

    //Store the user's current answer as a wrong answer for purposes of making sure they change the answer before pressing "try again"
    public MatchingGameState recordWrongAnswer() { return withLastWrongAnswer( getCurrentAnswer() ); }

    //Record the user's current answer (must exist) for purposes of making sure they change the answer before pressing "try again"
    private Option<Answer> getCurrentAnswer() {
        assert getScaleFraction( leftScale ).isSome() && getScaleFraction( rightScale ).isSome();
        return Option.some( new Answer( getScaleFraction( leftScale ).some().id,
                                        getScaleFraction( rightScale ).some().id ) );
    }

    //Check whether the user changed their answer since the last time they pressed "try again",
    public boolean changedFromWrongAnswer() { return !lastWrongAnswer.equals( getCurrentAnswer() ); }
}