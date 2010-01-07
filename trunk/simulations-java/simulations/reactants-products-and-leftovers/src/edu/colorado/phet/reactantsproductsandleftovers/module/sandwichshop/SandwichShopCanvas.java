
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.ResetAllButton;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.RightArrowNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichEquationNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichShopAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichShopBeforeNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopCanvas extends RPALCanvas {
    
    public SandwichShopCanvas( SandwichShopModel model, Resettable resettable ) {
        super();
        
        SandwichEquationNode equationNode = new SandwichEquationNode( model );
        addChild( equationNode );
        
        SandwichShopBeforeNode beforeNode = new SandwichShopBeforeNode( model );
        addChild( beforeNode );
        
        RightArrowNode arrowNode = new RightArrowNode();
        addChild( arrowNode );
        
        SandwichShopAfterNode afterNode = new SandwichShopAfterNode( model );
        addChild( afterNode );
        
        ResetAllButton resetAllButton = new ResetAllButton( resettable, this );
        PSwing resetAllButtonWrapper = new PSwing( resetAllButton );
        resetAllButtonWrapper.scale( 1.25 );
        addChild( resetAllButtonWrapper );
        
        // layout of this module is static, so do it here...
        
        // equation at upper left
        double x = 0;
        double y = 0;
        equationNode.setOffset( x, y );
        
        // Before box below equation, left justified
        x = equationNode.getFullBoundsReference().getMinX();
        y = equationNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
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
