/**
 *
 * Class: CompositeInteractiveGraphic
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.mousecontrols.MouseManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;

public class CompositeInteractiveGraphic implements InteractiveGraphic {

    private MultiMap graphicMap;
    private HashMap graphicTxMap = new HashMap();
    private HashMap graphicSetupMap = new HashMap();
    private MouseManager mouseManager;

    public CompositeInteractiveGraphic() {
        graphicMap = new MultiMap();
        mouseManager = new MouseManager( graphicMap );
    }

    public MultiMap getGraphicMap() {
        return graphicMap;
    }

    public void paint( Graphics2D g ) {
        Iterator it = graphicMap.iterator();
        while( it.hasNext() ) {
            Graphic graphic = (Graphic)it.next();
            AffineTransform orgTx = g.getTransform();
            // If there is an affine transform bound to this graphic, apply
            // it to the graphics object
            AffineTransform atx = (AffineTransform)graphicTxMap.get( graphic );
            if( atx != null ) {
                g.transform( atx );
            }
            RevertableGraphicsSetup setup = (RevertableGraphicsSetup)graphicSetupMap.get( graphic );
            if( setup != null ) {
                setup.setup( g );
            }
            graphic.paint( g );
            g.setTransform( orgTx );
            if( setup != null ) {
                setup.revert( g );
            }
        }
    }

    public boolean contains( int x, int y ) {
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
        return false;
    }

    public void clear() {
        graphicMap.clear();
    }

    public void remove( Graphic graphic ) {
        graphicMap.removeValue( graphic );
        graphicTxMap.remove( graphic );
        graphicSetupMap.remove( graphic );
    }

    // Mouse-related behaviors
    public void mouseClicked( MouseEvent e ) {
        mouseManager.mouseClicked( e );
    }

    public void mousePressed( MouseEvent e ) {
        mouseManager.mousePressed( e );
    }

    public void mouseReleased( MouseEvent e ) {
        mouseManager.mouseReleased( e );
    }

    public void mouseEntered( MouseEvent e ) {
        mouseManager.mouseEntered( e );
    }

    public void mouseExited( MouseEvent e ) {
        mouseManager.mouseExited( e );
    }

    public void mouseDragged( MouseEvent e ) {
        mouseManager.mouseDragged( e );
    }

    public void mouseMoved( MouseEvent e ) {
        mouseManager.mouseMoved( e );
    }

    public void addGraphic( Graphic graphic ) {
        addGraphic( graphic, 0 );
    }

    public void addGraphic( Graphic graphic, double layer ) {
        this.graphicMap.add( new Double( layer ), graphic );
    }

    /**
     * Alternative to a large many to many composite interactive graphic implementation.
     * <p/>
     * We will also need to handle transforming mouse events, etc...
     */
    public void addGraphic2( Graphic graphic, double layer, AffineTransform atx, RevertableGraphicsSetup graphicSetup ) {
        addGraphic( new RevertibleGraphic( graphic, atx, graphicSetup ), layer );
        /**Doing this keeps the mappings 1-1, not many to many, and keeps the paint method here nice and small, as it should be.*/
//        this.graphicMap.add( new Double( layer ), graphic );
//        this.graphicTxMap.put( graphic, atx );
//        this.graphicSetupMap.put( graphic, graphicSetup );
    }

    public void addGraphic( Graphic graphic, double layer,
                            AffineTransform atx, RevertableGraphicsSetup graphicSetup ) {
        this.graphicMap.add( new Double( layer ), graphic );
        this.graphicTxMap.put( graphic, atx );
        this.graphicSetupMap.put( graphic, graphicSetup );
    }

    public void moveToTop( Graphic target ) {
        this.remove( target );
        graphicMap.add( graphicMap.lastKey(), target );
    }

    public MouseManager getMouseManager() {
        return mouseManager;
    }

}
