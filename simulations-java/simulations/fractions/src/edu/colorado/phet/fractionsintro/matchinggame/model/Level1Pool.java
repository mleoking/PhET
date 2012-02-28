// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;

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
import static fj.data.List.range;

/**
 * @author Sam Reid
 */
public class Level1Pool {
    //Nodes for filling the cells.
    final F<Fraction, PNode> numeric = new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new FractionNode( f, 0.3 );
        }
    };
    final F<Fraction, PNode> horizontalBars = new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new HorizontalBarsNode( new Fraction( f.numerator, f.denominator ), 0.9 );
        }
    };
    final F<Fraction, PNode> verticalBars = new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new VerticalBarsNode( new Fraction( f.numerator, f.denominator ), 0.9 );
        }
    };

    final F<Fraction, PNode> pies = new F<Fraction, PNode>() {
        @Override public PNode f( Fraction fraction ) {
            return new PieNode( createIdentity(), fraction, new Property<ContainerSet>( new ContainerSet( fraction.denominator, new Container[] { new Container( fraction.denominator, range( 0, fraction.numerator ) ) } ) ) );
        }
    };

    public Level1Pool() {
    }

    public List<MovableFraction> create( List<Cell> cells ) {
        return cells.map( new F<Cell, MovableFraction>() {
            @Override public MovableFraction f( Cell c ) {
                return c.i == 0 && c.j == 0 ? fraction( 2, 3, c, numeric ) :
                       c.i == 1 && c.j == 0 ? fraction( 3, 4, c, pies ) :
                       c.i == 2 && c.j == 0 ? fraction( 1, 4, c, numeric ) :
                       c.i == 3 && c.j == 0 ? fraction( 2, 3, c, horizontalBars ) :
                       c.i == 4 && c.j == 0 ? fraction( 1, 3, c, pies ) :
                       c.i == 5 && c.j == 0 ? fraction( 1, 4, c, verticalBars ) :
                       c.i == 0 && c.j == 1 ? fraction( 2, 3, c, pies ) :
                       c.i == 1 && c.j == 1 ? fraction( 3, 4, c, numeric ) :
                       c.i == 2 && c.j == 1 ? fraction( 2, 3, c, verticalBars ) :
                       c.i == 3 && c.j == 1 ? fraction( 1, 4, c, horizontalBars ) :
                       c.i == 4 && c.j == 1 ? fraction( 1, 3, c, horizontalBars ) :
                       c.i == 5 && c.j == 1 ? fraction( 1, 4, c, pies ) :
                       fraction( 2, 5, c, numeric );
            }
        } );
    }

    //Create a MovableFraction for the given fraction at the specified cell
    private static MovableFraction fraction( int numerator, int denominator, Cell cell, final F<Fraction, PNode> node ) {

        //Cache nodes as images to improve performance
        return new MovableFraction( new Vector2D( cell.rectangle.getCenter() ), numerator, denominator, false, cell, 1.0,
                                    new Cache<Fraction, PNode>( new F<Fraction, PNode>() {
                                        @Override public PNode f( Fraction fraction ) {
                                            return new PImage( node.f( fraction ).toImage() );
                                        }
                                    } ),
                                    MoveToCell( cell ) );
    }
}