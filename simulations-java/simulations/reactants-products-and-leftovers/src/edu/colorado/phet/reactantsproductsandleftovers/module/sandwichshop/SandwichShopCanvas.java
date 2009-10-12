
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichShopBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichFormulaNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.LeftoversDisplayNode;


public class SandwichShopCanvas extends RPALCanvas {
    
    private final SandwichFormulaNode formulaNode;
    private final SandwichShopBeforeNode beforeNode;
    private final LeftoversDisplayNode leftoversNode;

    public SandwichShopCanvas( SandwichShop model ) {
        super();
        
        formulaNode = new SandwichFormulaNode( model.getSandwichFormula() );
        addChild( formulaNode );
        
        beforeNode = new SandwichShopBeforeNode( model );
        addChild( beforeNode );
        
        leftoversNode = new LeftoversDisplayNode( model );
        addChild( leftoversNode );
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
        else if ( RPALConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "SandwichShopCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        double x = 0;
        double y = 0;
        formulaNode.setOffset( x, y );
        x = formulaNode.getFullBoundsReference().getMinX();
        y = formulaNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );
        x = worldSize.getWidth() - leftoversNode.getFullBoundsReference().getWidth() - 30;
        y = worldSize.getHeight() - leftoversNode.getFullBoundsReference().getHeight() - 30;
        leftoversNode.setOffset( x, y );
        
        centerRootNode();
    }
}
