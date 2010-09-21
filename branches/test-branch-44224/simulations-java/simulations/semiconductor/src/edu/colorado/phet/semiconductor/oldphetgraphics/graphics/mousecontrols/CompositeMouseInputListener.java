/*, 2003.*/
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics.mousecontrols;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.event.MouseInputListener;

/**
 * User: Sam Reid
 * Date: Nov 3, 2003
 * Time: 3:05:32 AM
 */
public class CompositeMouseInputListener implements MouseInputListener {
    ArrayList list = new ArrayList();

    public void addMouseInputListener( MouseInputListener mil ) {
        list.add( mil );
    }

    public void mouseClicked( MouseEvent e ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MouseInputListener mouseInputListener = (MouseInputListener) list.get( i );
            mouseInputListener.mouseClicked( e );
        }
    }

    public void mousePressed( MouseEvent e ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MouseInputListener mouseInputListener = (MouseInputListener) list.get( i );
            mouseInputListener.mousePressed( e );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MouseInputListener mouseInputListener = (MouseInputListener) list.get( i );
            mouseInputListener.mouseReleased( e );
        }
    }

    public void mouseEntered( MouseEvent e ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MouseInputListener mouseInputListener = (MouseInputListener) list.get( i );
            mouseInputListener.mouseEntered( e );
        }
    }

    public void mouseExited( MouseEvent e ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MouseInputListener mouseInputListener = (MouseInputListener) list.get( i );
            mouseInputListener.mouseExited( e );
        }
    }

    public void mouseDragged( MouseEvent e ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MouseInputListener mouseInputListener = (MouseInputListener) list.get( i );
            mouseInputListener.mouseDragged( e );
        }
    }

    public void mouseMoved( MouseEvent e ) {
        for ( int i = 0; i < list.size(); i++ ) {
            MouseInputListener mouseInputListener = (MouseInputListener) list.get( i );
            mouseInputListener.mouseMoved( e );
        }
    }

}
