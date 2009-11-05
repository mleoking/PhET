
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.view.*;

/**
 * Canvas for the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopCanvas extends RPALCanvas {
    
    public SandwichShopCanvas( SandwichShopModel model ) {
        super();
        
        SandwichFormulaNode formulaNode = new SandwichFormulaNode( model );
        addChild( formulaNode );
        
        SandwichShopBeforeNode beforeNode = new SandwichShopBeforeNode( model );
        addChild( beforeNode );
        
        RightArrowNode arrowNode = new RightArrowNode();
        addChild( arrowNode );
        
        SandwichShopAfterNode afterNode = new SandwichShopAfterNode( model );
        addChild( afterNode );
        
        // layout of this module is static, so do it here...
        
        // formula at upper left
        double x = 0;
        double y = 0;
        formulaNode.setOffset( x, y );
        
        // Before box below formula, left justified
        x = formulaNode.getFullBoundsReference().getMinX();
        y = formulaNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );
        
        // arrow to the right of Before box, vertically centered with box
        x = beforeNode.getFullBoundsReference().getMaxX() + 20;
        y = beforeNode.getYOffset() + ( beforeNode.getBoxHeight() / 2 );
        arrowNode.setOffset( x, y );
        
        // After box to the right of arrow, top aligned with Before box
        x = arrowNode.getFullBoundsReference().getMaxX() + 20;
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
    
    private static class SandwichShopAfterNode extends AbstractAfterNode {

        public SandwichShopAfterNode( SandwichShopModel model ) {
            super( RPALStrings.LABEL_AFTER_SANDWICH, model.getReaction(), model.getQuantityRange(), false /* showSubstanceName */ );
        }
    }
    
    private static class SandwichShopBeforeNode extends AbstractBeforeNode {

        public SandwichShopBeforeNode( SandwichShopModel model ) {
            super( RPALStrings.LABEL_BEFORE_SANDWICH, model.getReaction(), model.getQuantityRange(), false /* showSubstanceNames */ );
        }
    }
}
