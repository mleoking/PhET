// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collection;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.STAR_0;
import static edu.colorado.phet.fractions.FractionsResources.Images.STAR_4;
import static fj.data.List.nil;

/**
 * Node that shows the number of stars the user attained.  There are 3 stars for each level and each level is worth 12 points,
 * so each star can show up to 4 points (by filling up from the bottom).
 *
 * @author Sam Reid
 */
public class StarSetNode2 extends PNode {
    public StarSetNode2( int numStars, int maxStars ) {
        final int size = maxStars <= 3 ? 30 : 25;
        final BufferedImage empty = multiScaleToWidth( STAR_0, size );
        final BufferedImage filled = multiScaleToWidth( STAR_4, size );

        List<BufferedImage> images = nil();
        for ( int i = 0; i < maxStars; i++ ) {
            images = images.snoc( i >= numStars ? empty : filled );
        }
        List<PImage> pImages = images.map( new F<BufferedImage, PImage>() {
            @Override public PImage f( final BufferedImage bufferedImage ) {
                return new PImage( bufferedImage );
            }
        } );
        final Collection<PImage> collection = pImages.toCollection();
        addChild( new ControlPanelNode( new HBox( 4, collection.toArray( new PNode[collection.size()] ) ), Color.white, new BasicStroke( 1 ), Color.darkGray ) );
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