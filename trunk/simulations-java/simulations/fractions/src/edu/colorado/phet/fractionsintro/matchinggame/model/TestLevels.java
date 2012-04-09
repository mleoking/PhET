// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.Equal;
import fj.F;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;

import static fj.data.List.list;
import static fj.data.List.range;

/**
 * @author Sam Reid
 */
public class TestLevels {
    public static void main( String[] args ) {
        Levels Levels = edu.colorado.phet.fractionsintro.matchinggame.model.Levels.Levels;
        //Make sure no fractions above 2.0
        for ( Fraction[] fractions : list( Levels.level1Fractions, Levels.level2Fractions, Levels.level3Fractions, Levels.level4Fractions ) ) {
            for ( Fraction fraction : fractions ) {
                if ( fraction.toDouble() > 2.0 ) {
                    System.out.println( "fraction = " + fraction );
                }
            }
        }

        for ( int i = 0; i < 1000; i++ ) {
            testLevel( 1 );
        }
        for ( int i = 0; i < 1000; i++ ) {
            testLevel( 2 );
        }
        for ( int i = 0; i < 1000; i++ ) {
            testLevel( 3 );
        }
        for ( int i = 0; i < 1000; i++ ) {
            testLevel( 4 );
        }
    }

    private static void testLevel( int level ) {
        Levels levels = edu.colorado.phet.fractionsintro.matchinggame.model.Levels.Levels;
        List<MovableFraction> fractions = levels.get( level ).f( range( 1, 7 ).map( new F<Integer, Cell>() {
            @Override public Cell f( final Integer i ) {
                return new Cell( new ImmutableRectangle2D( i * 100, i * 100, 100, 100 ), i, i );
            }
        } ) );

        //Make sure no two from the same group
        List<F<Fraction, PNode>> representation = fractions.map( new F<MovableFraction, F<Fraction, PNode>>() {
            @Override public F<Fraction, PNode> f( final MovableFraction movableFraction ) {
                return movableFraction.node;
            }
        } );
        for ( F<Fraction, PNode> f : representation ) {
            List<F<Fraction, PNode>> g = representation.delete( f, Equal.<F<Fraction, PNode>>anyEqual() );
        }
    }
}