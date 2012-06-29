// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.STAR_0;
import static edu.colorado.phet.fractions.FractionsResources.Images.STAR_4;
import static fj.data.List.list;

/**
 * Node that shows the number of stars the user attained.  There are 3 stars for each level and each level is worth 12 points,
 * so each star can show up to 4 points (by filling up from the bottom).
 *
 * @author Sam Reid
 */
public class StarSetNode2 extends PNode {
    public StarSetNode2( int numStars, int maxStars ) {
        final BufferedImage star0 = multiScaleToWidth( STAR_0, 30 );
//        final BufferedImage star1 = multiScaleToWidth( STAR_1, 30 );
//        final BufferedImage star2 = multiScaleToWidth( STAR_2, 30 );
//        final BufferedImage star3 = multiScaleToWidth( STAR_3, 30 );
        final BufferedImage star4 = multiScaleToWidth( STAR_4, 30 );

        List<BufferedImage> images = numStars == 0 ? list( star0, star0, star0 ) :
                                     numStars == 1 ? list( star4, star0, star0 ) :
                                     numStars == 2 ? list( star4, star4, star0 ) :
                                     numStars == 3 ? list( star4, star4, star4 ) :
                                     null;
        if ( images == null ) { throw new RuntimeException( "No images found for score: " + numStars ); }
        addChild( new ControlPanelNode( new HBox( new PImage( images.index( 0 ) ), new PImage( images.index( 1 ) ), new PImage( images.index( 2 ) ) ), Color.white, new BasicStroke( 1 ), Color.darkGray ) );
    }

    //Find the best score the user attained for a level.
//    private int getBestScore( final List<GameResult> gameResults ) {
//        final List<GameResult> scoresForThisLevel = gameResults.filter( new F<GameResult, Boolean>() {
//            @Override public Boolean f( final GameResult g ) {
//                return g.level == levelName;
//            }
//        } );
//        return scoresForThisLevel.length() == 0 ? 0 :
//               scoresForThisLevel.map( new F<GameResult, Integer>() {
//                   @Override public Integer f( final GameResult g ) {
//                       return g.score;
//                   }
//               } ).maximum( intOrd );
//    }
}