package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Grid;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.PlusSigns;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Polygon;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Pyramid;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.fraction;
import static edu.colorado.phet.fractionsintro.matchinggame.model.FillType.Mixed;
import static edu.colorado.phet.fractionsintro.matchinggame.model.FillType.Sequential;
import static edu.colorado.phet.fractionsintro.matchinggame.model.ShapeType.*;
import static edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern.randomFill;
import static edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern.sequentialFill;
import static fj.data.List.*;

/**
 * @author Sam Reid
 */
public class Levels {

    /**
     * Level 1
     * No mixed numbers
     * Only “exact” matches will be present.  So for instance if there is a 3/6  and a pie with 6 divisions and 3 shaded slices, there will not be a ½  present .  In other words, the numerical representation on this level will exactly match the virtual manipulative.
     * Only numbers/representations  ≦ 1 possible on this level
     * “Easy” shapes on this level (not some of the more abstract representations)
     */
    public static final List<Fraction> level1Fraction = list( fraction( 1, 3 ),
                                                              fraction( 2, 3 ),
                                                              fraction( 1, 4 ),
                                                              fraction( 3, 4 ),
                                                              fraction( 1, 2 ),
                                                              fraction( 1, 1 ) );

    /**
     * Level 2
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    public static final List<Fraction> level2Fractions = list( fraction( 1, 2 ),
                                                               fraction( 2, 4 ),
                                                               fraction( 3, 4 ),
                                                               fraction( 1, 3 ),
                                                               fraction( 2, 3 ),
                                                               fraction( 3, 6 ),
                                                               fraction( 2, 6 ) );

    /**
     * Level 3:
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    public static final List<Fraction> level3Fraction = list( fraction( 3, 2 ),
                                                              fraction( 4, 3 ),
                                                              fraction( 6, 3 ),
                                                              fraction( 4, 2 ),
                                                              fraction( 7, 6 ),
                                                              fraction( 4, 5 ),
                                                              fraction( 7, 4 ),
                                                              fraction( 5, 4 ),
                                                              fraction( 6, 4 ),
                                                              fraction( 5, 6 ),
                                                              fraction( 4, 6 ),
                                                              fraction( 3, 6 ),
                                                              fraction( 2, 6 ),
                                                              fraction( 3, 8 ),
                                                              fraction( 4, 8 ),
                                                              fraction( 5, 8 ),
                                                              fraction( 6, 8 ),
                                                              fraction( 7, 8 ) );

    /**
     * Level 4:
     * All representations possible as well as complicated mixed/improper numbers
     */
    public static final List<Fraction> level4Fraction = list( fraction( 13, 7 ),
                                                              fraction( 13, 7 ),
                                                              fraction( 14, 8 ),
                                                              fraction( 9, 5 ),
                                                              fraction( 6, 3 ),
                                                              fraction( 9, 8 ),
                                                              fraction( 8, 9 ),
                                                              fraction( 6, 9 ),
                                                              fraction( 4, 9 ),
                                                              fraction( 3, 9 ),
                                                              fraction( 2, 9 ),
                                                              fraction( 9, 7 ) );

    /**
     * Level 5:
     * All representations possible as well as complicated mixed/improper numbers
     */
    public static final List<Fraction> level5Fractions = list( fraction( 13, 7 ),
                                                               fraction( 13, 7 ),
                                                               fraction( 14, 8 ),
                                                               fraction( 9, 5 ),
                                                               fraction( 6, 3 ),
                                                               fraction( 9, 8 ),
                                                               fraction( 8, 9 ),
                                                               fraction( 6, 9 ),
                                                               fraction( 4, 9 ),
                                                               fraction( 3, 9 ),
                                                               fraction( 2, 9 ),
                                                               fraction( 9, 7 ) );

    /**
     * Level 6:
     * All representations possible as well as complicated mixed/improper numbers
     */
    public static final List<Fraction> level6Fractions = list( fraction( 9, 5 ),
                                                               fraction( 8, 5 ),
                                                               fraction( 7, 5 ),
                                                               fraction( 6, 5 ),
                                                               fraction( 7, 6 ),
                                                               fraction( 8, 6 ),
                                                               fraction( 9, 6 ),
                                                               fraction( 9, 7 ),
                                                               fraction( 10, 7 ),
                                                               fraction( 13, 7 ),
                                                               fraction( 9, 8 ),
                                                               fraction( 10, 8 ),
                                                               fraction( 11, 8 ),
                                                               fraction( 14, 8 ),
                                                               fraction( 4, 9 ),
                                                               fraction( 6, 9 ),
                                                               fraction( 8, 9 ),
                                                               fraction( 10, 9 ),
                                                               fraction( 11, 9 ) );

    private final Random random = new Random();

    static @Data class GraphicalRepresentation {
        public final ShapeType shapeType;
        public final Color color;
        public final FillType fillType;
    }

