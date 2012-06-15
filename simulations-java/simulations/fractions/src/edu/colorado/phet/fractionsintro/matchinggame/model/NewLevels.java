package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Grid;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.PlusSigns;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Polygon;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Pyramid;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static edu.colorado.phet.fractionsintro.intro.model.Fraction.fraction;
import static edu.colorado.phet.fractionsintro.matchinggame.model.FillType.Mixed;
import static edu.colorado.phet.fractionsintro.matchinggame.model.FillType.Sequential;
import static edu.colorado.phet.fractionsintro.matchinggame.model.ShapeType.*;
import static fj.data.List.iterableList;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class NewLevels {

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

        final List<Fraction> selectedFractions = fractionList.take( 6 );
        ArrayList<Cell> remainingCells = new ArrayList<Cell>( shuffle( _cells ).toCollection() );

        final List<ShapeType> easy = list( pies, horizontalBars, verticalBars );
        final List<ShapeType> medium = list( plusses, grid, pyramid, polygon, tetris, flower );
        final List<GraphicalRepresentation> r =
                level == 1 ? generateAll( easy, list( Sequential ) ) :
                level == 2 || level == 3 ? generateAll( medium.append( easy ), list( Sequential ) ) :
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

            PNode node = null;
            GraphicalRepresentation representation = null;

            //Flag for the primary representation.  Ensure a minimum of 3 numeric representations per stage
            boolean numeric = i < 3 || random.nextBoolean();
            if ( numeric ) {
                node = new FractionNode( fraction, 0.3 );
            }
            //ensure a minimum of 3 shape representations per stage
            else {
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
               s == plusses ? true :
               s == polygon ? d >= 3 :
               s == tetris ? d == 4 :
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

    private PatternNode createGraphic( Fraction fraction, final GraphicalRepresentation representation ) {
        final ShapeType s = representation.shapeType;
        final int d = fraction.denominator;
        final Pattern container = s == pies ? Pattern.pie( d ) :
                                  s == verticalBars ? Pattern.verticalBars( d ) :
                                  s == horizontalBars ? Pattern.horizontalBars( d ) :
                                  s == flower && d == 6 ? Pattern.sixFlower() :
                                  s == tetris && d == 4 ? Pattern.tetrisPiece( 50 ) :
                                  s == plusses ? new PlusSigns( d ) :
                                  s == polygon ? Polygon.create( 80, d ) :
                                  s == pyramid && d == 4 ? Pyramid.four() :
                                  s == pyramid && d == 9 ? Pyramid.nine() :
                                  s == grid && d == 4 ? new Grid( 2 ) :
                                  s == grid && d == 9 ? new Grid( 3 ) :
                                  null;
        if ( container == null ) {
            throw new RuntimeException( "Null pattern for rep = " + representation );
        }
        return new PatternNode( FilledPattern.sequentialFill( container, fraction.numerator ), representation.color );
    }

    private static <T> List<T> shuffle( final List<T> cells ) {
        ArrayList<T> c = new ArrayList<T>( cells.toCollection() );
        Collections.shuffle( c );
        return iterableList( c );
    }
}