
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactionChoiceNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;


public class RealReactionCanvas extends RPALCanvas {

    private ReactionChoiceNode reactionChoiceNode;
    
    public RealReactionCanvas() {
        super();
        
        //XXX
        PText underConstructionNode = new PText( "Under Construction" );
        underConstructionNode.setFont( new PhetFont( 30 ) );
        underConstructionNode.scale( 2 );
        addChild( underConstructionNode );
        
        reactionChoiceNode = new ReactionChoiceNode();
        reactionChoiceNode.scale( 1.25 );
        addChild( reactionChoiceNode );
        
        //XXX
        PImage moleculeImage = new PImage( RPALImages.H2O );
        addChild( moleculeImage );
        
        // static layout
        double x = 0;
        double y = 0;
        underConstructionNode.setOffset( x, y );
        x = underConstructionNode.getFullBoundsReference().getCenterX() - ( reactionChoiceNode.getFullBoundsReference().getWidth() / 2 );
        y = underConstructionNode.getFullBoundsReference().getMaxY() + 20;
        reactionChoiceNode.setOffset( x, y );
        x = reactionChoiceNode.getFullBoundsReference().getCenterX() - ( moleculeImage.getFullBoundsReference().getWidth() / 2 );
        y = reactionChoiceNode.getFullBoundsReference().getMaxY() + 20;
        moleculeImage.setOffset( x, y );
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        //XXX lay out nodes
        
        centerRootNode();
    }
}
