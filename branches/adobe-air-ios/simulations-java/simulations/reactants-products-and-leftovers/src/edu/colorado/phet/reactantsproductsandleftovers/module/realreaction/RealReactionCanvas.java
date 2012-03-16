// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.realreaction.RealReactionEquationNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Real Reaction" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionCanvas extends RPALCanvas {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;

    private final RealReactionModel model;
    
    // these nodes are final, allocated once
    private final ReactionChoiceNode reactionChoiceNode;
    private final RightArrowNode arrowNode;
    private final PSwing resetAllButtonWrapper;

    // these nodes are mutable, allocated when reaction changes
    private RealReactionEquationNode equationNode;
    private RealReactionBeforeNode beforeNode;
    private RealReactionAfterNode afterNode;

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

        if ( equationNode != null ) {
            removeChild( equationNode );
            equationNode.cleanup();
        }
        equationNode = new RealReactionEquationNode( model.getReaction() );
        addChild( equationNode );

        if ( beforeNode != null ) {
            removeChild( beforeNode );
            beforeNode.cleanup();
        }
        beforeNode = new RealReactionBeforeNode( model, BOX_SIZE );
        addChild( beforeNode );

        if ( afterNode != null ) {
            removeChild( afterNode );
            afterNode.cleanup();
        }
        afterNode = new RealReactionAfterNode( model, BOX_SIZE );
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
        y = beforeNode.getYOffset() + ( BOX_SIZE.getHeight() / 2 );
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
