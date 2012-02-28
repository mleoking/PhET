// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Pair;
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
import static fj.data.List.iterableList;
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

    public static final Random random = new Random();

    public List<MovableFraction> create( List<Cell> _cells ) {
        assert _cells.length() % 2 == 0;

        ArrayList<Fraction> fractions = new ArrayList<Fraction>( Arrays.asList( new Fraction[] {
                new Fraction( 1, 3 ),
                new Fraction( 2, 3 ),
                new Fraction( 1, 4 ),
                new Fraction( 3, 4 ),
                new Fraction( 1, 2 ),
                new Fraction( 1, 1 ) } ) );

        ArrayList<MovableFraction> list = new ArrayList<MovableFraction>();

        //Use mutable collection so it can be removed from for drawing without replacement
        ArrayList<Cell> cells = new ArrayList<Cell>( _cells.toCollection() );
        while ( list.size() < _cells.length() ) {
            System.out.println( "cells = " + cells );
            Pair<MovableFraction, MovableFraction> pair = createPair( fractions, cells );
            list.add( pair._1 );
            list.add( pair._2 );
        }
        return iterableList( list );
    }

    private Pair<MovableFraction, MovableFraction> createPair( ArrayList<Fraction> fractions, ArrayList<Cell> cells ) {

        //choose a fraction
        Fraction f = fractions.get( random.nextInt( fractions.size() ) );

        //Without replacement, remove the old fraction.
        fractions.remove( f );

        ArrayList<F<Fraction, PNode>> representations = new ArrayList<F<Fraction, PNode>>( Arrays.asList( numeric, pies, horizontalBars, verticalBars ) );

        //create 2 representation for it
        F<Fraction, PNode> representationA = representations.get( random.nextInt( representations.size() ) );
        final Cell cellA = cells.get( random.nextInt( cells.size() ) );
        MovableFraction fractionA = fraction( f, cellA, representationA );

        //Don't use the same representation for the 2nd one, and put it in a new cell
        representations.remove( representationA );
        cells.remove( cellA );

        final Cell cellB = cells.get( random.nextInt( cells.size() ) );
        F<Fraction, PNode> representationB = representations.get( random.nextInt( representations.size() ) );
        MovableFraction fractionB = fraction( f, cellB, representationB );

        cells.remove( cellB );

        return new Pair<MovableFraction, MovableFraction>( fractionA, fractionB );
    }

    private static MovableFraction fraction( Fraction fraction, Cell cell, final F<Fraction, PNode> node ) {
        return fraction( fraction.numerator, fraction.denominator, cell, node );
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