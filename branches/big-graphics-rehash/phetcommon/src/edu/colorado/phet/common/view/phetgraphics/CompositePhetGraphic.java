/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;
import java.util.Iterator;

/**
 * A list of PhetGraphics, painted in order.
 */
public class CompositePhetGraphic extends GraphicLayerSet {

    public CompositePhetGraphic( Component component ) {
        super( component );
    }

    protected PhetGraphic getHandler( Point p ) {
        if( contains( p.x, p.y ) ) {
            return this;
        }
        else {
            return null;
        }
    }

    public void setLocation( int x, int y ) {
        Point location = super.getLocation();
        int dx = x - location.x;
        int dy = y - location.y;
        location.setLocation( x, y );
        Iterator it = this.iterator();
        while( it.hasNext() ) {
            PhetGraphic graphic = (PhetGraphic)it.next();
            graphic.setLocation( graphic.getLocation().x + dx, graphic.getLocation().y + dy );
        }
    }

}