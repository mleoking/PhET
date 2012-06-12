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
import edu.colorado.phet.fractionsintro.matchinggame.model.GameOverScore;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractions.FractionsResources.Images.STAR_GOLD;
import static edu.colorado.phet.fractions.FractionsResources.Images.STAR_GRAY;

/**
 * @author Sam Reid
 */
public class StarSetNode extends PNode {
    public StarSetNode( final Property<List<GameOverScore>> gameOverScores, final int levelName ) {
        final BufferedImage starGray = BufferedImageUtils.multiScaleToWidth( STAR_GRAY, 30 );
        final BufferedImage starGold = BufferedImageUtils.multiScaleToWidth( STAR_GOLD, 30 );

        gameOverScores.addObserver( new VoidFunction1<List<GameOverScore>>() {
            public void apply( final List<GameOverScore> gameOverScores ) {
                removeAllChildren();
                int starCount = getStarCount( gameOverScores );
                //never played = 0 stars
                //less than half points = 1 star
                //equal or more than half = 2 stars
                //full points = 3 stars
                BufferedImage[] images = new BufferedImage[3];
                images[0] = starGray;
                images[1] = starGray;
                images[2] = starGray;
                for ( int i = 0; i < starCount; i++ ) {
                    images[i] = starGold;
                }
                addChild( new ControlPanelNode( new HBox( new PImage( images[0] ), new PImage( images[1] ), new PImage( images[2] ) ), Color.white ) );
            }

            private int getStarCount( final List<GameOverScore> gameOverScores ) {

                final List<GameOverScore> scoresForThisLevel = gameOverScores.filter( new F<GameOverScore, Boolean>() {
                    @Override public Boolean f( final GameOverScore gameOverScore ) {
                        return gameOverScore.level == levelName;
                    }
                } );
                if ( scoresForThisLevel.length() == 0 ) {
                    return 0;
                }
                int bestScoreForThisLevel = scoresForThisLevel.
                        map( new F<GameOverScore, Integer>() {
                            @Override public Integer f( final GameOverScore gameOverScore ) {
                                return gameOverScore.score;
                            }
                        } ).
                        maximum( Ord.intOrd );

                return bestScoreForThisLevel == 12 ? 3 :
                       bestScoreForThisLevel < 6 ? 1 :
                       2;
            }
        } );
    }
}