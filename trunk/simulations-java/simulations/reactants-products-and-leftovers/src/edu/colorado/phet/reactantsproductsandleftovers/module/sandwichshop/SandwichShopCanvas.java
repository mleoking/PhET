
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichShopBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichFormulaNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.TestSandwichModelNode;


public class SandwichShopCanvas extends RPALCanvas {
    
    private final SandwichFormulaNode formulaNode;
    private final SandwichShopBeforeNode beforeNode;
    private final TestSandwichModelNode testNode;

    public SandwichShopCanvas( SandwichShop model ) {
        super();
        
        formulaNode = new SandwichFormulaNode( model.getSandwichFormula() );
        addChild( formulaNode );
        
        beforeNode = new SandwichShopBeforeNode( model );
        addChild( beforeNode );
        
        testNode = new TestSandwichModelNode( model );
        testNode.scale( 2 );
        addChild( testNode );
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

        formulaNode.setOffset( 0, 10 );
        beforeNode.setOffset( 0, formulaNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30 );
        
        testNode.setOffset( 500, formulaNode.getFullBoundsReference().getMaxY() + 60 );
        
        centerRootNode();
    }
}
