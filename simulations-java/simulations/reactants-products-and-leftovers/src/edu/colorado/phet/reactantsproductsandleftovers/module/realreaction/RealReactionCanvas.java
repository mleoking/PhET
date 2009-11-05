
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.geom.Dimension2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ReactionChoiceNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.*;

/**
 * Canvas for the "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionCanvas extends RPALCanvas {

    private final RealReactionModel model;
    private final ReactionChoiceNode reactionChoiceNode;
    private final RPALArrowNode arrowNode;
    
    private RealReactionFormulaNode formulaNode;
    private RealReactionBeforeNode beforeNode;
    private RealReactionAfterNode afterNode;
    
    public RealReactionCanvas( final RealReactionModel model ) {
        super();
        
        this.model = model;
        model.addChangeListeners( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateNodes();
            }
        });
        
        reactionChoiceNode = new ReactionChoiceNode( model );
        reactionChoiceNode.scale( 1.25 );
        addChild( reactionChoiceNode );
        
        arrowNode = new RPALArrowNode();
        addChild( arrowNode );
        
        updateNodes();
    }
    

    private void updateNodes() {
        
        if ( formulaNode != null ) {
            removeChild( formulaNode );
        }
        formulaNode = new RealReactionFormulaNode( model.getReaction() );
        formulaNode.scale( 3 ); //XXX
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
        
        updateLayout();
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

        // radio buttons at upper left
        double x = 0;
        double y = 0;
        reactionChoiceNode.setOffset( x, y );
        
        // formula to right of radio buttons
        x = reactionChoiceNode.getFullBoundsReference().getWidth() + 30;
        y = reactionChoiceNode.getFullBoundsReference().getCenterY() - ( formulaNode.getFullBoundsReference().getHeight() / 2 );
        formulaNode.setOffset( x, y );
        
        // Before
        x = reactionChoiceNode.getFullBoundsReference().getMinX();
        y = reactionChoiceNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );
        
        // arrow
        x = beforeNode.getFullBoundsReference().getMaxX() + 20;
        y = beforeNode.getYOffset() + 150;
        arrowNode.setOffset( x, y );
        
        // After
        x = arrowNode.getFullBoundsReference().getMaxX() + 20;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );
        
        centerRootNode();
    }
}
