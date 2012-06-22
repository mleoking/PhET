package edu.colorado.phet.fractionmatcher.view;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.image.BufferedImage;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractionmatcher.model.Mode;
import edu.colorado.phet.fractionmatcher.model.MovableFraction;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * Reward node that is shown when the user gets a perfect score.  It animates the fractions as falling and piling up at the
 * bottom of the screen.
 *
 * @author Sam Reid
 */
public class RewardNode extends PNode {
    public RewardNode( final MatchingGameModel model ) {

        //Updates when the model mode changes
        model.mode.addObserver( new VoidFunction1<Mode>() {
            public void apply( final Mode mode ) {
                final boolean visible = mode == Mode.SHOWING_GAME_OVER_SCREEN && model.state.get().info.score == 12;
                removeAllChildren();
                if ( visible ) {
                    final ContentNode child = new ContentNode( model );
                    addChild( child );

                    //Animation only works if starting it after added to scene graph
                    child.init();
                }
            }
        } );
    }

    //Content to be shown in the reward mode.
    private static class ContentNode extends PNode {
        private final MatchingGameModel model;

        public ContentNode( final MatchingGameModel model ) {
            this.model = model;
        }

        //Creation done in the init method because animation only works if starting it after added to the scene graph.
        public void init() {
            List<BufferedImage> images = model.state.get().fractions.map( new F<MovableFraction, BufferedImage>() {
                @Override public BufferedImage f( final MovableFraction m ) {
                    return toBufferedImage( m.node.toImage() );
                }
            } );
            double maxHeight = maxHeight( images );

            //Compute the layout metrics to layout as a grid above the screen, then fade in and fall to the bottom.
            double x = 0;
            double maxX = AbstractFractionsCanvas.STAGE_SIZE.width;
            double dx = ( maxX - x ) / ( images.length() );
            double y = 0;
            double dy = dx;
            Random random = new Random();

            //Create rows of images above the screen and have them fall to the ground.
            for ( int i = 0; i < 20; i++ ) {
                for ( BufferedImage node : images ) {
                    PImage image = new PImage( node );
                    image.setOffset( x, -maxHeight - image.getFullBounds().getHeight() / 2 + y );
                    addChild( image );
                    final double h = AbstractFractionsCanvas.STAGE_SIZE.getHeight();
                    image.animateToPositionScaleRotation( x, h + h / 16 + h / 4 * ( random.nextDouble() - 0.5 ) * 2, random.nextDouble() + 0.5, 2 * ( random.nextDouble() - 0.5 ) * Math.PI, 5000 + random.nextInt( 5000 ) );
                    image.setTransparency( 0 );
                    image.animateToTransparency( 1, 2000 );
                    x += dx;
                }
                //Go to the next row above the screen.
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