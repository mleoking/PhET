/** University of Colorado, PhET*/
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;

/**
 * A list of PhetGraphics, painted in order.
 * The CompositePhetGraphic manages interactivity for all its children.
 */
public class CompositePhetGraphic extends GraphicLayerSet {

    public CompositePhetGraphic( Component component ) {
        super( component );
    }

    /**
     * The CompositePhetGraphic manages interactivity for all its children.
     *
     * @param p The specified point.
     * @return The PhetGraphic responsible for handling the event.
     */
    protected PhetGraphic getHandler( Point p ) {
        if( contains( p.x, p.y ) ) {
            return this;
        }
        else {
            return null;
        }
    }

}