    public List<MovableFraction> createLevel( final int level, final List<Cell> _cells ) {
        final List<Fraction> fractionList = shuffle( level == 1 ? level1Fraction :
                                                     level == 2 ? level2Fractions :
                                                     level == 3 ? level3Fraction :
                                                     level == 4 ? level4Fraction :
                                                     level == 5 ? level5Fractions :
                                                     level == 6 ? level6Fractions :
                                                     List.<Fraction>nil() );

        final List<Integer> numericScaleFactors = level < 5 ? single( 1 ) :
                                                  level == 5 ? list( 1, 2, 3 ) :
                                                  level == 6 ? list( 1, 4, 5 ) :
                                                  level == 7 ? list( 1, 6, 7 ) :
                                                  level == 8 ? list( 1, 8, 9 ) :
                                                  List.<Integer>nil();
        final List<Fraction> selectedFractions = fractionList.take( 6 );
        ArrayList<Cell> remainingCells = new ArrayList<Cell>( shuffle( _cells ).toCollection() );

        final List<ShapeType> easy = list( pies, horizontalBars, verticalBars );
        final List<ShapeType> medium = list( plusses, grid, pyramid, polygon, tetris, flower, letterLShapes ).append( easy );
        final List<GraphicalRepresentation> r =
                level == 1 ? generateAll( easy, list( Sequential ) ) :
                level == 2 || level == 3 ? generateAll( medium, list( Sequential ) ) :
                level == 4 ? generateAll( medium, list( Sequential ) ) :
                level == 5 ? generateAll( medium, list( Sequential, Mixed ) ) :
                level == 6 ? generateAll( medium, list( Sequential, Mixed, FillType.Random ) ) :
                null;
        ArrayList<GraphicalRepresentation> representations = new ArrayList<GraphicalRepresentation>( r.toCollection() );

        //for each cell, create a MovableFraction
        ArrayList<MovableFraction> result = new ArrayList<MovableFraction>();
        for ( int i = 0; i < selectedFractions.length(); i++ ) {

            //choose one of the fractions at random, but don't repeat it.
            final Fraction fraction = selectedFractions.index( i % 6 );

            ArrayList<GraphicalRepresentation> allowedRepresentations = filter( representations, fraction );
            Collections.shuffle( allowedRepresentations );

            PNode node;
            GraphicalRepresentation representation = null;

            //Flag for the primary representation.  Ensure a minimum of 3 numeric representations per stage
            boolean numeric = i < 3 || random.nextBoolean();
            if ( numeric ) {
                int scaleFactor = numericScaleFactors.index( random.nextInt( numericScaleFactors.length() ) );
                node = new FractionNode( new Fraction( fraction.numerator * scaleFactor, fraction.denominator * scaleFactor ), 0.3 );
            }
            //ensure a minimum of 3 shape representations per stage
            else {
                if ( allowedRepresentations.size() == 0 ) { allowedRepresentations = filter( new ArrayList<GraphicalRepresentation>( r.toCollection() ), fraction ); }
                representation = allowedRepresentations.get( 0 );
                node = createGraphic( fraction, representation );
                representations.remove( representation );
                allowedRepresentations.remove( representation );
            }

            {
                Cell cell = remainingCells.remove( 0 );
                result.add( new MovableFraction( MovableFraction.nextID(), cell.position(), fraction.numerator, fraction.denominator,
                                                 false, cell, 1.0, new RichPNode( node ), Motions.Stillness, false, null,
                                                 numeric ? Color.black : representation.color,
                                                 numeric ? "numeric" : representation.toString() ) );
            }

            //If fraction is numeric, partner must not also be numeric.
            {
                if ( allowedRepresentations.size() == 0 ) { allowedRepresentations = filter( new ArrayList<GraphicalRepresentation>( r.toCollection() ), fraction ); }
                GraphicalRepresentation alternateRepresentation = allowedRepresentations.get( 0 );
                node = createGraphic( fraction, alternateRepresentation );
                representations.remove( alternateRepresentation );

                Cell cell = remainingCells.remove( 0 );

                result.add( new MovableFraction( MovableFraction.nextID(), cell.position(), fraction.numerator, fraction.denominator,
                                                 false, cell, 1.0, new RichPNode( node ), Motions.Stillness, false, null,
                                                 alternateRepresentation.color,
                                                 alternateRepresentation.toString() ) );
            }
        }
        return iterableList( result );
    }

    //Choose only the representations that match a fraction.
    private ArrayList<GraphicalRepresentation> filter( final ArrayList<GraphicalRepresentation> representations, final Fraction fraction ) {
        ArrayList<GraphicalRepresentation> filtered = new ArrayList<GraphicalRepresentation>();
        for ( GraphicalRepresentation representation : representations ) {
            if ( matches( representation.shapeType, fraction ) ) { filtered.add( representation ); }
        }
        return filtered;
    }

    //numeric, horizontalBars, verticalBars, pies, plusses, grid, pyramid,
    //polygon, tetris, flower

