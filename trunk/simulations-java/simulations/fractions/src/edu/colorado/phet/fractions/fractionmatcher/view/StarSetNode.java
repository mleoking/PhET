// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.fractionmatcher.model.GameResult;
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
class StarSetNode extends PNode {

    private static final int PIECES_PER_STAR = 4;
    private static final int STAR_WIDTH = 30;

    //Images fill up with 4 segments each, here are the 5 pictures (including empty star) in order of filling up.
    private static final List<BufferedImage> STAR_IMAGES = list( multiScaleToWidth( Images.STAR_0, STAR_WIDTH ),
                                                                 multiScaleToWidth( Images.STAR_1, STAR_WIDTH ),
                                                                 multiScaleToWidth( Images.STAR_2, STAR_WIDTH ),
                                                                 multiScaleToWidth( Images.STAR_3, STAR_WIDTH ),
                                                                 multiScaleToWidth( Images.STAR_4, STAR_WIDTH ) );

    private static BufferedImage getImage( final int numberStarPieces ) {
        return STAR_IMAGES.index( clamp( 0, numberStarPieces, PIECES_PER_STAR ) );
    }

    public StarSetNode( final Property<List<GameResult>> gameResults, final int levelName ) {
        gameResults.addObserver( new VoidFunction1<List<GameResult>>() {
            public void apply( final List<GameResult> gameResults ) {
                removeAllChildren();

                //One star segment (piece of a star) for each point the user attained
                final int score = getBestScore( gameResults );
                final int numberPieces = score;

                List<BufferedImage> images = list( getImage( numberPieces - PIECES_PER_STAR * 0 ),
                                                   getImage( numberPieces - PIECES_PER_STAR * 1 ),
                                                   getImage( numberPieces - PIECES_PER_STAR * 2 ) );
                if ( images == null ) {
                    throw new RuntimeException( "No images found for score: " + score );
                }
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