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
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static fj.Ord.intOrd;
import static fj.data.List.list;

/**
 * Node that shows the number of stars the user attained.  There are 3 stars for each level and each level is worth 12 points,
 * so each star can show up to 4 points (by filling up from the bottom).
 *
 * @author Sam Reid
 */
public class StarSetNode extends PNode {

    //Images fill up with 4 segments each, here are the 5 pictures (including empty star) in order of filling up.
    private static final List<BufferedImage> STAR_IMAGES = list( multiScaleToWidth( Images.STAR_0, 30 ),
                                                                 multiScaleToWidth( Images.STAR_1, 30 ),
                                                                 multiScaleToWidth( Images.STAR_2, 30 ),
                                                                 multiScaleToWidth( Images.STAR_3, 30 ),
                                                                 multiScaleToWidth( Images.STAR_4, 30 ) );

    private static BufferedImage getImage( final int numberStarPieces ) {
        return STAR_IMAGES.index( clamp( 0, numberStarPieces, 4 ) );
    }

    public StarSetNode( final Property<List<GameResult>> gameResults, final int levelName ) {
        gameResults.addObserver( new VoidFunction1<List<GameResult>>() {
            public void apply( final List<GameResult> gameResults ) {
                removeAllChildren();
                int score = getBestScore( gameResults );

                int piecesPerStar = 4;

                //One star segment (piece of a star) for each point the user attained
                int numberPieces = score;

                List<BufferedImage> images = list( getImage( numberPieces - piecesPerStar * 0 ),
                                                   getImage( numberPieces - piecesPerStar * 1 ),
                                                   getImage( numberPieces - piecesPerStar * 2 ) );
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