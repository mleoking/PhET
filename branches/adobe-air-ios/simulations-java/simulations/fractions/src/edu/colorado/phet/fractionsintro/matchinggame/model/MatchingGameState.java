// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Levels.Levels;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.*;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.Scale;
import static fj.data.List.list;
import static fj.data.List.range;

/**
 * Immutable class for the state of the matching game model.
 *
 * @author Sam Reid
 */
@Data public class MatchingGameState {

    //Movable fractions
    public final List<MovableFraction> fractions;

    //Cells where the fractions start
    public final List<Cell> startCells;

    //Cells where the fractions start
    public final List<Cell> scoreCells;

    //Number of times the user scored by getting a fraction into a score cell, used for iterating to the next score cell for animation
    public final int scored;

    public final Scale leftScale = new Scale( new Vector2D( 220, 320 ) );
    public final Scale rightScale = new Scale( new Vector2D( 570, 320 ) );

    //Time (in sim time) of when something was dropped on the scale, for purposes of animating the bar
    public final long leftScaleDropTime;
    public final long rightScaleDropTime;

    public final int level;

    public final boolean audio;

    public static MatchingGameState initialState() { return initialState( 1 ); }

    public static MatchingGameState initialState( int level ) {
        final List<Cell> cells = createCells( 100, 415 + 12, 130, 120, 6, 2, 0, 0 );
        final List<Cell> scoreCells = createCells( 10, 12, 155, 90, 6, 1, 10, 0 );
        return new MatchingGameState( Levels.get( level ).f( cells ), cells, scoreCells, 0, 0, 0, level, false );
    }

    //Create adjacent cells from which fractions can be dragged
    private static List<Cell> createCells( final int x, final int y, final int width, final int height, int columns, final int rows, final double spacingX, final double spacingY ) {
        return range( 0, columns ).bind( new F<Integer, List<Cell>>() {
            @Override public List<Cell> f( final Integer column ) {
                return range( 0, rows ).map( new F<Integer, Cell>() {
                    @Override public Cell f( Integer row ) {
                        return new Cell( new ImmutableRectangle2D( x + column * ( width + spacingX ), y + row * ( height + spacingY ), width, height ), column, row );
                    }
                } );
            }
        } );
    }

    public MatchingGameState fractions( List<MovableFraction> fractions ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

    public MatchingGameState scored( int scored ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

    public MatchingGameState audio( boolean audio ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

    public MatchingGameState leftScaleDropTime( long leftScaleDropTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

    public MatchingGameState rightScaleDropTime( long rightScaleDropTime ) { return new MatchingGameState( fractions, startCells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

    public List<Scale> getScales() { return list( leftScale, rightScale ); }

    public MatchingGameState stepInTime( final double dt ) {
        return fractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction f ) {
                return f.stepInTime( new UpdateArgs( f, dt, MatchingGameState.this ) );
            }
        } ) );
    }

    public Option<MovableFraction> getScaleFraction( final Scale scale ) {
        return fractions.find( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( MovableFraction m ) {
                return m.position.equals( scale.getAttachmentPoint( m ) );
            }
        } );
    }

    public double getScaleValue( Scale scale ) { return getScaleFraction( scale ).isSome() ? getScaleFraction( scale ).some().getValue() : 0.0; }

    public double getLeftScaleValue() { return getScaleValue( leftScale ); }

    public double getRightScaleValue() { return getScaleValue( rightScale ); }

    public MatchingGameState jettisonFraction( Scale scale ) {
        final Option<MovableFraction> scaleFraction = getScaleFraction( scale );
        return scaleFraction.option( this, new F<MovableFraction, MatchingGameState>() {
            @Override public MatchingGameState f( final MovableFraction match ) {
                return fractions( fractions.map( new F<MovableFraction, MovableFraction>() {
                    @Override public MovableFraction f( MovableFraction m ) {
                        return match.equals( m ) ? m.motion( MoveToCell( getClosestFreeStartCell( m ) ) ) : m;
                    }
                } ) );
            }
        } );
    }

    public boolean isOnScale( Scale scale, final MovableFraction movableFraction ) {
        return getScaleFraction( scale ).option( false, new F<MovableFraction, Boolean>() {
            @Override public Boolean f( MovableFraction m ) {
                return m.equals( movableFraction );
            }
        } );
    }

    public MatchingGameState animateMatchToScoreCell() {
        return fractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction m ) {
                double width = m.scale( 0.5 ).toNode().getFullBounds().getWidth();
                final Cell cell = scoreCells.index( scored );
                final int offset = 15;
                final F<UpdateArgs, MovableFraction> moveToLeftSide = MoveToPosition( new Vector2D( cell.rectangle.getCenter().getX() - width / 2 - offset, cell.rectangle.getCenter().getY() ) );
                final F<UpdateArgs, MovableFraction> moveToRightSide = MoveToPosition( new Vector2D( cell.rectangle.getCenter().getX() + width / 2 + offset, cell.rectangle.getCenter().getY() ) );

                //Could shrink values >1 more so they will both fit in the score box using m.getValue() > 1 ? 0.5 : 0.5.  Not perfect, will have to be fine-tuned.
                final F<UpdateArgs, MovableFraction> shrink = Scale( 0.5 );
                return isOnScale( leftScale, m ) ? m.motion( composite( moveToLeftSide, shrink ) ).scored( true ) :
                       isOnScale( rightScale, m ) ? m.motion( composite( moveToRightSide, shrink ) ).scored( true ) :
                       m;
            }
        } ) ).scored( scored + 1 );
    }

    public boolean getLastDroppedScaleRight() { return rightScaleDropTime > leftScaleDropTime; }

    public Cell getClosestFreeStartCell( final MovableFraction f ) {
        return startCells.filter( new F<Cell, Boolean>() {
            @Override public Boolean f( final Cell cell ) {
                return isFree( cell );
            }
        } ).sort( ord( new F<Cell, Double>() {
            @Override public Double f( final Cell u ) {
                return u.position().distance( f.position );
            }
        } ) ).head();
    }

    //Find out if a cell is free for a fraction to move to.  True if no other fraction is there and no other fraction is moving there
    private boolean isFree( final Cell cell ) {
        return !isTaken( cell );
    }

    private boolean isTaken( final Cell cell ) {
        return fractions.exists( new F<MovableFraction, Boolean>() {
            @Override public Boolean f( final MovableFraction movableFraction ) {
                //TODO: Block the cell if any fraction is moving there, otherwise could over-occupy
                return !movableFraction.dragging && movableFraction.position.equals( cell.position() );// || movableFraction.motion.movingTo( cell.position() );
            }
        } );
    }
}