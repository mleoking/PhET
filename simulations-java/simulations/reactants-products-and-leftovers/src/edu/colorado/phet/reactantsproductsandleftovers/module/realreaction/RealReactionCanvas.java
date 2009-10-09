
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;


public class RealReactionCanvas extends RPALCanvas {

    public RealReactionCanvas() {
        super();
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

        //XXX lay out nodes
        
        centerRootNode();
    }
}
