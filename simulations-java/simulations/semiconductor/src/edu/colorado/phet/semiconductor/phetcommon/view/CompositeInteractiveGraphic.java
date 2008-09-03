/**
 *
 * Class: CompositeInteractiveGraphic
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view;

import edu.colorado.phet.semiconductor.phetcommon.util.MultiMap;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.InteractiveGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.bounds.Boundary;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.MouseManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;

public class CompositeInteractiveGraphic implements InteractiveGraphic {

    private MultiMap graphicMap;
    private HashMap graphicTxMap = new HashMap();
    private MouseManager mouseManager;

    public CompositeInteractiveGraphic() {
        graphicMap = new MultiMap();
        mouseManager = new MouseManager( graphicMap );
    }

    public void paint( Graphics2D g ) {
        Iterator it = graphicMap.iterator();
        while( it.hasNext() ) {
            Graphic graphic = (Graphic)it.next();
            AffineTransform orgTx = g.getTransform();
            // If there is an affine transform bound to this graphic, updateFrames
            // it to the graphics object
            AffineTransform atx = (AffineTransform)graphicTxMap.get( graphic );
            if( atx != null ) {
                g.transform( atx );
            }
//            RevertableGraphicsSetup setup = (RevertableGraphicsSetup)graphicSetupMap.get( graphic );
//            if( setup != null ) {
//                setup.setup( g );
//            }
            graphic.paint( g );
            g.setTransform( orgTx );
//            if( setup != null ) {
//                setup.revert( g );
//            }
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

    public MouseManager getMouseManager() {
        return mouseManager;
    }

    public void removeGraphic( Graphic g ) {
        graphicMap.removeValue( g );
    }
}
