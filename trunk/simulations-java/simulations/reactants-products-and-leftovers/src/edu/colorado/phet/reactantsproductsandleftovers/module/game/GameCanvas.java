
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends RPALCanvas {
    
    public GameCanvas( GameModel model, Resettable resettable ) {
        super();
        
        //XXX
        PText underConstructionNode = new PText( "Under Construction" );
        underConstructionNode.setFont( new PhetFont( 36 ) );
        addChild( underConstructionNode );
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
}
