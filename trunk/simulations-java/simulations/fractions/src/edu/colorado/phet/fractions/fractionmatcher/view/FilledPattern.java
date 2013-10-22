// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.F;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionmatcher.model.Pattern;

import static edu.colorado.phet.fractions.common.util.DefaultP2.p2;

/**
 * Pattern with an indication of which shapes should be filled in.
 *
 * @author Sam Reid
 */
public @Data class FilledPattern {

    //List of the shapes with a flag for each indicating whether filled
    public final List<P2<Shape, Boolean>> shapes;

    //Outline of the entire container
    public final Shape outline;

    //Type for comparisons (to make sure different types are used)
    public final PatternType type;

    //Pattern that fills sequentially (from the left)
    public static FilledPattern sequentialFill( Pattern pattern, final int numFilled ) {
        return new FilledPattern( pattern.shapes.zipIndex().map( new F<P2<Shape, Integer>, P2<Shape, Boolean>>() {
            @Override public P2<Shape, Boolean> f( final P2<Shape, Integer> p ) {
                return p2( p._1(), p._2() < numFilled );
            }
        } ), pattern.outline, pattern.type );
    }

    //Pattern that fills randomly.
    public static FilledPattern randomFill( Pattern pattern, final int numFilled, final long seed ) {
        int numElements = pattern.shapes.length();

        final List<Integer> elm = List.iterableList( new ArrayList<Integer>( List.range( 0, numElements ).toCollection() ) {{
            Collections.shuffle( this, new Random( seed ) );
        }} ).take( numFilled );

        return new FilledPattern( pattern.shapes.zipIndex().map( new F<P2<Shape, Integer>, P2<Shape, Boolean>>() {
            @Override public P2<Shape, Boolean> f( final P2<Shape, Integer> p ) {
                return p2( p._1(), elm.toCollection().contains( p._2() ) );
            }
        } ), pattern.outline, pattern.type );
    }

    public Boolean isCompletelyFilled() {
        final List<P2<Shape, Boolean>> filledShapes = shapes.filter( new F<P2<Shape, Boolean>, Boolean>() {
            @Override public Boolean f( final P2<Shape, Boolean> shapeBooleanP2 ) {
                return shapeBooleanP2._2();
            }
        } );
        return filledShapes.length() == shapes.length();
    }

    public Fraction toFraction() {
        return new Fraction( shapes.filter( new F<P2<Shape, Boolean>, Boolean>() {
            @Override public Boolean f( final P2<Shape, Boolean> shapeBooleanP2 ) {
                return shapeBooleanP2._2();
            }
        } ).length(), shapes.length() );
    }

    public Integer getDenominator() {
        return shapes.length();
    }
}