    private boolean matches( final ShapeType s, final Fraction fraction ) {
        final int d = fraction.denominator;
        return s == pies ? true :
               s == horizontalBars ? true :
               s == verticalBars ? true :
               s == grid ? ( d == 4 || d == 9 ) :
               s == flower ? d == 6 :
               s == pyramid ? ( d == 1 || d == 4 || d == 9 ) :
               s == plusses ? d == 6 :
               s == polygon ? d >= 3 :
               s == tetris ? d == 4 :
               s == letterLShapes ? d % 2 == 0 :
               false;
    }

    private List<GraphicalRepresentation> generateAll( final List<ShapeType> shapes, final List<FillType> fills ) {
        ArrayList<GraphicalRepresentation> all = new ArrayList<GraphicalRepresentation>();
        for ( ShapeType shape : shapes ) {
            for ( Color color : list( LIGHT_BLUE, LIGHT_GREEN, LIGHT_RED ) ) {
                for ( FillType fill : fills ) {
                    all.add( new GraphicalRepresentation( shape, color, fill ) );
                }
            }
        }
        return iterableList( all );
    }

    private PNode createGraphic( Fraction f, final GraphicalRepresentation r ) {
        if ( f.numerator <= f.denominator ) {
            final PatternNode single = createSingle( f, r.shapeType, r.fillType == FillType.Random, r.color );
            HBox box = new HBox( single );
            scaleHBox( box, 80 );
            return box;
        }
        else {
            int numShapes = (int) Math.ceil( f.toDouble() );
            if ( numShapes >= 3 ) {
                throw new RuntimeException( "3+ not handled yet" );
            }

            if ( r.fillType == Sequential || r.fillType == Mixed ) {
                PatternNode first = createSingle( new Fraction( f.denominator, f.denominator ), r.shapeType, false, r.color );
                PatternNode second = createSingle( new Fraction( f.numerator - f.denominator, f.denominator ), r.shapeType, r.fillType == Mixed, r.color );

                final HBox box = new HBox( first, second );
                scaleHBox( box, 110.0 );
                return box;
            }
            else {
                int numInFirst = random.nextInt( f.numerator );
                int numInSecond = f.numerator - numInFirst;
                PatternNode first = createSingle( new Fraction( numInFirst, f.denominator ), r.shapeType, true, r.color );
                PatternNode second = createSingle( new Fraction( numInSecond, f.denominator ), r.shapeType, true, r.color );

                final HBox box = new HBox( first, second );
                scaleHBox( box, 110.0 );
                return box;
            }

        }
    }

    private void scaleHBox( final HBox box, double newWidth ) {
//        Scale to a size of 110 so it will be a good fit for the starting cells and score cells
        double size = box.getFullWidth();
        final double scale = newWidth / size;

        double size2 = box.getFullHeight();
        final double scale2 = newWidth / size2;

        box.scale( Math.min( scale, scale2 ) );

        if ( scale < 1 ) {
            //if the objects got scaled down, then scale up the strokes so they will look like they have the same width
            for ( PNode child : box.getChildren() ) {
                if ( child instanceof PatternNode ) {
                    PatternNode patternNode = (PatternNode) child;
                    patternNode.scaleStrokes( 1.0 / scale );
                }
            }
        }
    }

    private PatternNode createSingle( final Fraction fraction, ShapeType s, boolean random, Color color ) {
        final int d = fraction.denominator;
        final Pattern container = s == pies ? Pattern.pie( d ) :
                                  s == verticalBars ? Pattern.verticalBars( d ) :
                                  s == horizontalBars ? Pattern.horizontalBars( d ) :
                                  s == flower && d == 6 ? Pattern.sixFlower() :
                                  s == tetris && d == 4 ? Pattern.tetrisPiece( 50 ) :
                                  s == plusses && d == 6 ? new PlusSigns( d ) :
                                  s == polygon ? Polygon.create( 80, d ) :
                                  s == pyramid && d == 1 ? Pyramid.single() :
                                  s == pyramid && d == 4 ? Pyramid.four() :
                                  s == pyramid && d == 9 ? Pyramid.nine() :
                                  s == grid && d == 4 ? new Grid( 2 ) :
                                  s == grid && d == 9 ? new Grid( 3 ) :
                                  s == letterLShapes && d % 2 == 0 ? Pattern.letterLShapedDiagonal( 14, d / 2 ) :
                                  null;
        if ( container == null ) {
            throw new RuntimeException( "Null pattern for rep = " + s + ", f = " + fraction );
        }
        return new PatternNode( random ? randomFill( container, fraction.numerator, 123 ) :
                                sequentialFill( container, fraction.numerator ), color );
    }

    private static <T> List<T> shuffle( final List<T> cells ) {
        ArrayList<T> c = new ArrayList<T>( cells.toCollection() );
        Collections.shuffle( c );
        return iterableList( c );
    }
}