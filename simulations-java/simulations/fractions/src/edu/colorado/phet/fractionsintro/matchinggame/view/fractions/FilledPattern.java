// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import fj.F;
import fj.P2;
import fj.data.List;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;

import static edu.colorado.phet.fractionsintro.common.util.DefaultP2.p2;

/**
 * Pattern with an indication of which shapes should be filled in.
 *
 * @author Sam Reid
 */
public class FilledPattern {
    public final List<P2<Shape, Boolean>> shapes;

    public FilledPattern( final List<P2<Shape, Boolean>> shapes ) {
        this.shapes = shapes;
    }

    public static FilledPattern sequentialFill( Pattern pattern, final int numFilled ) {
        return new FilledPattern( pattern.shapes.zipIndex().map( new F<P2<Shape, Integer>, P2<Shape, Boolean>>() {
            @Override public P2<Shape, Boolean> f( final P2<Shape, Integer> p ) {
                return p2( p._1(), p._2() < numFilled );
            }
        } ) );
    }

    public static FilledPattern randomFill( Pattern pattern, final int numFilled, final long seed ) {
        int numElements = pattern.shapes.length();

        final List<Integer> elm = List.iterableList( new ArrayList<Integer>( List.range( 0, numElements ).toCollection() ) {{ Collections.shuffle( this, new Random( seed ) ); }} ).take( numFilled );

        return new FilledPattern( pattern.shapes.zipIndex().map( new F<P2<Shape, Integer>, P2<Shape, Boolean>>() {
            @Override public P2<Shape, Boolean> f( final P2<Shape, Integer> p ) {
                return p2( p._1(), elm.toCollection().contains( p._2() ) );
            }
        } ) );
    }
}