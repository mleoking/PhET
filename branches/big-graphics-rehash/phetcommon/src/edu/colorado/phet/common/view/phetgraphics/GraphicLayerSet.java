/**
 *
 * Class: CompositeGraphic
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.view.graphics.Boundary;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class GraphicLayerSet extends PhetGraphic {

    private MultiMap graphicMap = new MultiMap();

    public GraphicLayerSet( Component component ) {
        super( component );
    }

    public PhetGraphic[] getChildrenForMouseHandling() {
        return getGraphics();
    }


    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            Iterator it = graphicMap.iterator();
            while( it.hasNext() ) {
                PhetGraphic graphic = (PhetGraphic)it.next();
                graphic.paint( g );
            }
        }
    }

    /**
     * Used to see if the mouse is in component InteractiveGraphic
     *
     * @param x
     * @param y
     * @return true if this graphic contains the specified point.
     */
    public boolean contains( int x, int y ) {
        if( isVisible() ) {
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

    protected Rectangle determineBounds() {
        return null;
    }

    public void clear() {
        graphicMap.clear();
    }

    public void removeGraphic( PhetGraphic graphic ) {
        graphicMap.removeValue( graphic );
    }

    protected void forceRepaint() {
        if( isVisible() ) {
            Iterator it = graphicMap.iterator();
            while( it.hasNext() ) {
                PhetGraphic graphic = (PhetGraphic)it.next();
                graphic.forceRepaint();
            }
        }
    }

    public void addGraphic( PhetGraphic graphic ) {
        addGraphic( graphic, 0 );
    }

    /**
     * Returns graphics from a forward iterator.
     */
    public PhetGraphic[] getGraphics() {
        Iterator it = graphicMap.iterator();
        ArrayList graphics = new ArrayList();
        while( it.hasNext() ) {
            PhetGraphic graphic = (PhetGraphic)it.next();
            graphics.add( graphic );
        }
        return (PhetGraphic[])graphics.toArray( new PhetGraphic[0] );
    }

    public void addGraphic( PhetGraphic graphic, double layer ) {
        this.graphicMap.add( new Double( layer ), graphic );
    }

    public void moveToTop( PhetGraphic target ) {
        this.removeGraphic( target );
        graphicMap.add( graphicMap.lastKey(), target );
    }

    protected Iterator iterator() {
        return this.graphicMap.iterator();
    }
}
