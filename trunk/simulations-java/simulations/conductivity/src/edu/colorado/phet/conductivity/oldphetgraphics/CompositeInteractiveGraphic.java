/**
 *
 * Class: CompositeInteractiveGraphic
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.conductivity.oldphetgraphics;

import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;
import edu.colorado.phet.conductivity.oldphetgraphics.InteractiveGraphic;
import edu.colorado.phet.conductivity.oldphetgraphics.Boundary;
import edu.colorado.phet.common.phetcommon.util.MultiMap;
import edu.colorado.phet.conductivity.oldphetgraphics.MouseManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;

public class CompositeInteractiveGraphic implements InteractiveGraphic {

    MultiMap graphicMap;
    MouseManager mouseManager;

    public CompositeInteractiveGraphic() {
        graphicMap = new MultiMap();
        mouseManager = new MouseManager( graphicMap );
    }

    public void paint( Graphics2D g ) {
        Iterator it = graphicMap.iterator();
        while( it.hasNext() ) {
            Graphic graphic = (Graphic)it.next();
            graphic.paint( g );
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

    public void remove( Graphic graphic ) {
        graphicMap.removeValue( graphic );
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

    public void addGraphic( Graphic graphic, double layer ) {
        this.graphicMap.put( new Double( layer ), graphic );
    }

}
