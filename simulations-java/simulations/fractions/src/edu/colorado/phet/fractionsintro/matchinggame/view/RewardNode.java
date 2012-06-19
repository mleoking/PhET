package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.image.BufferedImage;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * @author Sam Reid
 */
public class RewardNode extends PNode {
    public RewardNode( final MatchingGameModel model ) {
        model.mode.addObserver( new VoidFunction1<Mode>() {
            public void apply( final Mode mode ) {
                final boolean visible = mode == Mode.SHOWING_GAME_OVER_SCREEN && model.state.get().info.score == 12;
                removeAllChildren();
                if ( visible ) {
                    final ContentNode child = new ContentNode( model );
                    addChild( child );

                    //Animation only works if starting it after added to scene graph
                    child.startAnimation();
                }
            }
        } );
    }

    private static class ContentNode extends PNode {
        private final MatchingGameModel model;

        public ContentNode( final MatchingGameModel model ) {
            this.model = model;
        }

        public void startAnimation() {
            List<BufferedImage> images = model.state.get().fractions.map( new F<MovableFraction, BufferedImage>() {
                @Override public BufferedImage f( final MovableFraction movableFraction ) {
                    return toBufferedImage( movableFraction.node.toImage() );
                }
            } );

            double maxHeight = maxHeight( images );

            double x = 0;
            double maxX = AbstractFractionsCanvas.STAGE_SIZE.width;
            double dx = ( maxX - x ) / ( images.length() );
            double y = 0;
            double dy = dx;
            Random random = new Random();
            for ( int i = 0; i < 20; i++ ) {
                for ( BufferedImage node : images ) {
                    PImage image = new PImage( node );
                    final double myY = -maxHeight - image.getFullBounds().getHeight() / 2 + y;
                    image.setOffset( x, myY );
                    addChild( image );
                    final double h = AbstractFractionsCanvas.STAGE_SIZE.getHeight();
                    image.animateToPositionScaleRotation( x, h * ( 0.8 + random.nextDouble() ), random.nextDouble() + 0.5, 2 * ( random.nextDouble() - 0.5 ) * Math.PI, 5000 + random.nextInt( 5000 ) );
                    image.setTransparency( 0 );
                    image.animateToTransparency( 1, 2000 );
                    x += dx;
                }
                x = 0;
                y -= dy;
            }
        }
    }

    private static double maxHeight( final List<BufferedImage> images ) {
        List<PNode> nodes = images.map( new F<BufferedImage, PNode>() {
            @Override public PNode f( final BufferedImage bufferedImage ) {
                return new PImage( bufferedImage );
            }
        } );
        return nodes.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( Ord.doubleOrd );
    }
}