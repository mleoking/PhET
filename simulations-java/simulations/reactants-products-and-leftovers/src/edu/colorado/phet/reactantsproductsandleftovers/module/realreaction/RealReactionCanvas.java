/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionEquationNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Real Reaction" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionCanvas extends RPALCanvas {

    private final RealReactionModel model;
    
    // these nodes are final, allocated once
    private final ReactionChoiceNode reactionChoiceNode;
    private final RightArrowNode arrowNode;

    // these nodes are mutable, allocated when reaction changes
    private RealReactionEquationNode equationNode;
    private RealReactionBeforeNode beforeNode;
    private RealReactionAfterNode afterNode;
    private PSwing resetAllButtonWrapper;

    public RealReactionCanvas( final RealReactionModel model, Resettable resettable ) {
        super();

        this.model = model;

        reactionChoiceNode = new ReactionChoiceNode( model );
        reactionChoiceNode.scale( 1.25 );
        addChild( reactionChoiceNode );

        arrowNode = new RightArrowNode();
        addChild( arrowNode );
        
        ResetAllButton resetAllButton = new ResetAllButton( resettable, this );
        resetAllButtonWrapper = new PSwing( resetAllButton );
        resetAllButtonWrapper.scale( 1.25 );
        addChild( resetAllButtonWrapper );
        
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateDynamicNodes();
            }
        } );

        updateDynamicNodes();
    }

    /*
     * Updates nodes that are "dynamic".
     * Dynamic nodes are replaced when the reaction changes.
     */
    private void updateDynamicNodes() {

        removeChild( equationNode );
        equationNode = new RealReactionEquationNode( model.getReaction() );
        addChild( equationNode );

        removeChild( beforeNode );
        beforeNode = new RealReactionBeforeNode( model );
        addChild( beforeNode );

        removeChild( afterNode );
        afterNode = new RealReactionAfterNode( model );
        addChild( afterNode );

        updateNodesLayout();
    }

    /*
     * Updates the layout of all nodes.
     */
    private void updateNodesLayout() {

        // radio buttons at upper left
        double x = 0;
        double y = 0;
        reactionChoiceNode.setOffset( x, y );

        // equation to right of radio buttons, vertically centered with buttons
        x = reactionChoiceNode.getFullBoundsReference().getWidth() + 30;
        y = reactionChoiceNode.getFullBoundsReference().getCenterY() - ( equationNode.getFullBoundsReference().getHeight() / 2 );
        equationNode.setOffset( x, y );

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
        
        // Reset All button at bottom center, cheated toward Before box
        x = arrowNode.getFullBoundsReference().getMaxX() - resetAllButtonWrapper.getFullBoundsReference().getWidth();
        y = afterNode.getFullBoundsReference().getMaxY();
        resetAllButtonWrapper.setOffset( x, y );
    }
}
