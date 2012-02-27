// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.HorizontalBarsNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PieNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.VerticalBarsNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.MoveToCell;
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

    public final Scale leftScale = new Scale( new Vector2D( 150, 300 ) );
    public final Scale rightScale = new Scale( new Vector2D( 500, 300 ) );

    public static MatchingGameState initialState() {
        final List<Cell> cells = createCells( 0, 500, 100, 100, 6, 2 );

        //Nodes for filling the cells.

        final F<Fraction, PNode> numericFraction = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction f ) {
                return new FractionNode( f, 0.2 );
            }
        };
        final F<Fraction, PNode> horizontalBars = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction f ) {
                return new HorizontalBarsNode( new Fraction( f.numerator, f.denominator ), 0.85 );
            }
        };
        final F<Fraction, PNode> verticalBars = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction f ) {
                return new VerticalBarsNode( new Fraction( f.numerator, f.denominator ), 0.85 );
            }
        };

        final F<Fraction, PNode> pies = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction fraction ) {
                return new PieNode( createIdentity(), fraction, new Property<ContainerSet>( new ContainerSet( fraction.denominator, new Container[] { new Container( fraction.denominator, range( 0, fraction.numerator ) ) } ) ) );
            }
        };

        return new MatchingGameState( cells.map( new F<Cell, MovableFraction>() {
            @Override public MovableFraction f( Cell c ) {
                return c.i == 0 && c.j == 0 ? fraction( 2, 3, c, numericFraction ) :
                       c.i == 1 && c.j == 0 ? fraction( 4, 9, c, numericFraction ) :
                       c.i == 2 && c.j == 0 ? fraction( 5, 9, c, numericFraction ) :
                       c.i == 3 && c.j == 0 ? fraction( 5, 9, c, horizontalBars ) :
                       c.i == 4 && c.j == 0 ? fraction( 2, 3, c, verticalBars ) :
                       c.i == 5 && c.j == 0 ? fraction( 5, 9, c, verticalBars ) :
                       c.i == 0 && c.j == 1 ? fraction( 5, 9, c, pies ) :
                       c.i == 1 && c.j == 1 ? fraction( 2, 3, c, pies ) :
                       c.i == 2 && c.j == 1 ? fraction( 1, 9, c, pies ) :
                       c.i == 3 && c.j == 1 ? fraction( 1, 4, c, horizontalBars ) :
                       c.i == 4 && c.j == 1 ? fraction( 5, 9, c, numericFraction ) :
                       c.i == 5 && c.j == 1 ? fraction( 5, 9, c, pies ) :
                       fraction( 2, 5, c, numericFraction );
            }
        } ), cells );
    }

    //Create a MovableFraction for the given fraction at the specified cell
    private static MovableFraction fraction( int numerator, int denominator, Cell cell, final F<Fraction, PNode> node ) {

        //Cache nodes as images to improve performance
        return new MovableFraction( new Vector2D( cell.rectangle.getCenter() ), numerator, denominator, false, cell,
                                    new Cache<Fraction, PNode>( new F<Fraction, PNode>() {
                                        @Override public PNode f( Fraction fraction ) {
                                            return new PImage( node.f( fraction ).toImage() );
                                        }
                                    } ),
                                    MoveToCell( cell ) );
    }

    //Create adjacent cells from which fractions can be dragged
    private static List<Cell> createCells( final int x, final int y, final int width, final int height, int columns, final int rows ) {
        return range( 0, columns ).bind( new F<Integer, List<Cell>>() {
            @Override public List<Cell> f( final Integer column ) {
                return range( 0, rows ).map( new F<Integer, Cell>() {
                    @Override public Cell f( Integer row ) {
                        return new Cell( new ImmutableRectangle2D( x + column * width, y + row * height, width, height ), column, row );
                    }
                } );
            }
        } );
    }

    public MatchingGameState fractions( List<MovableFraction> fractions ) { return new MatchingGameState( fractions, cells ); }

    public List<Scale> scales() { return list( leftScale, rightScale ); }

    public MatchingGameState stepInTime( final double dt ) {
        return fractions( fractions.map( new F<MovableFraction, MovableFraction>() {
            @Override public MovableFraction f( MovableFraction f ) {
                return f.stepInTime( new UpdateArgs( f, dt, MatchingGameState.this ) );
            }
        } ) );
    }
}