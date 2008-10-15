/**
 *
 * Class: CompositeGraphic
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.radiowaves.common_1200.graphics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.util.MultiMap;

public class CompositeGraphic implements BoundedGraphic {

    private MultiMap graphicMap = new MultiMap();
    private boolean visible = true;

    public void paint( Graphics2D g ) {
        if( visible ) {
            Iterator it = graphicMap.iterator();
            while( it.hasNext() ) {
                Graphic graphic = (Graphic)it.next();
                graphic.paint( g );
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    /**
     * Used to see if the mouse is in component InteractiveGraphic
     *
     * @param x
     * @param y
     * @return true if this graphic contains the specified point.
     */
    public boolean contains( int x, int y ) {
        if( visible ) {
            Iterator it = this.graphicMap.iterator();
            while( it.hasNext() ) {
                Object o = it.next();
                if( o instanceof Boundary ) {
                    Boundary boundary = (Boundary)o;
                    if( boundary.contains( x, y ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void clear() {
        graphicMap.clear();
    }

    public void removeGraphic( Graphic graphic ) {
        graphicMap.removeValue( graphic );
    }

    public void addGraphic( Graphic graphic ) {
        addGraphic( graphic, 0 );
    }

    /**
     * Returns graphics from a forward iterator.
     */
    public Graphic[] getGraphics() {
        Iterator it = graphicMap.iterator();
        ArrayList graphics = new ArrayList();
        while( it.hasNext() ) {
            Graphic graphic = (Graphic)it.next();
            graphics.add( graphic );
        }
        return (Graphic[])graphics.toArray( new Graphic[0] );
    }

    public void addGraphic( Graphic graphic, double layer ) {
        this.graphicMap.put( new Double( layer ), graphic );
    }

    public void moveToTop( Graphic target ) {
        this.removeGraphic( target );
        graphicMap.put( graphicMap.lastKey(), target );
    }

}
