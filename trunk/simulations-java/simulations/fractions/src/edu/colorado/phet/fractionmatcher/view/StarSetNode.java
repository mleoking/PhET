package edu.colorado.phet.fractionmatcher.view;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionmatcher.model.GameResult;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;
import static fj.Ord.intOrd;
import static fj.data.List.list;

//REVIEW This looks brute-force and brittle. What will happen if something changes elsewhere that allows the score to be > 12 ? At the very least, add assertions to verify your assumptions.
/**
 * Node that shows the number of stars the user attained.  There are 3 stars for each level and each level is worth 12 points,
 * so each star can show up to 4 points (by filling up from the bottom).
 *
 * @author Sam Reid
 */
public class StarSetNode extends PNode {
    public StarSetNode( final Property<List<GameResult>> gameResults, final int levelName ) {
        final BufferedImage star0 = multiScaleToWidth( STAR_0, 30 );
        final BufferedImage star1 = multiScaleToWidth( STAR_1, 30 );
        final BufferedImage star2 = multiScaleToWidth( STAR_2, 30 );
        final BufferedImage star3 = multiScaleToWidth( STAR_3, 30 );
        final BufferedImage star4 = multiScaleToWidth( STAR_4, 30 );

        gameResults.addObserver( new VoidFunction1<List<GameResult>>() {
            public void apply( final List<GameResult> gameResults ) {
                removeAllChildren();
                int score = getBestScore( gameResults );
                List<BufferedImage> images = score == 0 ? list( star0, star0, star0 ) :
                                             score == 1 ? list( star1, star0, star0 ) :
                                             score == 2 ? list( star2, star0, star0 ) :
                                             score == 3 ? list( star3, star0, star0 ) :
                                             score == 4 ? list( star4, star0, star0 ) :
                                             score == 5 ? list( star4, star1, star0 ) :
                                             score == 6 ? list( star4, star2, star0 ) :
                                             score == 7 ? list( star4, star3, star0 ) :
                                             score == 8 ? list( star4, star4, star0 ) :
                                             score == 9 ? list( star4, star4, star1 ) :
                                             score == 10 ? list( star4, star4, star2 ) :
                                             score == 11 ? list( star4, star4, star3 ) :
                                             score == 12 ? list( star4, star4, star4 ) :
                                             null;
                if ( images == null ) { throw new RuntimeException( "No images found for score: " + score ); }
                addChild( new ControlPanelNode( new HBox( new PImage( images.index( 0 ) ), new PImage( images.index( 1 ) ), new PImage( images.index( 2 ) ) ), Color.white ) );
            }

            //Find the best score the user attained for a level.
            private int getBestScore( final List<GameResult> gameResults ) {
                final List<GameResult> scoresForThisLevel = gameResults.filter( new F<GameResult, Boolean>() {
                    @Override public Boolean f( final GameResult g ) {
                        return g.level == levelName;
                    }
                } );
                return scoresForThisLevel.length() == 0 ? 0 :
                       scoresForThisLevel.map( new F<GameResult, Integer>() {
                           @Override public Integer f( final GameResult g ) {
                               return g.score;
                           }
                       } ).maximum( intOrd );
            }
        } );
    }
}