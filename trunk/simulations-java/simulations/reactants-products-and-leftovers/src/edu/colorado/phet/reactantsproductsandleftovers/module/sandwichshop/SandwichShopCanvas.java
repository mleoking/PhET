
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichFormulaNode;


public class SandwichShopCanvas extends RPALCanvas {
    
    private final SandwichFormulaNode sandwichFormulaNode;

    public SandwichShopCanvas( SandwichShop model ) {
        super();
        
        sandwichFormulaNode = new SandwichFormulaNode( model.getSandwichFormula() );
        addChild( sandwichFormulaNode );
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

        sandwichFormulaNode.setOffset( 0, 10 );
        
        centerRootNode();
    }
}
