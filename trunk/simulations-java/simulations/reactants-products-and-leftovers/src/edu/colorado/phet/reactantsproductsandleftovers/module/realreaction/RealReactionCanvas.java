
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.geom.Dimension2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactionChoiceNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.*;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutStrategy.GridLayoutStrategy;

/**
 * Canvas for the "Real Reaction" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionCanvas extends RPALCanvas {

    private final RealReactionModel model;
    
    // these nodes don't change
    private final ReactionChoiceNode reactionChoiceNode;
    private final RightArrowNode arrowNode;

    // these nodes change based based on reaction
    private RealReactionFormulaNode formulaNode;
    private RealReactionBeforeNode beforeNode;
    private RealReactionAfterNode afterNode;

    public RealReactionCanvas( final RealReactionModel model ) {
        super();

        this.model = model;

        reactionChoiceNode = new ReactionChoiceNode( model );
        reactionChoiceNode.scale( 1.25 );
        addChild( reactionChoiceNode );

        arrowNode = new RightArrowNode();
        addChild( arrowNode );
        
        model.addChangeListeners( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateNodes();
            }
        } );

        updateNodes();
    }


    private void updateNodes() {

        if ( formulaNode != null ) {
            removeChild( formulaNode );
        }
        formulaNode = new RealReactionFormulaNode( model.getReaction() );
        addChild( formulaNode );

        if ( beforeNode != null ) {
            removeChild( beforeNode );
        }
        beforeNode = new RealReactionBeforeNode( model );
        addChild( beforeNode );

        if ( afterNode != null ) {
            removeChild( afterNode );
        }
        afterNode = new RealReactionAfterNode( model );
        addChild( afterNode );

        updateNodesLayout();
    }

    private void updateNodesLayout() {

        // radio buttons at upper left
        double x = 0;
        double y = 0;
        reactionChoiceNode.setOffset( x, y );

        // formula to right of radio buttons, vertically centered with buttons
        x = reactionChoiceNode.getFullBoundsReference().getWidth() + 30;
        y = reactionChoiceNode.getFullBoundsReference().getCenterY() - ( formulaNode.getFullBoundsReference().getHeight() / 2 );
        formulaNode.setOffset( x, y );

        // Before box below radio buttons, left justified
        x = reactionChoiceNode.getFullBoundsReference().getMinX();
        y = reactionChoiceNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );

        // arrow to the right of Before box, vertically centered with box
        final double arrowXSpacing = 20;
        x = beforeNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset() + ( beforeNode.getBoxHeight() / 2 );
        arrowNode.setOffset( x, y );

        // After box to the right of arrow, top aligned with Before box
        x = arrowNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );
    }

    /*
     * Centers the root node on the canvas when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerRootNode();
        }
    }
    
    private static class RealReactionBeforeNode extends AbstractBeforeNode {

        public RealReactionBeforeNode( RealReactionModel model ) {
            super( RPALStrings.LABEL_BEFORE_REACTION, model.getReaction(), model.getQuantityRange(), true /* showSubstanceNames */, new GridLayoutStrategy() );
        }
    }
    
    private static class RealReactionAfterNode extends AbstractAfterNode {

        public RealReactionAfterNode( RealReactionModel model ) {
            super( RPALStrings.LABEL_AFTER_REACTION, model.getReaction(), model.getQuantityRange(), true /* showSubstanceNames */, new GridLayoutStrategy() );
        }
    }
}
