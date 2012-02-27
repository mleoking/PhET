// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.HorizontalBarsNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.PieNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.VerticalBarsNode;
import edu.umd.cs.piccolo.PNode;

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

    public final Scale leftScale = new Scale( 200, 300 );
    public final Scale rightScale = new Scale( 400, 300 );

    public static MatchingGameState initialState() {
        final List<Cell> cells = createCells( 0, 500, 100, 100, 6, 2 );

        //Nodes for filling the cells.

        class SmallFractionNode extends FractionNode {
            public SmallFractionNode( Fraction fraction ) {
                this( fraction.numerator, fraction.denominator );
            }

            public SmallFractionNode( int numerator, int denominator ) {
                super( numerator, denominator );
                scale( 0.2 );
            }
        }

        final F<Fraction, PNode> numericFraction = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction f ) {
                return new SmallFractionNode( f );
            }
        };

        final F<Fraction, PNode> horizontalBars = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction f ) {
                return new HorizontalBarsNode( ModelViewTransform.createIdentity(), new Fraction( f.numerator, f.denominator ) ) {{
                    scale( 0.85 );
                }};
            }
        };

        final F<Fraction, PNode> verticalBars = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction f ) {
                return new VerticalBarsNode( ModelViewTransform.createIdentity(), new Fraction( f.numerator, f.denominator ) ) {{
                    scale( 0.85 );
                }};
            }
        };

        final F<Fraction, PNode> pies = new F<Fraction, PNode>() {
            @Override public PNode f( Fraction fraction ) {
                return new PieNode( ModelViewTransform.createIdentity(), fraction, new Property<ContainerSet>( new ContainerSet( fraction.denominator, new Container[] { new Container( fraction.denominator, range( 0, fraction.numerator ) ) } ) ) );
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

    private static MovableFraction fraction( int numerator, int denominator, Cell cell, F<Fraction, PNode> nodeF ) {
        return new MovableFraction( new ImmutableVector2D( cell.rectangle.getCenter() ), numerator, denominator, false, nodeF );
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
}