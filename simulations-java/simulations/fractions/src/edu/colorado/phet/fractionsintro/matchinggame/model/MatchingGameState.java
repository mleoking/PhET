// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

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
    public final List<Cell> cells;

    //Cells where the fractions start
    public final List<Cell> scoreCells;

    //Number of times the user scored by getting a fraction into a score cell, used for iterating to the next score cell for animation
    public final int scored;

    public final Scale leftScale = new Scale( new Vector2D( 220, 300 ) );
    public final Scale rightScale = new Scale( new Vector2D( 570, 300 ) );

    //Time (in sim time) of when something was dropped on the scale, for purposes of animating the bar
    public final double leftScaleDropTime;
    public final double rightScaleDropTime;

    public final int level;

    public final boolean audio;

    public static MatchingGameState initialState() {
        return initialState( 1 );
    }

    public static MatchingGameState initialState( int level ) {
        final List<Cell> cells = createCells( 100, 415, 130, 120, 6, 2, 0, 0 );
        final List<Cell> scoreCells = createCells( 10, 5, 155, 90, 6, 1, 10, 0 );
        return new MatchingGameState( Levels.get( level ).f( cells ), cells, scoreCells, 0, 0.0, 0.0, level, false );
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

    public MatchingGameState fractions( List<MovableFraction> fractions ) { return new MatchingGameState( fractions, cells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

    public MatchingGameState scored( int scored ) { return new MatchingGameState( fractions, cells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

    public MatchingGameState audio( boolean audio ) { return new MatchingGameState( fractions, cells, scoreCells, scored, leftScaleDropTime, rightScaleDropTime, level, audio ); }

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
                        return match.equals( m ) ? m.motion( MoveToCell( m.home ) ) : m;
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
                final F<UpdateArgs, MovableFraction> moveToLeftSide = MoveToPosition( new Vector2D( cell.rectangle.x + width / 2 + 2, cell.rectangle.getCenter().getY() ) );
                final F<UpdateArgs, MovableFraction> moveToRightSide = MoveToPosition( new Vector2D( cell.rectangle.getMaxX() - width / 2 - 2, cell.rectangle.getCenter().getY() ) );

                //Shrink values >1 more so they will both fit in the score box.  Not perfect, will have to be fine-tuned.
                final F<UpdateArgs, MovableFraction> shrink = Scale( m.getValue() > 1 ? 0.25 : 0.5 );
                return isOnScale( leftScale, m ) ? m.motion( composite( moveToLeftSide, shrink ) ).scored( true ) :
                       isOnScale( rightScale, m ) ? m.motion( composite( moveToRightSide, shrink ) ).scored( true ) :
                       m;
            }
        } ) ).scored( scored + 1 );
    }
}