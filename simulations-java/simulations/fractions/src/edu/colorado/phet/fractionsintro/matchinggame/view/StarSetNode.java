package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.matchinggame.model.GameResult;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * @author Sam Reid
 */
public class StarSetNode extends PNode {
    public StarSetNode( final Property<List<GameResult>> gameResults, final int levelName ) {
        final BufferedImage star0 = BufferedImageUtils.multiScaleToWidth( STAR_0, 30 );
        final BufferedImage star1 = BufferedImageUtils.multiScaleToWidth( STAR_1, 30 );
        final BufferedImage star2 = BufferedImageUtils.multiScaleToWidth( STAR_2, 30 );
        final BufferedImage star3 = BufferedImageUtils.multiScaleToWidth( STAR_3, 30 );
        final BufferedImage star4 = BufferedImageUtils.multiScaleToWidth( STAR_4, 30 );

        gameResults.addObserver( new VoidFunction1<List<GameResult>>() {
            public void apply( final List<GameResult> gameResults ) {
                removeAllChildren();
                int score = getScore( gameResults );
                //never played = 0 stars
                //less than half points = 1 star
                //equal or more than half = 2 stars
                //full points = 3 stars
                List<BufferedImage> images = score == 0 ? List.list( star0, star0, star0 ) :
                                             score == 1 ? List.list( star1, star0, star0 ) :
                                             score == 2 ? List.list( star2, star0, star0 ) :
                                             score == 3 ? List.list( star3, star0, star0 ) :
                                             score == 4 ? List.list( star4, star0, star0 ) :
                                             score == 5 ? List.list( star4, star1, star0 ) :
                                             score == 6 ? List.list( star4, star2, star0 ) :
                                             score == 7 ? List.list( star4, star3, star0 ) :
                                             score == 8 ? List.list( star4, star4, star0 ) :
                                             score == 9 ? List.list( star4, star4, star1 ) :
                                             score == 10 ? List.list( star4, star4, star2 ) :
                                             score == 11 ? List.list( star4, star4, star3 ) :
                                             score == 12 ? List.list( star4, star4, star4 ) :
                                             null;
                addChild( new ControlPanelNode( new HBox( new PImage( images.index( 0 ) ), new PImage( images.index( 1 ) ), new PImage( images.index( 2 ) ) ), Color.white ) );
            }

            private int getScore( final List<GameResult> gameResults ) {

                final List<GameResult> scoresForThisLevel = gameResults.filter( new F<GameResult, Boolean>() {
                    @Override public Boolean f( final GameResult gameResult ) {
                        return gameResult.level == levelName;
                    }
                } );
                if ( scoresForThisLevel.length() == 0 ) {
                    return 0;
                }
                int bestScoreForThisLevel = scoresForThisLevel.
                        map( new F<GameResult, Integer>() {
                            @Override public Integer f( final GameResult gameResult ) {
                                return gameResult.score;
                            }
                        } ).
                        maximum( Ord.intOrd );

                return bestScoreForThisLevel;
            }
        } );
    }
}