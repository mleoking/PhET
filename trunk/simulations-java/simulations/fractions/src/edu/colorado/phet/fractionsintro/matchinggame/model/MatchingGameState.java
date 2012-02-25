// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static fj.data.List.range;

/**
 * Immutable class for the state of the matching game model.
 *
 * @author Sam Reid
 */
@Data public class MatchingGameState {

    //Movable fractions
    public final List<Fraction> fractions;

    //Cells where the fractions start
    public final List<Cell> cells;

    public static MatchingGameState initialState() {
        final List<Cell> cells = createCells( 0, 0, 100, 100, 6, 2 );
        return new MatchingGameState( cells.map( new F<Cell, Fraction>() {
            @Override public Fraction f( Cell c ) {
                return new Fraction( new ImmutableVector2D( c.rectangle.getCenter() ), 2, 3, false );
            }
        } ), cells );
    }

    //Create adjacent cells from which fractions can be dragged
    private static List<Cell> createCells( final int x, final int y, final int width, final int height, int columns, final int rows ) {
        return range( 0, columns ).bind( new F<Integer, List<Cell>>() {
            @Override public List<Cell> f( final Integer column ) {
                return range( 0, rows ).map( new F<Integer, Cell>() {
                    @Override public Cell f( Integer row ) {
                        return new Cell( new ImmutableRectangle2D( x + column * width, y + row * height, width, height ) );
                    }
                } );
            }
        } );
    }

    public MatchingGameState fractions( List<Fraction> fractions ) { return new MatchingGameState( fractions, cells ); }
}